package com.ghostspace.privacy

import android.app.Application
import com.ghostspace.privacy.data.VaultRepository
import java.io.File

class GhostSpaceApp : Application() {

    lateinit var repository: VaultRepository
        private set

    override fun onCreate() {
        super.onCreate()
        
        // Ensure private encrypted directory exists
        val vaultDir = File(filesDir, "vault_encrypted")
        if (!vaultDir.exists()) {
            vaultDir.mkdirs()
        }

        // Initialize repository
        repository = VaultRepository(this)
    }
}
