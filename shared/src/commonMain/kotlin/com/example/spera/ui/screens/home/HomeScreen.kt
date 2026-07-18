package com.example.spera.ui.screens.home

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spera.data.feed.FeedItem
import com.example.spera.models.User
import com.example.spera.viewmodels.HomeVM
import com.example.spera.viewmodels.states.HomeUiState

// Palette auth de référence (CLAUDE.md)
private val Background = Color(0xFF0F0D14)
private val Surface = Color(0xFF1C1922)
private val Primary = Color(0xFF8B2FF0)
private val Accent = Color(0xFFE93D9B)
private val TextPrimary = Color(0xFFF5F3F7)
private val TextMuted = Color(0xFF9A93A8)

/**
 * Contenu de l'onglet « Fil d'actualité » (US4) : liste des posts (séances +
 * recettes) des abonnements, avec infinite scroll. Le header et le footer sont
 * fournis par `MainScaffold`.
 */
@Composable
fun HomeScreen(
    viewModel: HomeVM = viewModel { HomeVM() },
) {
    val state by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    // Infinite scroll : déclenche la page suivante quand on approche du bas.
    val shouldLoadMore by remember {
        derivedStateOf {
            val info = listState.layoutInfo
            val lastVisible = info.visibleItemsInfo.lastOrNull()?.index ?: 0
            info.totalItemsCount > 0 && lastVisible >= info.totalItemsCount - 3
        }
    }
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) viewModel.loadMore()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
    ) {
        when (val s = state) {
            is HomeUiState.Loading -> CenteredBox {
                CircularProgressIndicator(color = Primary)
            }

            is HomeUiState.Error -> CenteredBox {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(s.message, color = TextMuted, fontSize = 15.sp)
                    TextButton(onClick = viewModel::load) {
                        Text("Réessayer", color = Primary, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            is HomeUiState.Success -> {
                if (s.posts.isEmpty()) {
                    CenteredBox {
                        Text(
                            "Aucun post pour l'instant.\nAbonne-toi à d'autres membres pour voir leur activité.",
                            color = TextMuted,
                            fontSize = 15.sp,
                        )
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        items(items = s.posts, key = { it.id }) { post ->
                            PostCard(post)
                        }
                        if (s.isLoadingMore) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    CircularProgressIndicator(
                                        color = Primary,
                                        strokeWidth = 2.dp,
                                        modifier = Modifier.size(28.dp),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PostCard(post: FeedItem) {
    val isRecipe = post is FeedItem.RecipePost
    val typeLabel = if (isRecipe) "Recette" else "Séance"
    val typeColor = if (isRecipe) Accent else Primary
    val title: String
    val description: String
    when (post) {
        is FeedItem.RecipePost -> {
            title = post.recipe.name
            description = post.recipe.description
        }
        is FeedItem.TrainingPost -> {
            title = post.training.name
            description = post.training.description
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Surface, RoundedCornerShape(16.dp))
            .padding(16.dp),
    ) {
        // Auteur + date
        Row(verticalAlignment = Alignment.CenterVertically) {
            Avatar(post.author)
            Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
                Text(
                    "${post.author.firstName} ${post.author.name}",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    "@${post.author.pseudo} · ${formatDate(post.date)}",
                    color = TextMuted,
                    fontSize = 13.sp,
                )
            }
            TypeBadge(typeLabel, typeColor)
        }

        // Contenu
        Text(
            title,
            color = TextPrimary,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 14.dp),
        )
        Text(
            description,
            color = TextMuted,
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 6.dp),
        )

        // Zone photo (placeholder — pas de chargement réseau pour rester autonome).
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .padding(top = 14.dp)
                .background(Background, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Text(if (isRecipe) "🍽️" else "🏃", fontSize = 34.sp)
        }
    }
}

@Composable
private fun Avatar(user: User) {
    val initial = user.firstName.firstOrNull()?.uppercase() ?: "?"
    Box(
        modifier = Modifier
            .size(42.dp)
            .background(Primary, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(initial, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun TypeBadge(label: String, color: Color) {
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.18f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp),
    ) {
        Text(label, color = color, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
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

/** `yyyy-MM-dd` → `dd/MM/yyyy` (affichage FR), sans dépendance date-time. */
private fun formatDate(iso: String): String {
    val parts = iso.split("-")
    return if (parts.size == 3) "${parts[2]}/${parts[1]}/${parts[0]}" else iso
}
