package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.RadialCenterColor
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

const val SPLASH_DURATION_MS = 2500L
const val SPLASH_FADE_IN_MS = 500
const val SPLASH_FADE_OUT_MS = 300

/**
 * Screen 1: Branded Splash Screen.
 * Displays "Sayyed Studios" and the creator tagline with subtle animations
 * prior to any permission requests or camera passthrough initializations.
 */
@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    var startAnimation by remember { mutableStateOf(false) }
    var isFadingOut by remember { mutableStateOf(false) }

    val contentAlpha by animateFloatAsState(
        targetValue = if (isFadingOut) 0f else if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = if (isFadingOut) SPLASH_FADE_OUT_MS else SPLASH_FADE_IN_MS,
            easing = FastOutSlowInEasing
        ),
        label = "splash_fade_animation"
    )

    val taglineOffset by animateDpAsState(
        targetValue = if (startAnimation) 0.dp else 20.dp,
        animationSpec = tween(
            durationMillis = SPLASH_FADE_IN_MS,
            easing = FastOutSlowInEasing
        ),
        label = "tagline_slide_animation"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        // Keep splash on screen for the specified 2.5 seconds
        delay(SPLASH_DURATION_MS)
        // Smoothly fade out before navigating
        isFadingOut = true
        delay(SPLASH_FADE_OUT_MS.toLong())
        onSplashFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(RadialCenterColor, DarkBackground),
                    radius = 900f
                )
            )
            .testTag("splash_screen_container"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .alpha(contentAlpha)
                .testTag("splash_content_column"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Primary text: "Sayyed Studios" — large, bold, white, letter-spaced
            Text(
                text = "S A Y Y E D   S T U D I O S",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                letterSpacing = 3.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("splash_primary_title")
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Thin horizontal divider line between the two text lines (subtle, low-opacity white)
            Box(
                modifier = Modifier
                    .width(180.dp)
                    .height(1.dp)
                    .background(Color.White.copy(alpha = 0.25f))
                    .testTag("splash_divider")
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Secondary text below: "A Sayyed Yuzasif Abbas Application" — smaller, light gray, italic, subtle
            // The tagline slides up slightly (20dp) while fading in
            Text(
                text = "A Sayyed Yuzasif Abbas Application",
                fontSize = 14.sp,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Normal,
                color = TextSecondary,
                letterSpacing = 0.8.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .offset(y = taglineOffset)
                    .testTag("splash_secondary_tagline")
            )
        }
    }
}
