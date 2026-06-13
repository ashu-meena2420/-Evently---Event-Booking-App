package com.example.evently.ui.screens

import android.content.Intent
import android.provider.CalendarContract
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.evently.navigation.Screen
import com.example.evently.theme.*
import com.example.evently.ui.utils.TicketExporter
import com.example.evently.viewmodel.EventlyViewModel
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// Confetti particle data
private data class ConfettiParticle(
    val x: Float,
    val y: Float,
    val rotation: Float,
    val color: Color,
    val width: Float,
    val height: Float,
    val velocityX: Float,
    val velocityY: Float,
    val rotationSpeed: Float
)

@Composable
fun BookingSuccessScreen(
    navController: NavController,
    bookingId: String,
    viewModel: EventlyViewModel
) {
    val context = LocalContext.current
    val bookings by viewModel.bookings.collectAsState()
    val activeBooking = bookings.find { it.id == bookingId }

    if (activeBooking == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Booking not found.")
        }
        return
    }

    val (gradStart, gradEnd) = categoryGradient(activeBooking.event.category)

    // ── Entrance animations ────────────────────────────────────────────────────
    val scaleAnim = remember { Animatable(0f) }
    val cardSlideAnim = remember { Animatable(100f) }
    val cardAlphaAnim = remember { Animatable(0f) }

    // Shimmer sweep
    val shimmerInfinite = rememberInfiniteTransition(label = "shimmer")
    val shimmerOffset by shimmerInfinite.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(tween(2200, easing = LinearEasing), RepeatMode.Restart),
        label = "shimmerX"
    )

    // Confetti animation
    val confettiProgress by shimmerInfinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing), RepeatMode.Restart),
        label = "confettiProgress"
    )

    val confettiColors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary,
        LightYellow,
        SuccessColor,
        Color(0xFF60A5FA),
        Color(0xFFF472B6)
    )

    val particles = remember {
        List(60) {
            ConfettiParticle(
                x = Random.nextFloat(),
                y = Random.nextFloat() * 0.6f - 0.1f,
                rotation = Random.nextFloat() * 360f,
                color = confettiColors.random(),
                width = Random.nextFloat() * 10f + 5f,
                height = Random.nextFloat() * 6f + 3f,
                velocityX = Random.nextFloat() * 0.2f - 0.1f,
                velocityY = Random.nextFloat() * 0.5f + 0.3f,
                rotationSpeed = Random.nextFloat() * 360f - 180f
            )
        }
    }

    LaunchedEffect(true) {
        scaleAnim.animateTo(1f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow))
        cardSlideAnim.animateTo(0f, tween(500, easing = FastOutSlowInEasing))
        cardAlphaAnim.animateTo(1f, tween(500))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // ── Confetti Canvas (behind content) ──────────────────────────────────
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            particles.forEach { p ->
                val animY = (p.y + confettiProgress * p.velocityY) % 1.2f
                val animX = p.x + sin(confettiProgress * Math.PI * 2).toFloat() * p.velocityX
                val animRot = p.rotation + confettiProgress * p.rotationSpeed
                if (animY < 1f) {
                    rotate(animRot, pivot = Offset(animX * w, animY * h)) {
                        drawRect(
                            color = p.color.copy(alpha = (1f - animY * 0.8f).coerceIn(0f, 0.9f)),
                            topLeft = Offset(animX * w - p.width / 2f, animY * h - p.height / 2f),
                            size = Size(p.width, p.height)
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .safeDrawingPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // ── Success check circle ───────────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .scale(scaleAnim.value),
                contentAlignment = Alignment.Center
            ) {
                // Outer glow
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .background(SuccessColor.copy(0.15f), CircleShape)
                )
                // Middle ring
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .background(SuccessColor.copy(0.25f), CircleShape)
                )
                // Core circle
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .background(SuccessColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(36.dp))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Booking Confirmed! 🎉",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Your ticket is ready to use",
                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground.copy(0.5f))
            )

            Spacer(modifier = Modifier.height(28.dp))

            // ── Premium Animated Ticket Card ──────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = cardSlideAnim.value.dp)
                    .graphicsLayer(alpha = cardAlphaAnim.value)
            ) {
                PremiumTicketCard(
                    activeBooking = activeBooking,
                    gradStart = gradStart,
                    gradEnd = gradEnd,
                    shimmerOffset = shimmerOffset
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Action Buttons ────────────────────────────────────────────────
            // Add to Calendar
            Button(
                onClick = {
                    val calIntent = Intent(Intent.ACTION_INSERT).apply {
                        data = CalendarContract.Events.CONTENT_URI
                        putExtra(CalendarContract.Events.TITLE, activeBooking.event.name)
                        putExtra(CalendarContract.Events.EVENT_LOCATION, activeBooking.event.venue)
                        putExtra(
                            CalendarContract.Events.DESCRIPTION,
                            "Evently Booking ID: ${activeBooking.id}\nTicket: ${activeBooking.ticketType} x${activeBooking.quantity}"
                        )
                        putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false)
                    }
                    try { context.startActivity(calIntent) } catch (e: Exception) { /* no calendar app */ }
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(listOf(gradStart, gradEnd)),
                            RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CalendarMonth, null, tint = Color.White, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Add to Calendar", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold), color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Download & Share row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = { TicketExporter.exportTicketToDownloads(context, activeBooking) },
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(0.5f))
                ) {
                    Icon(Icons.Default.Download, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Download", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
                }

                OutlinedButton(
                    onClick = {
                        val shareText = "Hey! I booked tickets for ${activeBooking.event.name}!\n" +
                                "📅 ${activeBooking.event.date} at ${activeBooking.event.time}\n" +
                                "📍 ${activeBooking.event.venue}\n" +
                                "🎟 ${activeBooking.ticketType} × ${activeBooking.quantity}\n" +
                                "Booking ID: ${activeBooking.id}\nSee you there! 🎉"
                        context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, shareText)
                        }, null))
                    },
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(0.5f))
                ) {
                    Icon(Icons.Default.Share, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Share", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    viewModel.clearLastCreatedBooking()
                    navController.navigate(Screen.Home.route) { popUpTo(Screen.Home.route) { inclusive = true } }
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onBackground.copy(0.08f))
            ) {
                Text("Back to Home", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground))
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ── Premium Ticket Card ────────────────────────────────────────────────────────
@Composable
fun PremiumTicketCard(
    activeBooking: com.example.evently.model.Booking,
    gradStart: Color,
    gradEnd: Color,
    shimmerOffset: Float
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(16.dp, RoundedCornerShape(24.dp))
    ) {
        // Ticket body
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
        ) {
            // ── Top section (gradient header) ─────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.linearGradient(listOf(gradStart, gradEnd)))
                    .padding(20.dp)
            ) {
                // Shimmer sweep overlay
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.White.copy(alpha = 0.12f),
                                    Color.Transparent
                                ),
                                start = Offset(shimmerOffset * 800f - 200f, 0f),
                                end = Offset(shimmerOffset * 800f + 200f, 300f)
                            )
                        )
                )

                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "E V E N T L Y",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White.copy(0.7f),
                                    letterSpacing = 3.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = activeBooking.event.name,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            )
                        }
                        // QR stub preview
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White.copy(0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.QrCode, null, tint = Color.White, modifier = Modifier.size(36.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                        Column {
                            Text("Date", style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(0.6f), fontWeight = FontWeight.Bold))
                            Text(activeBooking.event.date.substringBefore(","), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.ExtraBold, color = Color.White))
                        }
                        Column {
                            Text("Time", style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(0.6f), fontWeight = FontWeight.Bold))
                            Text(activeBooking.event.time, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.ExtraBold, color = Color.White))
                        }
                        Column {
                            Text("Tickets", style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(0.6f), fontWeight = FontWeight.Bold))
                            Text("${activeBooking.quantity}x ${activeBooking.ticketType}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.ExtraBold, color = Color.White))
                        }
                    }
                }
            }

            // ── Perforated Divider ──────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                // Left notch
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .offset(x = (-12).dp)
                        .align(Alignment.CenterStart)
                        .background(MaterialTheme.colorScheme.background, CircleShape)
                )
                // Right notch
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .offset(x = 12.dp)
                        .align(Alignment.CenterEnd)
                        .background(MaterialTheme.colorScheme.background, CircleShape)
                )
                // Dashed line
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .padding(horizontal = 20.dp)
                        .align(Alignment.Center)
                ) {
                    val dashWidth = 12f
                    val gapWidth = 8f
                    var x = 0f
                    while (x < size.width) {
                        drawLine(
                            color = Color.Gray.copy(0.3f),
                            start = Offset(x, 0f),
                            end = Offset((x + dashWidth).coerceAtMost(size.width), 0f),
                            strokeWidth = 3f
                        )
                        x += dashWidth + gapWidth
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // ── Bottom section (ticket details + QR) ──────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Booking ID
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Booking ID", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onBackground.copy(0.5f)))
                        Text(
                            text = activeBooking.id,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Venue", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onBackground.copy(0.5f)))
                        Text(
                            text = activeBooking.event.venue.substringBefore(","),
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // QR Code
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .border(2.dp, Brush.linearGradient(listOf(gradStart.copy(0.4f), gradEnd.copy(0.4f))), RoundedCornerShape(16.dp))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    DummyQRCodeDrawer(token = activeBooking.qrCodeToken, accentColor = gradStart)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Scan at the entry gate",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.onBackground.copy(0.45f),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Total paid
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(listOf(gradStart.copy(0.08f), gradEnd.copy(0.08f))),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(12.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Paid", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onBackground.copy(0.6f)))
                        Text("₹${activeBooking.totalPrice}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, color = gradStart))
                    }
                }
            }
        }
    }
}

