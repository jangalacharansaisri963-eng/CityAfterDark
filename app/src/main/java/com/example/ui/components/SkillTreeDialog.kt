package com.example.ui.components

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.features.SkillTreeSystem

@Composable
fun SkillTreeDialog(
    skillTree: SkillTreeSystem,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0x99000000))
            .clickable { onDismiss() }
            .testTag("dialog_skill_tree"),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            color = Color(0xFF0D1117),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFFD600)),
            modifier = Modifier
                .width(420.dp)
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
                        Icon(Icons.Default.MilitaryTech, contentDescription = null, tint = Color(0xFFFFD600), modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("RANK & SKILL PERKS", color = Color.White, fontWeight = FontWeight.Black, fontSize = 13.sp)
                            Text("Title: ${skillTree.rankTitle} (Lvl ${skillTree.playerLevel})", color = Color(0xFFFFD600), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // XP & Skill points bar
                Surface(
                    color = Color(0xFF161B22),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Available Skill Points: ${skillTree.skillPointsAvailable}", color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("XP: ${skillTree.playerXP} / ${skillTree.playerLevel * 500}", color = Color(0xFFB0BEC5), fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    }
                }

                // Perks List
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    items(skillTree.availablePerks) { perk ->
                        val isUnlocked = skillTree.hasPerk(perk.id)
                        Surface(
                            color = if (isUnlocked) Color(0xFF14241B) else Color(0xFF161B22),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isUnlocked) Color(0xFF00E676) else Color(0x33FFFFFF)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(perk.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(perk.description, color = Color(0xFF90A4AE), fontSize = 10.sp)
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                if (isUnlocked) {
                                    Surface(
                                        color = Color(0x3300E676),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text("ACTIVE", color = Color(0xFF00E676), fontWeight = FontWeight.Black, fontSize = 10.sp)
                                        }
                                    }
                                } else {
                                    Button(
                                        onClick = { skillTree.unlockPerk(perk) },
                                        enabled = skillTree.skillPointsAvailable >= perk.costSkillPoints,
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD600)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.height(34.dp)
                                    ) {
                                        Text("${perk.costSkillPoints} SP", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 11.sp)
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
