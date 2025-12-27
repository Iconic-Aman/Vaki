package com.example.to_do_list.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun VakiDrawerContent() {
    val configuration = LocalConfiguration.current
    val drawerWidth = (configuration.screenWidthDp * 0.7).dp

    ModalDrawerSheet(
        modifier = Modifier.width(drawerWidth),
        drawerContainerColor = Color.White,
        drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp),
        windowInsets = WindowInsets.systemBars // Ensures smooth handling of status bar insets
    ) {
        Spacer(Modifier.height(48.dp))
        Text(
            "Vaki Menu",
            modifier = Modifier.padding(24.dp),
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )
        NavigationDrawerItem(
            label = { Text("Set Alarm") },
            selected = false,
            onClick = { /* Set Alarm Logic */ },
            icon = { Icon(Icons.Default.Notifications, contentDescription = null) },
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        NavigationDrawerItem(
            label = { Text("Add Notes") },
            selected = false,
            onClick = { /* Add Notes Logic */ },
            icon = { Icon(Icons.Default.Edit, contentDescription = null) },
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }
}