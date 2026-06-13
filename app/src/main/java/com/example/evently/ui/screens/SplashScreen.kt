package com.example.evently.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.evently.navigation.Screen
import kotlinx.coroutines.delay
import kotlin.math.sin
import kotlin.random.Random

private data class Particle(
    val x: Float,
    val y: Float,
    val radius: Float,
    val alpha: Float,
    val speed: Float,
    val offsetX: Float
)

@Composable
fun SplashScreen(
    navController: NavController,
    hasCompletedOnboarding: Boolean,
    isLoggedIn: Boolean
) {
    // Logo entrance animation
    val scaleAnim = remember { Animatable(0f) }
    val alphaAnim = remember { Animatable(0f) }
    val rotateAnim = remember { Animatable(-15f) }
    val subtitleAlpha = remember { Animatable(0f) }

    // Particle float animation
    val particleAnim = rememberInfiniteTransition(label = "particles")
    val particleOffset by particleAnim.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particleOffset"
    )

    // Glow pulse for logo
    val glowPulse by particleAnim.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowPulse"
    )

    // Stable random particles seeded so they don't re-randomize on recompose
    val particles = remember {
        List(30) {
            Particle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                radius = Random.nextFloat() * 5f + 2f,
                alpha = Random.nextFloat() * 0.4f + 0.1f,
                speed = Random.nextFloat() * 0.3f + 0.1f,
                offsetX = Random.nextFloat() * 0.1f - 0.05f
            )
        }
    }

    LaunchedEffect(key1 = true) {
        // Logo bounces in
        scaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
        rotateAnim.animateTo(
            targetValue = 0f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        alphaAnim.animateTo(1f, animationSpec = tween(600))
        subtitleAlpha.animateTo(1f, animationSpec = tween(800))
        delay(1400)

        if (isLoggedIn) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
        } else if (!hasCompletedOnboarding) {
            navController.navigate(Screen.Onboarding.route) {
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
        } else {
            navController.navigate(Screen.Login.route) {
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
        }
    }

    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        primary.copy(alpha = 0.15f),
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.background
                    ),
                    radius = 1200f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Floating particles Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            particles.forEach { p ->
                val animY = ((p.y - particleOffset * p.speed + 1f) % 1f)
                val animX = p.x + sin(particleOffset * Math.PI * 2 * p.speed).toFloat() * p.offsetX
                drawCircle(
                    color = primary.copy(alpha = p.alpha * glowPulse),
                    radius = p.radius,
                    center = Offset(animX * w, animY * h)
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.alpha(alphaAnim.value)
        ) {
            // Glowing logo container
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(scaleAnim.value)
                    .rotate(rotateAnim.value),
                contentAlignment = Alignment.Center
            ) {
                // Outer glow ring
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    primary.copy(alpha = 0.3f * glowPulse),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )
                // Inner icon background
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(primary, secondary.copy(alpha = 0.8f))
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ConfirmationNumber,
                        contentDescription = "App Logo",
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Evently",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 2.sp
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Discover Amazing Experiences",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f),
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.5.sp
                ),
                modifier = Modifier.alpha(subtitleAlpha.value)
            )

            Spacer(modifier = Modifier.height(80.dp))

            // Pulsing dots loader instead of boring spinner
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.alpha(subtitleAlpha.value)
            ) {
                repeat(3) { i ->
                    val dotAnim by particleAnim.animateFloat(
                        initialValue = 0.3f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(600, delayMillis = i * 200, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "dot$i"
                    )
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .scale(dotAnim)
                            .background(
                                color = primary.copy(alpha = dotAnim),
                                shape = CircleShape
                            )
                    )
                }
            }
        }
    }
}
