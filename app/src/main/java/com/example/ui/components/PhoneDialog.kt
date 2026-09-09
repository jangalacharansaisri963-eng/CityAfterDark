package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalTaxi
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.character.Player
import com.example.game.world.District

enum class PhoneApp {
    HOME,
    BANK,
    CONTACTS,
    TAXI,
    WEATHER,
    CRYPTO
}

data class ContactItem(
    val name: String,
    val role: String,
    val avatarColor: Color,
    val quote: String
)

@Composable
fun DanPhoneDialog(
    player: Player,
    currentDistrict: District,
    timeString: String,
    weatherString: String,
    onCallContact: (name: String, message: String) -> Unit,
    onDispatchTaxi: (District) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentApp by remember { mutableStateOf(PhoneApp.HOME) }
    var cryptoPrice by remember { mutableDoubleStateOf(342.50) }
    var cryptoHolding by remember { mutableIntStateOf(2) }
    var bankAccountBalance by remember { mutableIntStateOf(player.money) }
    var phoneToast by remember { mutableStateOf<String?>(null) }

    val contacts = remember {
        listOf(
            ContactItem("Elena Rostova", "Syndicate Intel Broker", Color(0xFF00E5FF), "Stay off main transit lines tonight. Police scans are elevated."),
            ContactItem("Marcus Vance", "Underground Chop-Shop Chief", Color(0xFFFF9100), "Got fresh ceramic brake pads in stock if you need a refit."),
            ContactItem("The Fixer", "Black Market Coordinator", Color(0xFFFFD600), "Target dropped the package in Financial District. Collect it quickly."),
            ContactItem("Officer Miller", "Compromised Dispatcher", Color(0xFFFF1744), "I can jam local radar sweeps for five minutes, but it will cost you.")
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0x88000000))
            .clickable { onDismiss() }
            .testTag("dialog_danphone"),
        contentAlignment = Alignment.Center
    ) {
        // Phone device shell
        Surface(
            modifier = Modifier
                .width(360.dp)
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(32.dp))
                .border(3.dp, Color(0xFF37474F), RoundedCornerShape(32.dp))
                .clickable(enabled = false) {}, // prevent click-through
            color = Color(0xFF0D1117)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                // Top notch bar with phone camera & time
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = timeString,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    // Camera speaker notch
                    Box(
                        modifier = Modifier
                            .size(width = 68.dp, height = 12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF1E2633))
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "5G", color = Color(0xFF00E5FF), fontSize = 11.sp, fontWeight = FontWeight.Black)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "94%", color = Color(0xFF00E676), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Header / App Title
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when (currentApp) {
                            PhoneApp.HOME -> "DanOS v4.2"
                            PhoneApp.BANK -> "DAN-BANK MOBILE"
                            PhoneApp.CONTACTS -> "SECURE CONTACTS"
                            PhoneApp.TAXI -> "DAN-RIDE TAXI"
                            PhoneApp.WEATHER -> "METEO SAT RADAR"
                            PhoneApp.CRYPTO -> "CYBER-EXCHANGE"
                        },
                        color = Color(0xFF00E5FF),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close Phone", tint = Color.White)
                    }
                }

                if (phoneToast != null) {
                    Surface(
                        color = Color(0xCC00E5FF),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = phoneToast!!,
                            color = Color.Black,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                }

                // Phone App Content
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    when (currentApp) {
                        PhoneApp.HOME -> {
                            Column(
                                modifier = Modifier.fillMaxSize().padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // Welcome widget
                                Surface(
                                    color = Color(0xFF161B22),
                                    shape = RoundedCornerShape(16.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x3300E5FF)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(text = "Welcome back, Vance", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text(text = "District: ${currentDistrict.title}", color = Color(0xFF80D8FF), fontSize = 11.sp)
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "Wallet: $%,d".format(player.money),
                                            color = Color(0xFFFFD700),
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 18.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }

                                // App Grid (3 x 2)
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                    PhoneAppIcon(Icons.Default.AccountBalance, "DanBank", Color(0xFF00E676)) { currentApp = PhoneApp.BANK }
                                    PhoneAppIcon(Icons.Default.Call, "Contacts", Color(0xFF00B0FF)) { currentApp = PhoneApp.CONTACTS }
                                    PhoneAppIcon(Icons.Default.LocalTaxi, "DanRide", Color(0xFFFFD600)) { currentApp = PhoneApp.TAXI }
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                    PhoneAppIcon(Icons.Default.CloudQueue, "Weather", Color(0xFF7C4DFF)) { currentApp = PhoneApp.WEATHER }
                                    PhoneAppIcon(Icons.Default.ShowChart, "Crypto", Color(0xFFFF4081)) { currentApp = PhoneApp.CRYPTO }
                                }
                            }
                        }

                        PhoneApp.BANK -> {
                            Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                                Surface(
                                    color = Color(0xFF16241D),
                                    shape = RoundedCornerShape(14.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E676)),
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text("DanBank Offshore Vault", color = Color(0xFF00E676), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            "$%,d".format(bankAccountBalance),
                                            color = Color.White,
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Black,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        Text("Available in Untraceable Credit", color = Color(0xFF81C784), fontSize = 10.sp)
                                    }
                                }

                                Button(
                                    onClick = {
                                        player.money += 250
                                        bankAccountBalance = player.money
                                        phoneToast = "ATM withdrawal: +$250 transferred to pocket."
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                                ) {
                                    Text("Withdraw $250 Cash", color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                                Button(
                                    onClick = {
                                        if (player.money >= 500) {
                                            player.money -= 500
                                            bankAccountBalance = player.money
                                            phoneToast = "Safe Deposit: $500 secured in offshore account."
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF263238)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Deposit $500 to Vault", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        PhoneApp.CONTACTS -> {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize().padding(horizontal = 6.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(contacts) { c ->
                                    Surface(
                                        color = Color(0xFF161B22),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth().clickable {
                                            onCallContact(c.name, c.quote)
                                            phoneToast = "Call connected with ${c.name}"
                                        }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(CircleShape)
                                                    .background(c.avatarColor),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(text = c.name.take(1), color = Color.Black, fontWeight = FontWeight.Black)
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(text = c.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                Text(text = c.role, color = Color(0xFF90A4AE), fontSize = 10.sp)
                                            }
                                            Icon(Icons.Default.Call, contentDescription = "Call", tint = Color(0xFF00E676), modifier = Modifier.size(20.dp))
                                        }
                                    }
                                }
                            }
                        }

                        PhoneApp.TAXI -> {
                            Column(modifier = Modifier.fillMaxSize().padding(10.dp)) {
                                Text("DanRide Automated Taxi Service", color = Color(0xFFFFD600), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Select destination district ($150 flat fare):", color = Color.White, fontSize = 11.sp, modifier = Modifier.padding(vertical = 6.dp))
                                LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    items(District.values().filter { it != currentDistrict }) { district ->
                                        Surface(
                                            color = Color(0xFF161B22),
                                            shape = RoundedCornerShape(10.dp),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(district.minimapColor)),
                                            modifier = Modifier.fillMaxWidth().clickable {
                                                if (player.money >= 150) {
                                                    player.money -= 150
                                                    onDispatchTaxi(district)
                                                    phoneToast = "Cab dispatched to ${district.title}!"
                                                } else {
                                                    phoneToast = "Insufficient funds for taxi fare!"
                                                }
                                            }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(10.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column {
                                                    Text(district.title, color = Color(district.minimapColor), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                    Text(district.description, color = Color(0xFFB0BEC5), fontSize = 10.sp, maxLines = 1)
                                                }
                                                Text("$150", color = Color(0xFFFFD600), fontWeight = FontWeight.Black, fontSize = 12.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        PhoneApp.WEATHER -> {
                            Column(modifier = Modifier.fillMaxSize().padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.CloudQueue, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(54.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("METRO WEATHER RADAR", color = Color(0xFF00E5FF), fontWeight = FontWeight.Black, fontSize = 14.sp)
                                Text(weatherString, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(14.dp))
                                Surface(
                                    color = Color(0xFF161B22),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text("Atmospheric Sensor: 18°C", color = Color(0xFFB0BEC5), fontSize = 11.sp)
                                        Text("Road Grip: 88% Traction", color = Color(0xFF81C784), fontSize = 11.sp)
                                        Text("Precipitation Risk: High (Midnight fog)", color = Color(0xFFFFD54F), fontSize = 11.sp)
                                        Text("Police Aerial Patrol: Active", color = Color(0xFFFF5252), fontSize = 11.sp)
                                    }
                                }
                            }
                        }

                        PhoneApp.CRYPTO -> {
                            Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                                Text("CYBR Coin Exchange", color = Color(0xFFFF4081), fontWeight = FontWeight.Black, fontSize = 14.sp)
                                Text("Price: $${"%.2f".format(cryptoPrice)} / coin", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Text("Your Wallet: $cryptoHolding CYBR", color = Color(0xFF80D8FF), fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(14.dp))

                                Button(
                                    onClick = {
                                        val cost = cryptoPrice.toInt()
                                        if (player.money >= cost) {
                                            player.money -= cost
                                            cryptoHolding++
                                            cryptoPrice += 15.20 // market reaction
                                            phoneToast = "Bought 1 CYBR coin!"
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                                ) {
                                    Text("Buy 1 CYBR ($${cryptoPrice.toInt()})", color = Color.Black, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = {
                                        if (cryptoHolding > 0) {
                                            cryptoHolding--
                                            player.money += cryptoPrice.toInt()
                                            cryptoPrice = (cryptoPrice - 11.40).coerceAtLeast(80.0)
                                            phoneToast = "Sold 1 CYBR coin!"
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF1744)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Sell 1 CYBR (+$${cryptoPrice.toInt()})", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Bottom Home Bar Button
                Surface(
                    onClick = {
                        if (currentApp != PhoneApp.HOME) {
                            currentApp = PhoneApp.HOME
                        } else {
                            onDismiss()
                        }
                    },
                    color = Color(0xFF1E2633),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Home, contentDescription = "Home", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun PhoneAppIcon(icon: ImageVector, label: String, color: Color, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Brush.linearGradient(listOf(color.copy(alpha = 0.85f), color.copy(alpha = 0.45f))))
                .border(1.dp, color, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = Color.White, modifier = Modifier.size(28.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun PhoneDialog(
    engine: com.example.game.GameEngine,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    DanPhoneDialog(
        player = engine.player,
        currentDistrict = engine.currentDistrict,
        timeString = engine.dayNightSystem.getFormattedTime(),
        weatherString = engine.dayNightSystem.weather.displayName,
        onCallContact = { name, msg ->
            engine.saveToastMessage = "$name: \"$msg\""
        },
        onDispatchTaxi = { district ->
            val center = when (district) {
                District.FINANCIAL -> com.example.engine.math.Vector3(0f, 0f, 160f)
                District.DOWNTOWN -> com.example.engine.math.Vector3(0f, 0f, 0f)
                District.WATERFRONT -> com.example.engine.math.Vector3(-200f, 0f, 0f)
                District.OLD_TOWN -> com.example.engine.math.Vector3(-160f, 0f, 160f)
                District.INDUSTRIAL -> com.example.engine.math.Vector3(160f, 0f, -160f)
                else -> com.example.engine.math.Vector3(0f, 0f, 0f)
            }
            if (engine.player.money >= 150) {
                engine.player.money -= 150
                engine.fastTravelTo(center)
                engine.saveToastMessage = "Taxi dispatched to ${district.title} (-$150)"
                onClose()
            } else {
                engine.saveToastMessage = "Insufficient funds for taxi fare!"
            }
        },
        onDismiss = onClose,
        modifier = modifier
    )
}

