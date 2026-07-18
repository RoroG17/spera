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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spera.models.User
import com.example.spera.ui.screens.home.HomeScreen
import com.example.spera.ui.screens.newpost.NewPostScreen
import com.example.spera.ui.screens.profile.ProfileScreen
import com.example.spera.ui.screens.recipes.RecipesScreen
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

    // US6 — écran « Nouvelle publication » en plein écran (sans header/footer),
    // comme le suggère la flèche retour de la maquette 6.
    var showNewPost by rememberSaveable { mutableStateOf(false) }
    // Incrémenté à chaque publication pour recharger le fil au retour.
    var feedRefresh by rememberSaveable { mutableStateOf(0) }

    if (showNewPost) {
        NewPostScreen(
            onBack = { showNewPost = false },
            onPublished = {
                showNewPost = false
                feedRefresh += 1
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
                MainTab.Feed -> HomeScreen(
                    refreshSignal = feedRefresh,
                    onCreatePost = { showNewPost = true },
                )
                MainTab.Recipes -> RecipesScreen()
                MainTab.Training -> TrainingScreen()
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
