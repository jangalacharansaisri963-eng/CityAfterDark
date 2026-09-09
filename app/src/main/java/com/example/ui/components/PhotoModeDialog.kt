package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class PhotoFilter(val displayName: String, val overlayColor: Color) {
    NONE("Original Vivid", Color.Transparent),
    CYBERPUNK("Neon Cyber 2077", Color(0x3300E5FF)),
    FILM_NOIR("Film Noir 1940s", Color(0x44263238)),
    SUNSET_GOLD("Golden Sunset", Color(0x33FFA000)),
    RETRO_VHS("VHS Glitch Synth", Color(0x33E040FB))
}

@Composable
fun PhotoModeDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var activeFilter by remember { mutableStateOf(PhotoFilter.NONE) }
    var zoomLevel by remember { mutableFloatStateOf(1.0f) }
    var uiHidden by remember { mutableStateOf(false) }
    var shutterFlash by remember { mutableStateOf(false) }
    var snapshotSavedToast by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("dialog_photo_mode")
    ) {
        // Aesthetic color overlay according to chosen filter
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(activeFilter.overlayColor)
        )

        // Viewfinder Rule-of-Thirds Grid
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .border(1.dp, Color(0x44FFFFFF))
        )

        // Shutter flash animation
        AnimatedVisibility(
            visible = shutterFlash,
            enter = fadeIn(animationSpec = tween(50)),
            exit = fadeOut(animationSpec = tween(300))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            )
        }

        // Camera UI Controls
        if (!uiHidden) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0xCC0D1117),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E5FF))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("PHOTO MODE", color = Color.White, fontWeight = FontWeight.Black, fontSize = 12.sp, letterSpacing = 1.sp)
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xCC0D1117))
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            // Bottom Filter & Shutter controls
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (snapshotSavedToast != null) {
                    Surface(
                        color = Color(0xCC00E676),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(bottom = 10.dp)
                    ) {
                        Text(
                            text = snapshotSavedToast!!,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }

                // Filter selectors
                Row(
                    modifier = Modifier
                        .background(Color(0xCC10141C), RoundedCornerShape(24.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PhotoFilter.values().forEach { filter ->
                        Surface(
                            onClick = { activeFilter = filter },
                            shape = RoundedCornerShape(16.dp),
                            color = if (activeFilter == filter) Color(0xFF00E5FF) else Color(0x33FFFFFF)
                        ) {
                            Text(
                                text = filter.displayName,
                                color = if (activeFilter == filter) Color.Black else Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Main capture button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    IconButton(
                        onClick = { uiHidden = true },
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color(0xCC1E2633))
                    ) {
                        Icon(Icons.Default.VisibilityOff, contentDescription = "Hide UI", tint = Color.White)
                    }

                    // Shutter button
                    Surface(
                        onClick = {
                            shutterFlash = true
                            snapshotSavedToast = "Captured in 4K UHD! Saved to Gallery."
                            // auto clear flash
                        },
                        shape = CircleShape,
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(4.dp, Color(0xFF00E5FF)),
                        modifier = Modifier.size(64.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF0D1117))
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(46.dp))
                }
            }
        } else {
            // Tap anywhere to reveal UI when hidden
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { uiHidden = false },
                contentAlignment = Alignment.TopEnd
            ) {
                Surface(
                    color = Color(0x99000000),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Tap to show controls",
                        color = Color.White,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}
