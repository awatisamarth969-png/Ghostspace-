package com.ghostspace.privacy.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.io.InputStream
import java.io.OutputStream
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Authentic AES-256-GCM encryption backed by hardware Android KeyStore.
 * Compatible with Android 6.0 (API 23) and above.
 */
class CryptoEngine {

    companion object {
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val KEY_ALIAS = "GhostSpaceMasterVaultKey"
        private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_GCM
        private const val PADDING = KeyProperties.ENCRYPTION_PADDING_NONE
        private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
        private const val GCM_TAG_LENGTH = 128
        private const val IV_SIZE = 12 // 96-bit IV standard for GCM
    }

    private val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply {
        load(null)
    }

    init {
        ensureMasterKeyExists()
    }

    private fun ensureMasterKeyExists() {
        if (!keyStore.containsAlias(KEY_ALIAS)) {
            val keyGenerator = KeyGenerator.getInstance(ALGORITHM, ANDROID_KEYSTORE)
            val parameterSpec = KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(BLOCK_MODE)
                .setEncryptionPaddings(PADDING)
                .setKeySize(256)
                .setUserAuthenticationRequired(false) // Pin & Biometric flow is managed at application level
                .setRandomizedEncryptionRequired(true)
                .build()

            keyGenerator.init(parameterSpec)
            keyGenerator.generateKey()
        }
    }

    private fun getSecretKey(): SecretKey {
        val entry = keyStore.getEntry(KEY_ALIAS, null) as KeyStore.SecretKeyEntry
        return entry.secretKey
    }

    /**
     * Encrypts an input stream to an output stream.
     * Writes 12-byte IV at the beginning of the file.
     */
    fun encryptStream(inputStream: InputStream, outputStream: OutputStream) {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())

        val iv = cipher.iv
        outputStream.write(iv) // Write IV prefix

        CipherOutputStream(outputStream, cipher).use { cipherOut ->
            inputStream.copyTo(cipherOut)
        }
    }

    /**
     * Decrypts an input stream where the first 12 bytes are the GCM IV.
     */
    fun decryptStream(inputStream: InputStream, outputStream: OutputStream) {
        val iv = ByteArray(IV_SIZE)
        val readBytes = inputStream.read(iv)
        if (readBytes != IV_SIZE) {
            throw IllegalArgumentException("Invalid encrypted file header: Missing GCM IV")
        }

        val cipher = Cipher.getInstance(TRANSFORMATION)
        val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), spec)

        CipherInputStream(inputStream, cipher).use { cipherIn ->
            cipherIn.copyTo(outputStream)
        }
    }

    /**
     * Decrypts file in-memory and returns decrypted bytes.
     */
    fun decryptBytes(inputStream: InputStream): ByteArray {
        val iv = ByteArray(IV_SIZE)
        val readBytes = inputStream.read(iv)
        if (readBytes != IV_SIZE) {
            throw IllegalArgumentException("Corrupt ciphertext payload")
        }

        val cipher = Cipher.getInstance(TRANSFORMATION)
        val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), spec)

        return CipherInputStream(inputStream, cipher).use { it.readBytes() }
    }
}
