package com.ghostspace.privacy

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import com.ghostspace.privacy.security.BiometricHelper
import com.ghostspace.privacy.security.PinManager
import com.ghostspace.privacy.ui.screens.CalculatorDisguiseScreen
import com.ghostspace.privacy.ui.screens.LockScreen
import com.ghostspace.privacy.ui.screens.VaultHomeScreen
import com.ghostspace.privacy.ui.theme.GhostSpaceTheme

class MainActivity : FragmentActivity() {

    private lateinit var pinManager: PinManager
    private lateinit var biometricHelper: BiometricHelper
    
    // In-memory unlock state
    private var isUnlocked by mutableStateOf(false)
    private var isDecoySession by mutableStateOf(false)
    private var isCalculatorDisguiseActive by mutableStateOf(false)
    private var isDarkMode by mutableStateOf(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Privacy First: Block screenshots & Recents screen preview
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        pinManager = PinManager(this)
        biometricHelper = BiometricHelper(this)

        setContent {
            GhostSpaceTheme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when {
                        isCalculatorDisguiseActive && !isUnlocked -> {
                            CalculatorDisguiseScreen(
                                onSecretPinEntered = { pin ->
                                    handlePinEntry(pin)
                                }
                            )
                        }
                        !isUnlocked -> {
                            LockScreen(
                                isPinConfigured = pinManager.isPinConfigured(),
                                onPinEntered = { pin ->
                                    handlePinEntry(pin)
                                },
                                onSetupPin = { newPin ->
                                    pinManager.setMasterPin(newPin)
                                    isUnlocked = true
                                },
                                onBiometricClick = {
                                    biometricHelper.showBiometricPrompt(
                                        onSuccess = { isUnlocked = true },
                                        onError = { /* display toast / error */ }
                                    )
                                }
                            )
                        }
                        else -> {
                            VaultHomeScreen(
                                isDecoy = isDecoySession,
                                onLock = { lockVault() },
                                onToggleTheme = { isDarkMode = !isDarkMode }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun handlePinEntry(pin: String) {
        when (pinManager.verifyPin(pin)) {
            PinManager.VerifyResult.SUCCESS_MASTER -> {
                isDecoySession = false
                isUnlocked = true
                isCalculatorDisguiseActive = false
            }
            PinManager.VerifyResult.SUCCESS_DECOY -> {
                isDecoySession = true
                isUnlocked = true
                isCalculatorDisguiseActive = false
            }
            else -> {
                // Shake keypad & record failed attempt
            }
        }
    }

    private fun lockVault() {
        isUnlocked = false
        isDecoySession = false
    }

    override fun onPause() {
        super.onPause()
        // Instant security auto-lock when app moves to background
        lockVault()
    }
}
