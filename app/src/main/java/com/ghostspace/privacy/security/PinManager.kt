package com.ghostspace.privacy.security

import android.content.Context
import android.util.Base64
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

class PinManager(context: Context) {

    private val prefs = context.getSharedPreferences("ghost_pin_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_PIN_HASH = "master_pin_hash"
        private const val KEY_PIN_SALT = "master_pin_salt"
        private const val KEY_DECOY_HASH = "decoy_pin_hash"
        private const val KEY_DECOY_SALT = "decoy_pin_salt"
        private const val KEY_FAILED_ATTEMPTS = "failed_attempts"
        private const val KEY_LOCKOUT_UNTIL = "lockout_until"
        private const val ITERATIONS = 100_000
        private const val KEY_LENGTH = 256
    }

    fun isPinConfigured(): Boolean {
        return prefs.contains(KEY_PIN_HASH)
    }

    fun setMasterPin(pin: String) {
        val salt = generateSalt()
        val hash = hashPin(pin, salt)
        prefs.edit()
            .putString(KEY_PIN_HASH, hash)
            .putString(KEY_PIN_SALT, Base64.encodeToString(salt, Base64.NO_WRAP))
            .apply()
    }

    fun setDecoyPin(pin: String) {
        val salt = generateSalt()
        val hash = hashPin(pin, salt)
        prefs.edit()
            .putString(KEY_DECOY_HASH, hash)
            .putString(KEY_DECOY_SALT, Base64.encodeToString(salt, Base64.NO_WRAP))
            .apply()
    }

    enum class VerifyResult {
        SUCCESS_MASTER,
        SUCCESS_DECOY,
        FAILED,
        LOCKED_OUT
    }

    fun verifyPin(pin: String): VerifyResult {
        val now = System.currentTimeMillis()
        val lockoutUntil = prefs.getLong(KEY_LOCKOUT_UNTIL, 0L)
        if (now < lockoutUntil) {
            return VerifyResult.LOCKED_OUT
        }

        // Verify Master PIN
        val masterHash = prefs.getString(KEY_PIN_HASH, null)
        val masterSaltStr = prefs.getString(KEY_PIN_SALT, null)
        if (masterHash != null && masterSaltStr != null) {
            val salt = Base64.decode(masterSaltStr, Base64.NO_WRAP)
            if (hashPin(pin, salt) == masterHash) {
                resetFailedAttempts()
                return VerifyResult.SUCCESS_MASTER
            }
        }

        // Verify Decoy PIN
        val decoyHash = prefs.getString(KEY_DECOY_HASH, null)
        val decoySaltStr = prefs.getString(KEY_DECOY_SALT, null)
        if (decoyHash != null && decoySaltStr != null) {
            val salt = Base64.decode(decoySaltStr, Base64.NO_WRAP)
            if (hashPin(pin, salt) == decoyHash) {
                resetFailedAttempts()
                return VerifyResult.SUCCESS_DECOY
            }
        }

        // Record failure
        recordFailedAttempt()
        return VerifyResult.FAILED
    }

    private fun recordFailedAttempt() {
        val attempts = prefs.getInt(KEY_FAILED_ATTEMPTS, 0) + 1
        val editor = prefs.edit().putInt(KEY_FAILED_ATTEMPTS, attempts)
        if (attempts >= 5) {
            // Lockout for 30 seconds after 5 failed attempts
            editor.putLong(KEY_LOCKOUT_UNTIL, System.currentTimeMillis() + 30_000L)
        }
        editor.apply()
    }

    private fun resetFailedAttempts() {
        prefs.edit().putInt(KEY_FAILED_ATTEMPTS, 0).putLong(KEY_LOCKOUT_UNTIL, 0L).apply()
    }

    private fun generateSalt(): ByteArray {
        val random = SecureRandom()
        val salt = ByteArray(16)
        random.nextBytes(salt)
        return salt
    }

    private fun hashPin(pin: String, salt: ByteArray): String {
        val spec = PBEKeySpec(pin.toCharArray(), salt, ITERATIONS, KEY_LENGTH)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val hash = factory.generateSecret(spec).encoded
        return Base64.encodeToString(hash, Base64.NO_WRAP)
    }
}
