package com.example.spera.ui.screens.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Background = Color(0xFF0F0D14)
private val TextPrimary = Color(0xFFF5F3F7)
private val TextMuted = Color(0xFF9A93A8)

/**
 * Contenu d'attente pour les sections pas encore développées (structure de
 * navigation en place, écran à implémenter dans une US dédiée).
 */
@Composable
fun ComingSoon(icon: String, title: String) {
    Box(
        modifier = Modifier.fillMaxSize().background(Background).padding(28.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(icon, fontSize = 48.sp)
            Text(
                title,
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp),
            )
            Text(
                "Bientôt disponible.",
                color = TextMuted,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 6.dp),
            )
        }
    }
}
