package com.example.spera.ui.screens.welcome

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Palette auth de référence (CLAUDE.md)
private val Background = Color(0xFF0F0D14)
private val Primary = Color(0xFF8B2FF0)
private val Accent = Color(0xFFE93D9B)
private val TextPrimary = Color(0xFFF5F3F7)
private val TextMuted = Color(0xFF9A93A8)

/**
 * Écran d'accueil (maquette n°1) : point d'entrée avant l'authentification.
 * Laisse choisir entre créer un compte (US1) et se connecter (US2).
 */
@Composable
fun WelcomeScreen(
    onCreateAccount: () -> Unit,
    onLogin: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 28.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(48.dp))

        // Pastille logo
        Box(
            modifier = Modifier
                .size(96.dp)
                .background(
                    brush = Brush.linearGradient(listOf(Primary, Accent)),
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text("É", color = Color.White, fontSize = 48.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(28.dp))

        Text(
            text = "Élan",
            color = TextPrimary,
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "Sport & recettes, ensemble.",
            color = TextMuted,
            fontSize = 16.sp,
            modifier = Modifier.padding(top = 8.dp),
        )

        Spacer(Modifier.weight(1f))

        Button(
            onClick = onCreateAccount,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary, contentColor = Color.White),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = "Créer un compte",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 6.dp),
            )
        }

        OutlinedButton(
            onClick = onLogin,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
            border = BorderStroke(1.dp, Brush.linearGradient(listOf(Primary, Accent))),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp),
        ) {
            Text(
                text = "Se connecter",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 6.dp),
            )
        }
    }
}
