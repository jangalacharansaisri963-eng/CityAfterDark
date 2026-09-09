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
import androidx.compose.ui.text.font.FontFamily
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
    onRoll: () -> Unit,
    onAttack: () -> Unit,
    onToggleFocus: () -> Unit,
    isFocusActive: Boolean,
    currentWeaponName: String,
    currentWeaponAmmo: String,
    onNextWeapon: () -> Unit,
    onOpenPhone: () -> Unit,
    interactionPrompt: String?,
    onInteract: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(12.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Weapon quick switch bar
        Surface(
            onClick = onNextWeapon,
            color = Color(0xD90D1117),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF00E5FF)),
            modifier = Modifier.testTag("btn_switch_weapon")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = currentWeaponName.uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = currentWeaponAmmo,
                    color = Color(0xFF00E5FF),
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("⟳", color = Color(0xFFFFD600), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Contextual Interaction Button (Enter car, Talk, Store, Mission)
        if (interactionPrompt != null) {
            Button(
                onClick = onInteract,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD600)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .height(44.dp)
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
                    fontSize = 11.sp
                )
            }
        }

        // Action Buttons Row 1: Attack / Fire & Bullet-Time Focus
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            // Focus Bullet-Time
            Surface(
                onClick = onToggleFocus,
                shape = CircleShape,
                color = if (isFocusActive) Color(0xFFFFD600) else Color(0xCC263238),
                border = androidx.compose.foundation.BorderStroke(2.dp, if (isFocusActive) Color.White else Color(0x66FFD600)),
                modifier = Modifier
                    .size(48.dp)
                    .testTag("btn_focus")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "FOCUS",
                        color = if (isFocusActive) Color.Black else Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            // Phone Shortcut
            Surface(
                onClick = onOpenPhone,
                shape = CircleShape,
                color = Color(0xCC00E5FF),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color.White),
                modifier = Modifier
                    .size(48.dp)
                    .testTag("btn_phone")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = "PHONE", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Black)
                }
            }

            // Attack / Fire Primary Action
            Surface(
                onClick = onAttack,
                shape = CircleShape,
                color = Color(0xFFD50000),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color.White),
                modifier = Modifier
                    .size(54.dp)
                    .testTag("btn_attack")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "FIRE",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        // Action Buttons Row 2: Roll, Sprint, Jump
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            // Dodge Roll
            Surface(
                onClick = onRoll,
                shape = CircleShape,
                color = Color(0xCC7C4DFF),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color.White),
                modifier = Modifier
                    .size(50.dp)
                    .testTag("btn_roll")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = "ROLL", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Sprint button
            Surface(
                onClick = onToggleSprint,
                shape = CircleShape,
                color = if (isSprinting) Color(0xFFFF9100) else Color(0xCC263238),
                border = androidx.compose.foundation.BorderStroke(2.dp, if (isSprinting) Color.White else Color(0x66FFFFFF)),
                modifier = Modifier
                    .size(52.dp)
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
                    .size(52.dp)
                    .testTag("btn_jump")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "JUMP",
                        color = Color.White,
                        fontSize = 11.sp,
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
    onToggleNitro: () -> Unit,
    isNitroActive: Boolean,
    nitroPercent: Float,
    onToggleHeadlights: () -> Unit,
    headlightsOn: Boolean,
    currentRadioStationName: String,
    onNextRadio: () -> Unit,
    onExitVehicle: () -> Unit,
    onOpenPhone: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(12.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Vehicle Top Control Row (Radio bar & Exit)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            // Radio Switcher Pill
            Surface(
                onClick = onNextRadio,
                shape = RoundedCornerShape(10.dp),
                color = Color(0xCC10141C),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E5FF)),
                modifier = Modifier.testTag("btn_radio_next")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("📻", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(currentRadioStationName, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("▶", color = Color(0xFF00E5FF), fontSize = 10.sp)
                }
            }

            // Headlights toggle
            Surface(
                onClick = onToggleHeadlights,
                shape = CircleShape,
                color = if (headlightsOn) Color(0xFFFFD600) else Color(0xCC263238),
                modifier = Modifier.size(34.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(if (headlightsOn) "💡" else "🔅", fontSize = 13.sp)
                }
            }

            // Phone Shortcut
            Surface(
                onClick = onOpenPhone,
                shape = CircleShape,
                color = Color(0xCC00E5FF),
                modifier = Modifier.size(34.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("📱", fontSize = 13.sp)
                }
            }

            // Exit Vehicle button
            Button(
                onClick = onExitVehicle,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xD9D50000)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .height(38.dp)
                    .testTag("btn_exit_vehicle")
            ) {
                Text("EXIT", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Bottom) {
            // Horn
            IconButton(
                onClick = onHonk,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0x99263238))
                    .testTag("btn_horn")
            ) {
                Icon(Icons.Default.VolumeUp, contentDescription = "Horn", tint = Color.White)
            }

            // Nitro Turbo Button
            Surface(
                onClick = onToggleNitro,
                shape = RoundedCornerShape(12.dp),
                color = if (isNitroActive) Color(0xFF00E5FF) else Color(0xCC006064),
                border = androidx.compose.foundation.BorderStroke(2.dp, if (isNitroActive) Color.White else Color(0x6600E5FF)),
                modifier = Modifier
                    .size(54.dp)
                    .testTag("btn_nitro")
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "NITRO",
                        color = if (isNitroActive) Color.Black else Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "${(nitroPercent * 100).toInt()}%",
                        color = if (isNitroActive) Color.Black else Color(0xFF80DEEA),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Handbrake / Drift
            Surface(
                onClick = { onHandbrake(true) },
                shape = RoundedCornerShape(10.dp),
                color = Color(0xCCFF6D00),
                modifier = Modifier
                    .size(52.dp)
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
                    .size(width = 60.dp, height = 70.dp)
                    .testTag("btn_brake")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("BRAKE\nREV", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Accelerate Gas Pedal
            Surface(
                onClick = { onThrottle(1f) },
                shape = RoundedCornerShape(12.dp),
                color = Color(0xCC00C853),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color.White),
                modifier = Modifier
                    .size(width = 70.dp, height = 82.dp)
                    .testTag("btn_gas")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("GAS", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}
