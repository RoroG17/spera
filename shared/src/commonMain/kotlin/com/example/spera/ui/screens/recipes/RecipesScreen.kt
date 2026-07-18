package com.example.spera.ui.screens.recipes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spera.models.Recipe
import com.example.spera.viewmodels.RecipesVM
import com.example.spera.viewmodels.states.RecipeFilter
import com.example.spera.viewmodels.states.RecipesUiState

// Palette auth de référence (CLAUDE.md)
private val Background = Color(0xFF0F0D14)
private val Surface = Color(0xFF1C1922)
private val Primary = Color(0xFF8B2FF0)
private val TextPrimary = Color(0xFFF5F3F7)
private val TextMuted = Color(0xFF9A93A8)

/**
 * Onglet Recettes (US9, maquette 8) : recherche (visuelle — active en US10),
 * filtres Toutes / Mes recettes / Favoris, grille des recettes et bouton « + »
 * de création. Le header et le footer sont fournis par `MainScaffold`.
 *
 * [refreshSignal] : recharge la liste quand la valeur change (retour de
 * création). [onCreateRecipe] : ouvre l'écran « Nouvelle recette ».
 */
@Composable
fun RecipesScreen(
    refreshSignal: Int = 0,
    onCreateRecipe: () -> Unit = {},
    onOpenRecipe: (recipe: Recipe, isFavorite: Boolean) -> Unit = { _, _ -> },
    viewModel: RecipesVM = viewModel { RecipesVM() },
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(refreshSignal) {
        if (refreshSignal > 0) viewModel.load()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
        ) {
            // Barre de recherche — visuelle uniquement (US10 : recherche de recettes).
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp)
                    .background(Surface, RoundedCornerShape(24.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                Text("🔍", fontSize = 15.sp)
                Text(
                    "Rechercher une recette…",
                    color = TextMuted,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(start = 10.dp),
                )
            }

            // Filtres fonctionnels (US9).
            val currentFilter = (state as? RecipesUiState.Success)?.filter ?: RecipeFilter.All
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(top = 14.dp),
            ) {
                RecipeFilter.entries.forEach { filter ->
                    FilterChip(
                        label = filter.label,
                        selected = filter == currentFilter,
                        onClick = { viewModel.onFilterSelect(filter) },
                    )
                }
            }

            // Filtres de recherche avancée — visuels uniquement (US10).
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(top = 10.dp).alpha(0.55f),
            ) {
                listOf("Calories", "Protéines", "Type").forEach { label ->
                    FilterChip(label = label, selected = false, onClick = {})
                }
            }

            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                when (val s = state) {
                    is RecipesUiState.Loading -> CenteredBox {
                        CircularProgressIndicator(color = Primary)
                    }

                    is RecipesUiState.Error -> CenteredBox {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(s.message, color = TextMuted, fontSize = 15.sp)
                            TextButton(onClick = viewModel::load) {
                                Text("Réessayer", color = Primary, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    is RecipesUiState.Success -> {
                        if (s.recipes.isEmpty()) {
                            CenteredBox {
                                Text(
                                    when (s.filter) {
                                        RecipeFilter.Mine -> "Tu n'as pas encore de recette."
                                        RecipeFilter.Favorites -> "Aucune recette en favori pour l'instant."
                                        RecipeFilter.All -> "Aucune recette pour l'instant."
                                    },
                                    color = TextMuted,
                                    fontSize = 15.sp,
                                )
                            }
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(top = 14.dp, bottom = 16.dp),
                                modifier = Modifier.fillMaxSize(),
                            ) {
                                items(items = s.recipes, key = { it.id }) { recipe ->
                                    val isFavorite = recipe.id in s.favoriteIds
                                    RecipeCard(
                                        recipe = recipe,
                                        isFavorite = isFavorite,
                                        onClick = { onOpenRecipe(recipe, isFavorite) },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Création de recette (même patron que le « + » du fil, avant refactoring).
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .size(56.dp)
                .background(Primary, CircleShape)
                .clickable(onClick = onCreateRecipe),
            contentAlignment = Alignment.Center,
        ) {
            Text("+", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun FilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(
                if (selected) Primary else Surface,
                RoundedCornerShape(50),
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 9.dp),
    ) {
        Text(
            label,
            color = if (selected) Color.White else TextMuted,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
        )
    }
}

@Composable
private fun RecipeCard(recipe: Recipe, isFavorite: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .background(Surface, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(10.dp),
    ) {
        // Photo (placeholder) + marqueur favori (toggle en US8).
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .background(Background, RoundedCornerShape(12.dp)),
        ) {
            Text("🍽️", fontSize = 30.sp, modifier = Modifier.align(Alignment.Center))
            if (isFavorite) {
                Text(
                    "🔖",
                    fontSize = 18.sp,
                    modifier = Modifier.align(Alignment.TopEnd).padding(8.dp),
                )
            }
        }

        Text(
            recipe.name,
            color = TextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 10.dp),
        )
        // Option A validée : temps du modèle (pas de macros dans data.txt).
        Text(
            formatTimes(recipe),
            color = TextMuted,
            fontSize = 13.sp,
            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp),
        )
    }
}

@Composable
private fun CenteredBox(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().padding(28.dp),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

/** « 15 min · 20 min cuisson », ou « 15 min · sans cuisson ». */
internal fun formatTimes(recipe: Recipe): String =
    if (recipe.timeCooking > 0) {
        "${recipe.timePreparation} min · ${recipe.timeCooking} min cuisson"
    } else {
        "${recipe.timePreparation} min · sans cuisson"
    }
