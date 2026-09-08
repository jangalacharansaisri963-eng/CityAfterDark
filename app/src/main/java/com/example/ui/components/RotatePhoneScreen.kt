package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.StayCurrentLandscape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RotatePhoneScreen(
    onSimulateLandscape: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "rotation_anim")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 90f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "phone_rotation"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF070A10), Color(0xFF101624), Color(0xFF070A10))
                )
            )
            .padding(32.dp)
            .testTag("screen_rotate_phone_warning"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated Rotating Icon
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .background(Color(0x2600E5FF), RoundedCornerShape(24.dp))
                    .border(2.dp, Color(0xFF00E5FF), RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ScreenRotation,
                    contentDescription = "Rotate Phone",
                    tint = Color(0xFF00E5FF),
                    modifier = Modifier
                        .size(64.dp)
                        .rotate(rotationAngle)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "CITY AFTER DARK",
                color = Color(0xFF00E5FF),
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 4.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Rotate your phone to landscape mode to play.",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "This open-world experience requires two-handed landscape controls for full vehicle physics, camera navigation, and interactive mission play.",
                color = Color(0xFF90A4AE),
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            RowLandscapeBadge()

            if (onSimulateLandscape != null) {
                Spacer(modifier = Modifier.height(20.dp))
                androidx.compose.material3.Button(
                    onClick = onSimulateLandscape,
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00E5FF)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("btn_simulate_landscape")
                ) {
                    Text(
                        text = "PLAY NOW (PREVIEW OVERRIDE)",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun RowLandscapeBadge() {
    Box(
        modifier = Modifier
            .background(Color(0x3300E676), RoundedCornerShape(20.dp))
            .border(1.dp, Color(0xFF00E676), RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        androidx.compose.foundation.layout.Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.StayCurrentLandscape,
                contentDescription = null,
                tint = Color(0xFF00E676),
                modifier = Modifier.size(18.dp)
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = "LANDSCAPE ORIENTATION REQUIRED",
                color = Color(0xFF00E676),
                fontSize = 11.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}