// ── Colorised QR Code Drawer ──────────────────────────────────────────────────
@Composable
fun DummyQRCodeDrawer(token: String, accentColor: Color = Color.Black) {
    val qrHash = token.hashCode()
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val columns = 11
        val rows = 11
        val cellWidth = width / columns
        val cellHeight = height / rows

        for (r in 0 until rows) {
            for (c in 0 until columns) {
                val isCornerFinder =
                    (r in 0..2 && c in 0..2) ||
                    (r in 0..2 && c in (columns - 3) until columns) ||
                    (r in (rows - 3) until rows && c in 0..2)

                if (isCornerFinder) {
                    val isOuter = r == 0 || r == 2 || c == 0 || c == 2 ||
                                  r == 0 || r == 2 || c == (columns - 1) || c == (columns - 3) ||
                                  r == (rows - 1) || r == (rows - 3) || c == 0 || c == 2
                    if (isOuter) {
                        drawRect(accentColor, Offset(c * cellWidth, r * cellHeight), Size(cellWidth, cellHeight))
                    } else if (r == 1 && c == 1 || r == 1 && c == (columns - 2) || r == (rows - 2) && c == 1) {
                        drawRect(accentColor, Offset(c * cellWidth, r * cellHeight), Size(cellWidth, cellHeight))
                    }
                } else {
                    val pseudoRandomBit = ((qrHash shr (r * c % 30)) and 1) == 1
                    if (pseudoRandomBit) {
                        drawRect(Color.Black, Offset(c * cellWidth, r * cellHeight), Size(cellWidth, cellHeight))
                    }
                }
            }
        }
    }
}
