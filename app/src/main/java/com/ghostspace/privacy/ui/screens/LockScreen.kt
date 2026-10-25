package com.ghostspace.privacy.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ghostspace.privacy.ui.components.GhostLogo
import com.ghostspace.privacy.ui.theme.PurplePrimary

@Composable
fun LockScreen(
    isPinConfigured: Boolean,
    onPinEntered: (String) -> Unit,
    onSetupPin: (String) -> Unit,
    onBiometricClick: () -> Unit
) {
    var pin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var isConfirming by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Logo & Header
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(24.dp))
            GhostLogo(size = 90.dp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "GhostSpace",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = when {
                    !isPinConfigured && !isConfirming -> "Create your 4-digit Master PIN"
                    !isPinConfigured && isConfirming -> "Confirm your Master PIN"
                    else -> "Enter PIN to access your vault"
                },
                fontSize = 14.sp,
                color = Color(0xFF94A3B8)
            )

            // PIN Dots Indicator
            Spacer(modifier = Modifier.height(28.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val currentLength = pin.length
                for (i in 0 until 4) {
                    val isFilled = i < currentLength
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(if (isFilled) PurplePrimary else Color(0xFF2E214A))
                    )
                }
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = errorMessage!!, color = Color(0xFFEF4444), fontSize = 12.sp)
            }
        }

        // Numeric Keypad
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            val rows = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf("BIOMETRIC", "0", "DELETE")
            )

            for (row in rows) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (item in row) {
                        when (item) {
                            "BIOMETRIC" -> {
                                IconButton(
                                    onClick = onBiometricClick,
                                    modifier = Modifier.size(72.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Fingerprint,
                                        contentDescription = "Biometric Unlock",
                                        tint = PurplePrimary,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                            }
                            "DELETE" -> {
                                IconButton(
                                    onClick = {
                                        if (pin.isNotEmpty()) pin = pin.dropLast(1)
                                    },
                                    modifier = Modifier.size(72.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Backspace,
                                        contentDescription = "Backspace",
                                        tint = Color.White,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                            else -> {
                                Button(
                                    onClick = {
                                        if (pin.length < 4) {
                                            pin += item
                                            if (pin.length == 4) {
                                                if (!isPinConfigured) {
                                                    if (!isConfirming) {
                                                        confirmPin = pin
                                                        pin = ""
                                                        isConfirming = true
                                                    } else {
                                                        if (pin == confirmPin) {
                                                            onSetupPin(pin)
                                                        } else {
                                                            errorMessage = "PINs do not match. Try again."
                                                            pin = ""
                                                            isConfirming = false
                                                        }
                                                    }
                                                } else {
                                                    onPinEntered(pin)
                                                    pin = ""
                                                }
                                            }
                                        }
                                    },
                                    modifier = Modifier.size(72.dp),
                                    shape = CircleShape,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF161026)
                                    )
                                ) {
                                    Text(
                                        text = item,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}
