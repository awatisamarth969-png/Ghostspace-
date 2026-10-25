package com.ghostspace.privacy.data

import android.content.Context
import android.net.Uri
import com.ghostspace.privacy.data.model.VaultItem
import com.ghostspace.privacy.security.CryptoEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.UUID

class VaultRepository(private val context: Context) {

    private val cryptoEngine = CryptoEngine()
    private val vaultDir = File(context.filesDir, "vault_encrypted")

    // In-memory items list (mirrored to Room DB)
    private val items = mutableListOf<VaultItem>()

    suspend fun importFile(uri: Uri, type: String, fileName: String, mimeType: String, isDecoy: Boolean = false): VaultItem = withContext(Dispatchers.IO) {
        val id = UUID.randomUUID().toString()
        val encryptedFile = File(vaultDir, "$id.enc")

        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(encryptedFile).use { outputStream ->
                cryptoEngine.encryptStream(inputStream, outputStream)
            }
        } ?: throw IllegalStateException("Could not open input stream for $uri")

        val item = VaultItem(
            id = id,
            name = fileName,
            type = type,
            encryptedFileName = encryptedFile.name,
            sizeBytes = encryptedFile.length(),
            mimeType = mimeType,
            createdAt = System.currentTimeMillis(),
            isDecoy = isDecoy
        )

        items.add(item)
        item
    }

    suspend fun saveNote(title: String, content: String, isDecoy: Boolean = false): VaultItem = withContext(Dispatchers.IO) {
        val id = UUID.randomUUID().toString()
        val encryptedFile = File(vaultDir, "$id.note.enc")
        
        val bytes = content.toByteArray(Charsets.UTF_8)
        bytes.inputStream().use { input ->
            FileOutputStream(encryptedFile).use { output ->
                cryptoEngine.encryptStream(input, output)
            }
        }

        val item = VaultItem(
            id = id,
            name = title,
            type = "NOTE",
            encryptedFileName = encryptedFile.name,
            sizeBytes = encryptedFile.length(),
            mimeType = "text/plain",
            createdAt = System.currentTimeMillis(),
            isDecoy = isDecoy
        )

        items.add(item)
        item
    }

    suspend fun getDecryptedBytes(item: VaultItem): ByteArray = withContext(Dispatchers.IO) {
        val file = File(vaultDir, item.encryptedFileName)
        FileInputStream(file).use { input ->
            cryptoEngine.decryptBytes(input)
        }
    }

    suspend fun deleteItem(item: VaultItem): Boolean = withContext(Dispatchers.IO) {
        val file = File(vaultDir, item.encryptedFileName)
        if (file.exists()) {
            file.delete()
        }
        items.remove(item)
    }

    fun getItems(decoy: Boolean): List<VaultItem> {
        return items.filter { it.isDecoy == decoy }
    }
}
