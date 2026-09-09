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
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
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
import com.example.engine.ecs.VehicleComponent
import com.example.engine.math.Vector3
import com.example.game.GameEngine
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
    engine: GameEngine? = null,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.padding(10.dp)) {
        // TOP BAR
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            // Top Left: Minimap + Stats
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                MinimapView(
                    playerPos = player.position,
                    playerYaw = player.yaw,
                    roadNetwork = roadNetwork,
                    entityManager = entityManager,
                    currentDistrict = currentDistrict,
                    targetPos = targetPosition,
                    onOpenFullMap = onOpenFullMap
                )

                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    // Health bar (Red)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Health",
                            tint = Color(0xFFFF1744),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Box(
                            modifier = Modifier
                                .width(84.dp)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color(0x66000000))
                        ) {
                            Box(
                                modifier = Modifier
                                    .width((84f * (player.health / 100f).coerceIn(0f, 1f)).dp)
                                    .height(6.dp)
                                    .background(Color(0xFFFF1744))
                            )
                        }
                    }

                    // Body Armor Shield bar (Blue)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Armor",
                            tint = Color(0xFF00E5FF),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Box(
                            modifier = Modifier
                                .width(84.dp)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color(0x66000000))
                        ) {
                            Box(
                                modifier = Modifier
                                    .width((84f * (player.armor / 100f).coerceIn(0f, 1f)).dp)
                                    .height(6.dp)
                                    .background(Color(0xFF00E5FF))
                            )
                        }
                    }

                    // Stamina bar (Green)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FlashOn,
                            contentDescription = "Stamina",
                            tint = Color(0xFF00E676),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Box(
                            modifier = Modifier
                                .width(84.dp)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color(0x66000000))
                        ) {
                            Box(
                                modifier = Modifier
                                    .width((84f * (player.stamina / 100f).coerceIn(0f, 1f)).dp)
                                    .height(6.dp)
                                    .background(Color(0xFF00E676))
                            )
                        }
                    }

                    // Bullet-Time Focus bar (Amber)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = "Focus",
                            tint = Color(0xFFFFD600),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Box(
                            modifier = Modifier
                                .width(84.dp)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color(0x66000000))
                        ) {
                            Box(
                                modifier = Modifier
                                    .width((84f * (player.focus / 100f).coerceIn(0f, 1f)).dp)
                                    .height(6.dp)
                                    .background(Color(0xFFFFD600))
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
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "%,d".format(player.money),
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            // Top Center: District Banner, Time, and Wanted Stars
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    color = Color(0xD90D1117),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(currentDistrict.minimapColor))
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 3.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = currentDistrict.title.uppercase(),
                            color = Color(currentDistrict.minimapColor),
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            text = "$timeString • $weatherString",
                            color = Color(0xFFB0BEC5),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Wanted Stars (0 to 5)
                if (engine != null) {
                    val wantedStars = engine.wantedSystem.wantedStars
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        modifier = Modifier.padding(top = 4.dp).testTag("hud_wanted_stars")
                    ) {
                        for (i in 1..5) {
                            val active = i <= wantedStars
                            Icon(
                                imageVector = if (active) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = "Wanted Star $i",
                                tint = if (active) Color(0xFFFF1744) else Color(0x33FFFFFF),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Police Radio Alert Toast
                    if (engine.wantedSystem.policeRadioMessage != null) {
                        Text(
                            text = engine.wantedSystem.policeRadioMessage ?: "",
                            color = Color(0xFFFF5252),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }

            // Top Right: Game Tools & Pause Menu
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                // Phone shortcut
                IconButton(
                    onClick = { engine?.showPhoneDialog = true },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xCC10141C))
                        .border(1.dp, Color(0x6600E5FF), CircleShape)
                        .testTag("btn_top_phone")
                ) {
                    Icon(Icons.Default.PhoneAndroid, contentDescription = "Phone", tint = Color(0xFF00E5FF), modifier = Modifier.size(18.dp))
                }

                // Photo mode shortcut
                IconButton(
                    onClick = { engine?.showPhotoModeDialog = true },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xCC10141C))
                        .border(1.dp, Color(0x66FFD600), CircleShape)
                        .testTag("btn_top_photo")
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "Photo Mode", tint = Color(0xFFFFD600), modifier = Modifier.size(18.dp))
                }

                // Skill Tree shortcut
                IconButton(
                    onClick = { engine?.showSkillTreeDialog = true },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xCC10141C))
                        .border(1.dp, Color(0x6600E676), CircleShape)
                        .testTag("btn_top_skills")
                ) {
                    Icon(Icons.Default.MilitaryTech, contentDescription = "Skills", tint = Color(0xFF00E676), modifier = Modifier.size(18.dp))
                }

                // Real Estate shortcut
                IconButton(
                    onClick = { engine?.showRealEstateDialog = true },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xCC10141C))
                        .border(1.dp, Color(0x66FF9100), CircleShape)
                        .testTag("btn_top_realestate")
                ) {
                    Icon(Icons.Default.Apartment, contentDescription = "Properties", tint = Color(0xFFFF9100), modifier = Modifier.size(18.dp))
                }

                // Pause Menu Button
                IconButton(
                    onClick = onOpenPauseMenu,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0xCC10141C))
                        .border(1.dp, Color.White, CircleShape)
                        .testTag("btn_pause_menu")
                ) {
                    Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        }

        // CENTER TOP: Drift Points Card
        if (engine != null && engine.driftTracker.isDrifting) {
            Box(modifier = Modifier.align(Alignment.TopCenter).padding(top = 70.dp)) {
                Surface(
                    color = Color(0xF2FF6D00),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(2.dp, Color.White),
                    modifier = Modifier.testTag("hud_drift_card")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("DRIFT", color = Color.White, fontWeight = FontWeight.Black, fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "+${engine.driftTracker.currentDriftScore.toInt()} PTS",
                            color = Color(0xFFFFEB3B),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "x%.1f".format(engine.driftTracker.driftMultiplier),
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // BOTTOM LEFT: Speedometer with Nitro & Health (when driving)
        if (isDriving) {
            val veh = player.currentVehicle?.get<VehicleComponent>()
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
                    Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Speed, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${abs(vehicleSpeedKmh).roundToInt()}",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "KM/H", color = Color(0xFF80DEEA), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        // Nitro & Health bars
                        if (veh != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            // Nitro bar
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("NOS", color = Color(0xFF00E5FF), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(4.dp))
                                Box(
                                    modifier = Modifier
                                        .width(70.dp)
                                        .height(4.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(Color(0x66000000))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .width((70f * (veh.nitroAmount / 100f).coerceIn(0f, 1f)).dp)
                                            .height(4.dp)
                                            .background(Color(0xFF00E5FF))
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // ON-FOOT: Quick Consumable Belt (Cola, Medkit, Armor, Adrenaline)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(bottom = 140.dp)
            ) {
                Surface(
                    color = Color(0xCC10141C),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x66FFFFFF)),
                    modifier = Modifier.testTag("hud_consumable_belt")
                ) {
                    Row(
                        modifier = Modifier.padding(6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Cola
                        val colaCount = player.inventory.getCount("item_cola")
                        Surface(
                            onClick = { player.useConsumable("item_cola") },
                            color = Color(0x99263238),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text("🥤", fontSize = 12.sp)
                                Text("$colaCount", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Medkit
                        val medCount = player.inventory.getCount("item_medkit")
                        Surface(
                            onClick = { player.useConsumable("item_medkit") },
                            color = Color(0x99263238),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text("💊", fontSize = 12.sp)
                                Text("$medCount", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Armor plate
                        val armCount = player.inventory.getCount("item_armor_plate")
                        Surface(
                            onClick = { player.useConsumable("item_armor_plate") },
                            color = Color(0x99263238),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text("🛡️", fontSize = 12.sp)
                                Text("$armCount", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Adrenaline
                        val adrCount = player.inventory.getCount("item_adrenaline")
                        Surface(
                            onClick = { player.useConsumable("item_adrenaline") },
                            color = Color(0x99263238),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text("⚡", fontSize = 12.sp)
                                Text("$adrCount", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // BOTTOM CENTER: Current Mission Objective Card
        if (objectiveDescription != null && currentMission != null) {
            Surface(
                color = Color(0xEE0B0F19),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFFD600)),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 10.dp)
                    .testTag("hud_mission_card")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
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
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    if (distanceToObjective != null && distanceToObjective > 0f) {
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "${distanceToObjective.roundToInt()}m",
                            color = Color(0xFF00E5FF),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    if (objectiveTimer > 0f) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${objectiveTimer.roundToInt()}s",
                            color = Color(0xFFFF5252),
                            fontSize = 12.sp,
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
