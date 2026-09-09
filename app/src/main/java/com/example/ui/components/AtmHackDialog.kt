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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.features.AtmHackGame

@Composable
fun AtmHackDialog(
    onRewardSuccess: (amount: Int) -> Unit,
    onAlarmTriggered: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val game = remember { AtmHackGame().apply { startNewHack() } }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0x99000000))
            .clickable { onDismiss() }
            .testTag("dialog_atm_hack"),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            color = Color(0xFF0A0E17),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(2.dp, if (game.isSuccess) Color(0xFF00E676) else if (game.isFailed) Color(0xFFFF1744) else Color(0xFF00E5FF)),
            modifier = Modifier
                .width(340.dp)
                .clickable(enabled = false) {}
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("ATM CYBER-BYPASS", color = Color.White, fontWeight = FontWeight.Black, fontSize = 13.sp)
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Terminal Display Box
                Surface(
                    color = Color(0xFF05080E),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E2633)),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = game.feedbackMessage,
                            color = if (game.isSuccess) Color(0xFF00E676) else if (game.isFailed) Color(0xFFFF5252) else Color(0xFFFFD54F),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (game.currentGuess.isEmpty()) "• • • •" else game.currentGuess.padEnd(4, '•').chunked(1).joinToString(" "),
                            color = Color(0xFF00E5FF),
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 4.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Keypad grid 1-9, backspace, 0, submit
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (row in 0..2) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            for (col in 1..3) {
                                val num = row * 3 + col
                                KeypadButton(label = num.toString()) {
                                    game.enterDigit(num)
                                }
                            }
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        KeypadButton(label = "⌫") { game.deleteDigit() }
                        KeypadButton(label = "0") { game.enterDigit(0) }
                        KeypadButton(label = "OK", isSubmit = true) {
                            if (game.submitGuess()) {
                                onRewardSuccess(2850)
                            } else if (game.isFailed) {
                                onAlarmTriggered()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun KeypadButton(
    label: String,
    isSubmit: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = if (isSubmit) Color(0xFF00C853) else Color(0xFF161E2E),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSubmit) Color.White else Color(0x3300E5FF)),
        modifier = Modifier.size(width = 68.dp, height = 44.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
