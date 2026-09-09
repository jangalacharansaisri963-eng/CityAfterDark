package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.character.Player
import com.example.game.features.RealEstateSystem

@Composable
fun RealEstateDialog(
    realEstate: RealEstateSystem,
    player: Player,
    onFastTravel: (com.example.engine.math.Vector3) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0x99000000))
            .clickable { onDismiss() }
            .testTag("dialog_real_estate"),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            color = Color(0xFF0D1117),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF00E5FF)),
            modifier = Modifier
                .width(440.dp)
                .clickable(enabled = false) {}
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Apartment, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("METROPOLIS REAL ESTATE", color = Color.White, fontWeight = FontWeight.Black, fontSize = 13.sp)
                            Text("Safehouses, Chop-Shops & Empire Assets", color = Color(0xFF90A4AE), fontSize = 10.sp)
                        }
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Passive income collecting banner
                Surface(
                    color = Color(0xFF14241B),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E676)),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = Color(0xFFFFD600), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text("Uncollected Passive Revenue", color = Color(0xFF81C784), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text("$%,d".format(realEstate.uncollectedRevenue), color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                            }
                        }
                        Button(
                            onClick = {
                                val amount = realEstate.collectRevenue()
                                player.money += amount
                            },
                            enabled = realEstate.uncollectedRevenue > 0,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text("COLLECT", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 11.sp)
                        }
                    }
                }

                // Properties list
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    items(realEstate.properties) { prop ->
                        val isOwned = realEstate.ownedPropertyIds.contains(prop.id)
                        Surface(
                            color = Color(0xFF161B22),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isOwned) Color(0xFF00E676) else Color(0x33FFFFFF)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(prop.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("${prop.district.title} • +$${prop.dailyRevenue}/day", color = Color(prop.district.minimapColor), fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                    if (isOwned) {
                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Button(
                                                onClick = {
                                                    onFastTravel(prop.location)
                                                    onDismiss()
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.height(30.dp)
                                            ) {
                                                Text("WARP", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                            }
                                            Surface(
                                                color = Color(0x3300E676),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text(
                                                    "OWNED",
                                                    color = Color(0xFF00E676),
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 10.sp,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                                )
                                            }
                                        }
                                    } else {
                                        Button(
                                            onClick = {
                                                if (realEstate.buyProperty(prop, player.money)) {
                                                    player.money -= prop.price
                                                }
                                            },
                                            enabled = player.money >= prop.price,
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD600)),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.height(32.dp)
                                        ) {
                                            Text("BUY $%,d".format(prop.price), color = Color.Black, fontWeight = FontWeight.Black, fontSize = 10.sp)
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(prop.description, color = Color(0xFF90A4AE), fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
