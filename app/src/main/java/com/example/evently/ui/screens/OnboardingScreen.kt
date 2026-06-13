package com.example.evently.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.evently.R
import com.example.evently.navigation.Screen
import com.example.evently.viewmodel.EventlyViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(navController: NavController, viewModel: EventlyViewModel) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()

    val onboardingPages = listOf(
        OnboardingPageData(
            title = "Discover Events\nNear You",
            description = "Find concerts, comedy shows, workshops, sports events, and more tailored to your interests.",
            illustrationRes = R.drawable.onboarding_discover
        ),
        OnboardingPageData(
            title = "Book Tickets\nInstantly",
            description = "Simple, seamless, and secure booking process with instant electronic ticket confirmation.",
            illustrationRes = R.drawable.onboarding_book
        ),
        OnboardingPageData(
            title = "Enjoy Amazing\nExperiences",
            description = "Attend premium events, check-in using QR codes, and create unforgettable memories.",
            illustrationRes = R.drawable.onboarding_enjoy
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
            .padding(24.dp)
    ) {
        // Skip Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            if (pagerState.currentPage < 2) {
                TextButton(
                    onClick = {
                        viewModel.completeOnboarding()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                ) {
                    Text(
                        text = "Skip",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(48.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Pager for swipeable screens
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { pageIndex ->
            val page = onboardingPages[pageIndex]
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                // Premium Onboarding Illustration
                Image(
                    painter = painterResource(id = page.illustrationRes),
                    contentDescription = page.title,
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(260.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .border(
                            width = 1.5.dp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(24.dp)
                        ),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = page.title,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 34.sp
                    ),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = page.description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        lineHeight = 22.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        // Indicators & Buttons Bottom Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pager dots indicators
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(3) { index ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(if (isSelected) 24.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                color = if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)
                            )
                    )
                }
            }

            // Next / Get Started Button
            Button(
                onClick = {
                    if (pagerState.currentPage < 2) {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    } else {
                        viewModel.completeOnboarding()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(
                    text = if (pagerState.currentPage == 2) "Get Started" else "Next",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }
        }
    }
}

data class OnboardingPageData(
    val title: String,
    val description: String,
    val illustrationRes: Int
)
