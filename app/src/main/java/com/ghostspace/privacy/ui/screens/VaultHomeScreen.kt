package com.ghostspace.privacy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ghostspace.privacy.ui.theme.PurplePrimary

@Composable
fun VaultHomeScreen(
    isDecoy: Boolean,
    onLock: () -> Unit,
    onToggleTheme: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Photos", "Videos", "Notes", "Files")

    Scaffold(
        topBar = {
            Column {
                if (isDecoy) {
                    // Discreet Decoy Mode notice
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF334155))
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Decoy Vault Active",
                            fontSize = 11.sp,
                            color = Color(0xFFCBD5E1)
                        )
                    }
                }
                SmallTopAppBar(
                    title = {
                        Text(
                            text = "GhostSpace",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    },
                    actions = {
                        IconButton(onClick = onToggleTheme) {
                            Icon(Icons.Default.Brightness4, contentDescription = "Toggle Theme", tint = Color.White)
                        }
                        IconButton(onClick = onLock) {
                            Icon(Icons.Default.Lock, contentDescription = "Lock Vault", tint = PurplePrimary)
                        }
                    },
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = Color(0xFF0A0812)
                    )
                )
                // Tab Row
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color(0xFF0A0812),
                    contentColor = PurplePrimary
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title, fontWeight = FontWeight.SemiBold) }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Launch File / Camera picker */ },
                containerColor = PurplePrimary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Item")
            }
        },
        containerColor = Color(0xFF0A0812)
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = when (selectedTab) {
                        0 -> Icons.Default.PhotoLibrary
                        1 -> Icons.Default.VideoLibrary
                        2 -> Icons.Default.Notes
                        else -> Icons.Default.FolderZip
                    },
                    contentDescription = null,
                    tint = Color(0xFF4C1D95),
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Encrypted ${tabs[selectedTab]} Vault",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Zero unencrypted traces on Android storage",
                    fontSize = 13.sp,
                    color = Color(0xFF94A3B8)
                )
            }
        }
    }
}
