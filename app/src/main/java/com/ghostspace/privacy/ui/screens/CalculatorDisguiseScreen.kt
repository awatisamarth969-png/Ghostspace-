package com.ghostspace.privacy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CalculatorDisguiseScreen(
    onSecretPinEntered: (String) -> Unit
) {
    var display by remember { mutableStateOf("0") }
    var currentInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Calculator Screen Display
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(bottom = 24.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            Text(
                text = display,
                fontSize = 48.sp,
                fontWeight = FontWeight.Light,
                color = Color.White,
                textAlign = TextAlign.End,
                maxLines = 1
            )
        }

        // Keypad Grid
        val buttons = listOf(
            listOf("C", "±", "%", "÷"),
            listOf("7", "8", "9", "×"),
            listOf("4", "5", "6", "-"),
            listOf("1", "2", "3", "+"),
            listOf("0", ".", "=")
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            for (row in buttons) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    for (btn in row) {
                        val isOperator = btn in listOf("÷", "×", "-", "+", "=")
                        val isSpecial = btn in listOf("C", "±", "%")
                        val weight = if (btn == "0") 2f else 1f

                        Button(
                            onClick = {
                                when (btn) {
                                    "C" -> {
                                        display = "0"
                                        currentInput = ""
                                    }
                                    "=" -> {
                                        // Trigger check if input matches master PIN!
                                        onSecretPinEntered(currentInput)
                                        // Standard fake evaluation fallback
                                        display = try {
                                            "0"
                                        } catch (e: Exception) {
                                            "Error"
                                        }
                                        currentInput = ""
                                    }
                                    else -> {
                                        if (display == "0" && btn !in listOf(".", "+", "-", "×", "÷")) {
                                            display = btn
                                        } else {
                                            display += btn
                                        }
                                        if (btn in "0123456789") {
                                            currentInput += btn
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(weight)
                                .height(72.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = when {
                                    isOperator -> Color(0xFFF59E0B)
                                    isSpecial -> Color(0xFFA3A3A3)
                                    else -> Color(0xFF262626)
                                },
                                contentColor = when {
                                    isSpecial -> Color.Black
                                    else -> Color.White
                                }
                            )
                        ) {
                            Text(text = btn, fontSize = 24.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
}
