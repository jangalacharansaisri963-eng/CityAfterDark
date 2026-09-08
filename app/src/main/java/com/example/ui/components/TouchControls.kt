package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt
import kotlin.math.sqrt

@Composable
fun VirtualJoystick(
    onMove: (x: Float, y: Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val maxRadiusPx = 130f
    var knobOffset by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = modifier
            .size(130.dp)
            .clip(CircleShape)
            .background(Color(0x33000000))
            .border(2.dp, Color(0x6600E5FF), CircleShape)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val delta = offset - center
                        val dist = delta.getDistance()
                        val clamped = if (dist > maxRadiusPx) delta * (maxRadiusPx / dist) else delta
                        knobOffset = clamped
                        onMove(clamped.x / maxRadiusPx, -clamped.y / maxRadiusPx)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val newOffset = knobOffset + dragAmount
                        val dist = newOffset.getDistance()
                        val clamped = if (dist > maxRadiusPx) newOffset * (maxRadiusPx / dist) else newOffset
                        knobOffset = clamped
                        onMove(clamped.x / maxRadiusPx, -clamped.y / maxRadiusPx)
                    },
                    onDragEnd = {
                        knobOffset = Offset.Zero
                        onMove(0f, 0f)
                    },
                    onDragCancel = {
                        knobOffset = Offset.Zero
                        onMove(0f, 0f)
                    }
                )
            }
            .testTag("touch_joystick"),
        contentAlignment = Alignment.Center
    ) {
        // Outer concentric guides
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color(0x2200E5FF),
                radius = size.width / 2f - 4f
            )
            drawCircle(
                color = Color(0x33FFFFFF),
                radius = size.width / 4f
            )
        }

        // Joystick Knob
        Box(
            modifier = Modifier
                .offset { IntOffset(knobOffset.x.roundToInt(), knobOffset.y.roundToInt()) }
                .size(54.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFF00E5FF), Color(0xFF0091EA))
                    )
                )
                .border(2.dp, Color.White, CircleShape)
        )
    }
}

@Composable
fun CameraTouchPad(
    onLook: (deltaYaw: Float, deltaPitch: Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    // Sensitivity scaling
                    val sensX = 0.28f
                    val sensY = 0.24f
                    onLook(dragAmount.x * sensX, dragAmount.y * sensY)
                }
            }
            .testTag("camera_touch_surface")
    )
}

@Composable
fun OnFootActionControls(
    isSprinting: Boolean,
    onToggleSprint: () -> Unit,
    onJump: () -> Unit,
    interactionPrompt: String?,
    onInteract: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Contextual Interaction Button (Enter car, Talk, Store, Mission)
        if (interactionPrompt != null) {
            Button(
                onClick = onInteract,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD600)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .height(48.dp)
                    .testTag("btn_interact")
            ) {
                Icon(
                    imageVector = Icons.Default.DirectionsCar,
                    contentDescription = null,
                    tint = Color.Black
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = interactionPrompt,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // Sprint button
            Surface(
                onClick = onToggleSprint,
                shape = CircleShape,
                color = if (isSprinting) Color(0xFFFF9100) else Color(0xCC263238),
                border = androidx.compose.foundation.BorderStroke(2.dp, if (isSprinting) Color.White else Color(0x66FFFFFF)),
                modifier = Modifier
                    .size(56.dp)
                    .testTag("btn_sprint")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "SPRINT",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Jump button
            Surface(
                onClick = onJump,
                shape = CircleShape,
                color = Color(0xCC00B0FF),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color.White),
                modifier = Modifier
                    .size(56.dp)
                    .testTag("btn_jump")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "JUMP",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun InVehicleActionControls(
    onThrottle: (Float) -> Unit, // 1 for gas, -1 for brake/reverse, 0 for release
    onHandbrake: (Boolean) -> Unit,
    onHonk: () -> Unit,
    onExitVehicle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Exit Vehicle button
        Button(
            onClick = onExitVehicle,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xD9D50000)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .height(44.dp)
                .testTag("btn_exit_vehicle")
        ) {
            Text("EXIT VEHICLE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Bottom) {
            // Horn
            IconButton(
                onClick = onHonk,
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color(0x99263238))
                    .testTag("btn_horn")
            ) {
                Icon(Icons.Default.VolumeUp, contentDescription = "Horn", tint = Color.White)
            }

            // Handbrake
            Surface(
                onClick = { onHandbrake(true) },
                shape = RoundedCornerShape(10.dp),
                color = Color(0xCCFF6D00),
                modifier = Modifier
                    .size(54.dp)
                    .testTag("btn_handbrake")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("DRIFT", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Brake / Reverse
            Surface(
                onClick = { onThrottle(-1f) },
                shape = RoundedCornerShape(12.dp),
                color = Color(0xCCE53935),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color.White),
                modifier = Modifier
                    .size(width = 62.dp, height = 72.dp)
                    .testTag("btn_brake")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("BRAKE\nREV", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Accelerate Gas Pedal
            Surface(
                onClick = { onThrottle(1f) },
                shape = RoundedCornerShape(12.dp),
                color = Color(0xCC00C853),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color.White),
                modifier = Modifier
                    .size(width = 72.dp, height = 86.dp)
                    .testTag("btn_gas")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("GAS", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}
