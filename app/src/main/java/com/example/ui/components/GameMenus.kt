package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.engine.math.Vector3
import com.example.game.character.Outfit
import com.example.game.character.Player
import com.example.game.missions.MissionManager
import com.example.game.simulation.DayNightSystem
import com.example.game.simulation.WeatherType
import com.example.game.vehicle.VehicleType
import com.example.game.world.District
import kotlin.math.roundToInt

@Composable
fun PauseMenuDialog(
    onResume: () -> Unit,
    onSaveGame: () -> Unit,
    onOpenMap: () -> Unit,
    onOpenWardrobe: () -> Unit,
    onOpenGarage: () -> Unit,
    onOpenSettings: () -> Unit,
    saveFeedback: String?
) {
    Dialog(
        onDismissRequest = onResume,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xE6080C14))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.68f)
                    .fillMaxHeight(0.9f)
                    .testTag("dialog_pause_menu"),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xF2101622),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF00E5FF))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "CITY AFTER DARK",
                                color = Color(0xFF00E5FF),
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                letterSpacing = 2.sp
                            )
                            Text(
                                text = "GAME PAUSED",
                                color = Color(0xFF90A4AE),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        IconButton(onClick = onResume) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }

                    if (saveFeedback != null) {
                        Surface(
                            color = Color(0x3300E676),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E676))
                        ) {
                            Text(
                                text = saveFeedback,
                                color = Color(0xFF00E676),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }
                    }

                    // Menu Options
                    Column(
                        modifier = Modifier.fillMaxWidth(0.85f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MenuButton("RESUME GAME", Icons.Default.PlayArrow, Color(0xFF00E676), onResume)
                        MenuButton("SAVE GAME", Icons.Default.Save, Color(0xFF00E5FF), onSaveGame)
                        MenuButton("WORLD MAP", Icons.Default.Map, Color(0xFFFFD600), onOpenMap)
                        MenuButton("WARDROBE SHOP", Icons.Default.Palette, Color(0xFFFF4081), onOpenWardrobe)
                        MenuButton("VEHICLE GARAGE", Icons.Default.DirectionsCar, Color(0xFFFF9100), onOpenGarage)
                        MenuButton("SETTINGS & GRAPHICS", Icons.Default.Settings, Color(0xFFB0BEC5), onOpenSettings)
                    }

                    Text(
                        text = "Build 1.0.0 • AI Studio Powered Native 3D Engine",
                        color = Color(0xFF546E7A),
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun MenuButton(
    title: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0x33263238)),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun FullMapDialog(
    playerPos: Vector3,
    targetPos: Vector3?,
    onClose: () -> Unit,
    onFastTravel: (Vector3) -> Unit
) {
    Dialog(onDismissRequest = onClose, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xE6080C14))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("dialog_world_map"),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xF20F1522),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF00E5FF))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("CITY AFTER DARK — METROPOLIS MAP", color = Color(0xFF00E5FF), fontWeight = FontWeight.Black, fontSize = 16.sp)
                            Text("10 Districts • Safehouses • Story Hubs • Garages", color = Color(0xFF90A4AE), fontSize = 11.sp)
                        }
                        IconButton(onClick = onClose) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // District list and Fast travel pins
                    Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        // Left: Districts
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(District.values()) { dist ->
                                Surface(
                                    color = Color(0x33263238),
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(dist.minimapColor).copy(alpha = 0.7f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(dist.title, color = Color(dist.minimapColor), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text(dist.description, color = Color(0xFFCFD8DC), fontSize = 10.sp, lineHeight = 13.sp)
                                    }
                                }
                            }
                        }

                        // Right: Landmarks & Fast Travel Buttons
                        Column(
                            modifier = Modifier
                                .weight(1.1f)
                                .fillMaxHeight(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("KEY LANDMARKS & HUBS", color = Color(0xFFFFD600), fontWeight = FontWeight.Bold, fontSize = 12.sp)

                            LandmarkItem("Midtown Safehouse (Bed & Wardrobe)", "Residential District", Vector3(80f, 0f, 40f), onFastTravel, onClose)
                            LandmarkItem("Marcus's Diner (Story Chapter Hub)", "Downtown Center", Vector3(20f, 0f, 20f), onFastTravel, onClose)
                            LandmarkItem("Waterfront Warehouse 14", "Waterfront Docks", Vector3(-180f, 0f, -20f), onFastTravel, onClose)
                            LandmarkItem("Urban Thread Clothing Boutique", "Neon Promenade", Vector3(30f, 0f, -80f), onFastTravel, onClose)
                            LandmarkItem("Veloce Dealership & Garage", "Downtown West", Vector3(-30f, 0f, -40f), onFastTravel, onClose)
                            LandmarkItem("Street Racing Circuit", "Skyway Expressway", Vector3(-160f, 0f, -160f), onFastTravel, onClose)
                            LandmarkItem("Southern Aerodrome Hangar", "Airport Outskirts", Vector3(-40f, 0f, -380f), onFastTravel, onClose)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LandmarkItem(
    title: String,
    district: String,
    pos: Vector3,
    onFastTravel: (Vector3) -> Unit,
    onClose: () -> Unit
) {
    Surface(
        color = Color(0x33102027),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x44FFFFFF)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                Text(district, color = Color(0xFF80CBC4), fontSize = 9.sp)
            }
            Button(
                onClick = {
                    onFastTravel(pos)
                    onClose()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B0FF)),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.height(30.dp)
            ) {
                Text("TRAVEL", fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun WardrobeDialog(
    player: Player,
    onEquipOutfit: (Outfit) -> Unit,
    onClose: () -> Unit
) {
    Dialog(onDismissRequest = onClose, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xE6080C14))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("dialog_wardrobe"),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xF2101624),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFF4081))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("URBAN THREAD — WARDROBE & TAILOR", color = Color(0xFFFF4081), fontWeight = FontWeight.Black, fontSize = 16.sp)
                            Text("Player Cash: $${player.money}", color = Color(0xFFFFD700), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        IconButton(onClick = onClose) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(Player.OUTFITS) { outfit ->
                            val isEquipped = player.currentOutfit.id == outfit.id
                            Surface(
                                color = if (isEquipped) Color(0x33FF4081) else Color(0x33263238),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.5.dp,
                                    if (isEquipped) Color(0xFFFF4081) else Color(0x44FFFFFF)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(outfit.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(outfit.description, color = Color(0xFFB0BEC5), fontSize = 11.sp)
                                        Text(
                                            text = if (outfit.price == 0) "FREE (OWNED)" else "$${outfit.price}",
                                            color = Color(0xFFFFD700),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }
                                    Button(
                                        onClick = {
                                            if (player.money >= outfit.price || outfit.price == 0) {
                                                if (outfit.price > 0 && !isEquipped) player.money -= outfit.price
                                                onEquipOutfit(outfit)
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isEquipped) Color(0xFF00E676) else Color(0xFFFF4081)
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = if (isEquipped) "EQUIPPED" else if (outfit.price == 0) "EQUIP" else "BUY & WEAR",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DealershipGarageDialog(
    player: Player,
    onSpawnVehicle: (VehicleType) -> Unit,
    onClose: () -> Unit
) {
    Dialog(onDismissRequest = onClose, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xE6080C14))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("dialog_garage"),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xF2101624),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFF9100))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("VELOCE MOTORS & TUNING GARAGE", color = Color(0xFFFF9100), fontWeight = FontWeight.Black, fontSize = 16.sp)
                            Text("Player Cash: $${player.money} • Instant Valet Delivery", color = Color(0xFFFFD700), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        IconButton(onClick = onClose) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(VehicleType.values()) { veh ->
                            Surface(
                                color = Color(0x33263238),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x66FF9100)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(veh.displayName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text(veh.description, color = Color(0xFFB0BEC5), fontSize = 11.sp)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("Max Speed: ${(veh.maxSpeed * 3.6f).roundToInt()} km/h  |  Price: $${veh.price}", color = Color(0xFFFFD600), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                    }

                                    Button(
                                        onClick = {
                                            onSpawnVehicle(veh)
                                            onClose()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9100)),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("DELIVER", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsDialog(
    dayNightSystem: DayNightSystem,
    viewDistance: Float,
    onChangeViewDistance: (Float) -> Unit,
    trafficDensity: Int,
    onChangeTrafficDensity: (Int) -> Unit,
    masterVolume: Float,
    onChangeVolume: (Float) -> Unit,
    onClose: () -> Unit
) {
    Dialog(onDismissRequest = onClose, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xE6080C14))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("dialog_settings"),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xF2101624),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF00E5FF))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("GRAPHICS, AUDIO & WORLD SETTINGS", color = Color(0xFF00E5FF), fontWeight = FontWeight.Black, fontSize = 16.sp)
                        IconButton(onClick = onClose) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        // Weather selector
                        item {
                            Text("WEATHER & ATMOSPHERE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 6.dp)) {
                                WeatherType.values().forEach { w ->
                                    val isSelected = dayNightSystem.weather == w
                                    Surface(
                                        onClick = { dayNightSystem.weather = w },
                                        color = if (isSelected) Color(0xFF00E5FF) else Color(0x33263238),
                                        shape = RoundedCornerShape(8.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E5FF))
                                    ) {
                                        Text(
                                            text = w.displayName,
                                            color = if (isSelected) Color.Black else Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Time of Day Slider
                        item {
                            var sliderTime by remember { mutableStateOf(dayNightSystem.timeOfDay) }
                            Text("TIME OF DAY: ${dayNightSystem.getFormattedTime()}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Slider(
                                value = sliderTime,
                                onValueChange = {
                                    sliderTime = it
                                    dayNightSystem.timeOfDay = it
                                },
                                valueRange = 0f..24f,
                                colors = SliderDefaults.colors(thumbColor = Color(0xFF00E5FF), activeTrackColor = Color(0xFF00E5FF))
                            )
                        }

                        // View Distance Slider
                        item {
                            Text("VIEW DISTANCE: ${viewDistance.roundToInt()}m", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Slider(
                                value = viewDistance,
                                onValueChange = onChangeViewDistance,
                                valueRange = 140f..350f,
                                colors = SliderDefaults.colors(thumbColor = Color(0xFF00E676), activeTrackColor = Color(0xFF00E676))
                            )
                        }

                        // Traffic Density
                        item {
                            Text("TRAFFIC DENSITY: $trafficDensity VEHICLES", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 6.dp)) {
                                listOf(8 to "LOW (8)", 14 to "MEDIUM (14)", 22 to "HIGH (22)").forEach { (count, label) ->
                                    val isSelected = trafficDensity == count
                                    Surface(
                                        onClick = { onChangeTrafficDensity(count) },
                                        color = if (isSelected) Color(0xFFFF9100) else Color(0x33263238),
                                        shape = RoundedCornerShape(8.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF9100))
                                    ) {
                                        Text(
                                            text = label,
                                            color = if (isSelected) Color.Black else Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Audio Volume
                        item {
                            Text("MASTER AUDIO VOLUME: ${(masterVolume * 100).roundToInt()}%", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Slider(
                                value = masterVolume,
                                onValueChange = onChangeVolume,
                                valueRange = 0f..1f,
                                colors = SliderDefaults.colors(thumbColor = Color(0xFFFFD600), activeTrackColor = Color(0xFFFFD600))
                            )
                        }
                    }
                }
            }
        }
    }
}
