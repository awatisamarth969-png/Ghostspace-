package com.ghostspace.privacy.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vault_items")
data class VaultItem(
    @PrimaryKey
    val id: String,
    val name: String,
    val type: String, // PHOTO, VIDEO, AUDIO, NOTE, FILE
    val encryptedFileName: String,
    val sizeBytes: Long,
    val mimeType: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isDecoy: Boolean = false,
    val noteContentCipher: String? = null
)
