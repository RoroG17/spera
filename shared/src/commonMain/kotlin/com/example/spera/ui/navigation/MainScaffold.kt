package com.example.spera.ui.navigation

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spera.models.Recipe
import com.example.spera.models.Training
import com.example.spera.models.User
import com.example.spera.ui.screens.home.HomeScreen
import com.example.spera.ui.screens.profile.ProfileScreen
import com.example.spera.ui.screens.recipes.NewRecipeScreen
import com.example.spera.ui.screens.recipes.RecipeDetailScreen
import com.example.spera.ui.screens.recipes.RecipesScreen
import com.example.spera.ui.screens.training.TimerScreen
import com.example.spera.ui.screens.training.TrainingDetailScreen
import com.example.spera.ui.screens.training.TrainingScreen

// Palette auth de référence (CLAUDE.md)
private val Background = Color(0xFF0F0D14)
private val Surface = Color(0xFF1C1922)
private val Primary = Color(0xFF8B2FF0)
private val TextPrimary = Color(0xFFF5F3F7)
private val TextMuted = Color(0xFF9A93A8)
private val Border = Color(0xFF302B3A)

/** Sections principales de l'app authentifiée (footer de navigation). */
private enum class MainTab(val title: String, val icon: String, val label: String) {
    Feed("Fil d'actualité", "🏠", "Fil"),
    Recipes("Recettes", "🍽️", "Recettes"),
    Training("Entraînement", "🏃", "Entraîn."),
    Profile("Profil", "👤", "Profil"),
}

/**
 * Coque de l'app une fois connecté : un header et un footer communs à toutes les
 * pages, avec le contenu de la section sélectionnée au centre.
 */
@Composable
fun MainScaffold(
    user: User,
    onLogout: () -> Unit,
) {
    var tabIndex by rememberSaveable { mutableStateOf(0) }
    val tab = MainTab.entries[tabIndex]

    // Création de recette en plein écran, ouverte depuis l'onglet Recettes.
    var showNewRecipe by rememberSaveable { mutableStateOf(false) }
    // Incrémenté à chaque partage (recette/séance) pour recharger le fil.
    var feedRefresh by rememberSaveable { mutableStateOf(0) }
    // Incrémenté à chaque recette créée pour recharger la liste des recettes.
    var recipesRefresh by rememberSaveable { mutableStateOf(0) }

    // US14 — flux timer (création de séance) en plein écran.
    var showTimer by rememberSaveable { mutableStateOf(false) }
    // Incrémenté quand une séance timer est enregistrée, pour recharger le calendrier.
    var trainingRefresh by rememberSaveable { mutableStateOf(0) }

    // US9 — détail d'une recette en plein écran (sans header/footer), comme le
    // suggère la flèche retour de la maquette 9. (recette, est favorite)
    var openedRecipe by remember { mutableStateOf<Pair<Recipe, Boolean>?>(null) }

    // US12 — détail d'une séance en plein écran, même patron que le détail recette.
    var openedTraining by remember { mutableStateOf<Training?>(null) }

    val opened = openedRecipe
    if (opened != null) {
        RecipeDetailScreen(
            recipe = opened.first,
            isFavorite = opened.second,
            onBack = { openedRecipe = null },
            canShare = opened.first.users.id == user.id,
            onShared = { feedRefresh += 1 },
        )
        return
    }

    val trainingDetail = openedTraining
    if (trainingDetail != null) {
        TrainingDetailScreen(
            training = trainingDetail,
            onBack = { openedTraining = null },
            onShared = { feedRefresh += 1 },
        )
        return
    }

    if (showNewRecipe) {
        NewRecipeScreen(
            onBack = { showNewRecipe = false },
            onCreated = { shared ->
                showNewRecipe = false
                recipesRefresh += 1
                if (shared) feedRefresh += 1
            },
        )
        return
    }

    if (showTimer) {
        TimerScreen(
            onBack = { showTimer = false },
            onSaved = {
                showTimer = false
                trainingRefresh += 1
            },
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
    ) {
        AppHeader(title = tab.title, onSearch = { /* US7 — recherche user */ })

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when (tab) {
                MainTab.Feed -> HomeScreen(refreshSignal = feedRefresh)
                MainTab.Recipes -> RecipesScreen(
                    refreshSignal = recipesRefresh,
                    onCreateRecipe = { showNewRecipe = true },
                    onOpenRecipe = { recipe, isFavorite -> openedRecipe = recipe to isFavorite },
                )
                MainTab.Training -> TrainingScreen(
                    refreshSignal = trainingRefresh,
                    onCreateTraining = { showTimer = true },
                    onOpenTraining = { openedTraining = it },
                )
                MainTab.Profile -> ProfileScreen(user = user, onLogout = onLogout)
            }
        }

        AppFooter(current = tab, onSelect = { tabIndex = it.ordinal })
    }
}

@Composable
private fun AppHeader(title: String, onSearch: () -> Unit) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Background)
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(title, color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(
                "🔍",
                fontSize = 20.sp,
                modifier = Modifier.clickable(onClick = onSearch).padding(4.dp),
            )
        }
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Border))
    }
}

@Composable
private fun AppFooter(current: MainTab, onSelect: (MainTab) -> Unit) {
    Column {
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Border))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Surface)
                .navigationBarsPadding()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MainTab.entries.forEach { t ->
                val selected = t == current
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onSelect(t) }
                        .padding(vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        t.icon,
                        fontSize = 20.sp,
                        modifier = Modifier.alpha(if (selected) 1f else 0.55f),
                    )
                    Text(
                        t.label,
                        color = if (selected) Primary else TextMuted,
                        fontSize = 11.sp,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                        modifier = Modifier.padding(top = 3.dp),
                    )
                }
            }
        }
    }
}
