package com.example.spera.ui.screens.recipes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spera.models.Recipe

// Palette auth de référence (CLAUDE.md)
private val Background = Color(0xFF0F0D14)
private val Surface = Color(0xFF1C1922)
private val Primary = Color(0xFF8B2FF0)
private val Accent = Color(0xFFE93D9B)
private val TextPrimary = Color(0xFFF5F3F7)
private val TextMuted = Color(0xFF9A93A8)

/**
 * Détail d'une recette (US9, maquette 9) : photo, temps, description,
 * ingrédients. Le bloc « Adapter avec l'IA » est affiché mais inactif (US11) ;
 * le marqueur favori est décoratif (toggle en US8).
 */
@Composable
fun RecipeDetailScreen(
    recipe: Recipe,
    isFavorite: Boolean,
    onBack: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding(),
    ) {
        // Photo (placeholder) avec retour + favori par-dessus.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .background(Surface),
        ) {
            Text(
                "photo de la recette",
                color = TextMuted,
                fontSize = 13.sp,
                modifier = Modifier.align(Alignment.Center),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(Background, CircleShape)
                        .clickable(onClick = onBack),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("‹", color = TextPrimary, fontSize = 24.sp)
                }
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(Background, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("🔖", fontSize = 17.sp, modifier = Modifier.alpha(if (isFavorite) 1f else 0.35f))
                }
            }
        }

        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                recipe.name,
                color = TextPrimary,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                "par ${recipe.users.firstName} ${recipe.users.name} · ${formatDate(recipe.date)}",
                color = TextMuted,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 6.dp),
            )

            // Tuiles temps (option A : données du modèle, pas de macros dans data.txt).
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(top = 18.dp),
            ) {
                StatTile(
                    value = "${recipe.timePreparation} min",
                    label = "préparation",
                    color = Accent,
                    modifier = Modifier.weight(1f),
                )
                StatTile(
                    value = if (recipe.timeCooking > 0) "${recipe.timeCooking} min" else "aucune",
                    label = "cuisson",
                    color = Primary,
                    modifier = Modifier.weight(1f),
                )
            }

            // Bloc « Adapter avec l'IA » — inactif, branché en US11.
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp)
                    .background(Primary.copy(alpha = 0.18f), RoundedCornerShape(14.dp))
                    .padding(16.dp),
            ) {
                Text(
                    "✨ Adapter avec l'IA",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    "Bientôt : ajuste les quantités à tes objectifs.",
                    color = TextMuted,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }

            Text(
                recipe.description,
                color = TextMuted,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 18.dp),
            )

            Text(
                "Ingrédients",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 22.dp),
            )
            recipe.ingredients.forEach { item ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .background(Surface, RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.ingredient.name, color = TextPrimary, fontSize = 14.sp)
                        if (item.ingredient.allergens.isNotEmpty()) {
                            Text(
                                "⚠️ ${item.ingredient.allergens.joinToString()}",
                                color = Accent,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 2.dp),
                            )
                        }
                    }
                    Text(
                        formatQuantity(item.quantity, item.ingredient.unit),
                        color = TextMuted,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

@Composable
private fun StatTile(value: String, label: String, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(Surface, RoundedCornerShape(14.dp))
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(value, color = color, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text(label, color = TextMuted, fontSize = 13.sp, modifier = Modifier.padding(top = 2.dp))
    }
}

/** « 1 pièce », « 80 g », « 150 ml »… */
private fun formatQuantity(quantity: Int, unit: String): String = "$quantity $unit"

/** `yyyy-MM-dd` → `dd/MM/yyyy` (affichage FR), sans dépendance date-time. */
private fun formatDate(iso: String): String {
    val parts = iso.split("-")
    return if (parts.size == 3) "${parts[2]}/${parts[1]}/${parts[0]}" else iso
}
