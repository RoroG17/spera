package com.example.spera.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spera.models.User

private val Background = Color(0xFF0F0D14)
private val Surface = Color(0xFF1C1922)
private val Primary = Color(0xFF8B2FF0)
private val Accent = Color(0xFFE93D9B)
private val TextPrimary = Color(0xFFF5F3F7)
private val TextMuted = Color(0xFF9A93A8)

/**
 * Onglet Profil (US17 — version minimale). Affiche les infos de l'utilisateur
 * connecté et porte l'action de déconnexion (US2).
 */
@Composable
fun ProfileScreen(
    user: User,
    onLogout: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Avatar + identité
        Box(
            modifier = Modifier.size(84.dp).background(Primary, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                user.firstName.firstOrNull()?.uppercase() ?: "?",
                color = Color.White,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Text(
            "${user.firstName} ${user.name}",
            color = TextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 14.dp),
        )
        Text("@${user.pseudo}", color = TextMuted, fontSize = 14.sp, modifier = Modifier.padding(top = 2.dp))

        // Mensurations
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            StatCard("Taille", "${user.height} cm", Modifier.weight(1f))
            StatCard("Poids", "${user.weight} kg", Modifier.weight(1f))
        }

        // Activités sportives
        if (user.activities.isNotEmpty()) {
            Text(
                "Activités",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth().padding(top = 24.dp, bottom = 8.dp),
            )
            user.activities.forEach { activity ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .background(Surface, RoundedCornerShape(14.dp))
                        .padding(16.dp),
                ) {
                    Text(activity.sport, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    Text(
                        "${activity.quantity}× / semaine" + (activity.objective?.let { " · $it" } ?: ""),
                        color = TextMuted,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }
        }

        // Déconnexion
        Button(
            onClick = onLogout,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = Color.White),
            modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
        ) {
            Text(
                "Se déconnecter",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 6.dp),
            )
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(Surface, RoundedCornerShape(14.dp))
            .padding(16.dp),
    ) {
        Text(label, color = TextMuted, fontSize = 13.sp)
        Text(value, color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
    }
}
