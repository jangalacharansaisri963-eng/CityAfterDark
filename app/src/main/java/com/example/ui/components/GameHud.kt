package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.ecs.EntityManager
import com.example.engine.math.Vector3
import com.example.game.character.Player
import com.example.game.missions.DialogueLine
import com.example.game.missions.StoryMission
import com.example.game.world.District
import com.example.game.world.RoadNetwork
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun GameHud(
    player: Player,
    currentDistrict: District,
    timeString: String,
    weatherString: String,
    currentMission: StoryMission?,
    objectiveDescription: String?,
    distanceToObjective: Float?,
    objectiveTimer: Float,
    isDriving: Boolean,
    vehicleSpeedKmh: Float,
    activeDialogue: DialogueLine?,
    onAdvanceDialogue: () -> Unit,
    onOpenPauseMenu: () -> Unit,
    onOpenFullMap: () -> Unit,
    roadNetwork: RoadNetwork,
    entityManager: EntityManager,
    targetPosition: Vector3?,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.padding(12.dp)) {
        // TOP BAR
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            // Top Left: Minimap + Stats
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                MinimapView(
                    playerPos = player.position,
                    playerYaw = player.yaw,
                    roadNetwork = roadNetwork,
                    entityManager = entityManager,
                    currentDistrict = currentDistrict,
                    targetPos = targetPosition,
                    onOpenFullMap = onOpenFullMap
                )

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    // Health bar
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Health",
                            tint = Color(0xFFFF1744),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .width(90.dp)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0x66000000))
                        ) {
                            Box(
                                modifier = Modifier
                                    .width((90f * (player.health / 100f).coerceIn(0f, 1f)).dp)
                                    .height(8.dp)
                                    .background(Color(0xFFFF1744))
                            )
                        }
                    }

                    // Stamina bar
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FlashOn,
                            contentDescription = "Stamina",
                            tint = Color(0xFF00E676),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .width(90.dp)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0x66000000))
                        ) {
                            Box(
                                modifier = Modifier
                                    .width((90f * (player.stamina / 100f).coerceIn(0f, 1f)).dp)
                                    .height(8.dp)
                                    .background(Color(0xFF00E676))
                            )
                        }
                    }

                    // Money
                    Surface(
                        color = Color(0xCC10141C),
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x66FFD700))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$",
                                color = Color(0xFFFFD700),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "%,d".format(player.money),
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            // Top Center: District Banner & Time
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    color = Color(0xD90D1117),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(currentDistrict.minimapColor))
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = currentDistrict.title.uppercase(),
                            color = Color(currentDistrict.minimapColor),
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = "$timeString • $weatherString",
                            color = Color(0xFFB0BEC5),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Top Right: Pause Menu Button
            IconButton(
                onClick = onOpenPauseMenu,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xCC10141C))
                    .border(1.dp, Color.White, CircleShape)
                    .testTag("btn_pause_menu")
            ) {
                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
            }
        }

        // BOTTOM LEFT: Speedometer (when driving)
        if (isDriving) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(bottom = 140.dp)
            ) {
                Surface(
                    color = Color(0xE610141C),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF00E5FF)),
                    modifier = Modifier.testTag("hud_speedometer")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Speed, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${abs(vehicleSpeedKmh).roundToInt()}",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("KM/H", color = Color(0xFF80D8FF), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // BOTTOM CENTER: Active Mission Objective Card
        if (objectiveDescription != null && currentMission != null) {
            Surface(
                color = Color(0xEE0B0F19),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFFD600)),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp)
                    .testTag("hud_mission_card")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFD600))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "${currentMission.title.uppercase()} • ${currentMission.chapterTitle}",
                            color = Color(0xFFFFD600),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = objectiveDescription,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    if (distanceToObjective != null && distanceToObjective > 0f) {
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "${distanceToObjective.roundToInt()}m",
                            color = Color(0xFF00E5FF),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    if (objectiveTimer > 0f) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${objectiveTimer.roundToInt()}s",
                            color = Color(0xFFFF5252),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // DIALOGUE SUBTITLE BOX (Overlaid on screen during cutscenes/conversations)
        AnimatedVisibility(
            visible = activeDialogue != null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            if (activeDialogue != null) {
                Surface(
                    color = Color(0xF2080C14),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF00E5FF)),
                    modifier = Modifier
                        .fillMaxWidth(0.72f)
                        .clickable { onAdvanceDialogue() }
                        .testTag("hud_dialogue_box")
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = activeDialogue.speakerName.uppercase(),
                                color = Color(0xFF00E5FF),
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "•  ${activeDialogue.speakerTitle}",
                                color = Color(0xFF90A4AE),
                                fontSize = 11.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = activeDialogue.text,
                            color = Color.White,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight.Normal
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Tap anywhere to continue ▶",
                            color = Color(0xFFFFD600),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }
            }
        }
    }
}
