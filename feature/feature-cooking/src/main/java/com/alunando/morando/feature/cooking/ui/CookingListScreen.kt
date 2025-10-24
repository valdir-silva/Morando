package com.alunando.morando.feature.cooking.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.alunando.morando.domain.model.Recipe
import com.alunando.morando.domain.model.RecipeCategory
import com.alunando.morando.feature.cooking.presentation.CookingIntent
import com.alunando.morando.feature.cooking.presentation.CookingViewModel

/**
 * Tela de lista de receitas
 */
@Suppress("LongMethod", "MagicNumber")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CookingListScreen(
    viewModel: CookingViewModel,
    onNavigateToRecipeDetail: (String) -> Unit,
    onNavigateToRecipeForm: () -> Unit,
    onNavigateToStoveSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.handleIntent(CookingIntent.LoadRecipes)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Receitas") },
                actions = {
                    IconButton(onClick = onNavigateToStoveSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Configurações")
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToRecipeForm) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Receita")
            }
        },
        modifier = modifier,
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
        ) {
            // Filtros por categoria
            CategoryFilters(
                selectedCategory = state.selectedCategory,
                onCategorySelected = { category ->
                    viewModel.handleIntent(CookingIntent.FilterByCategory(category))
                },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )

            // Grid de receitas
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            } else if (state.filteredRecipes.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("Nenhuma receita encontrada")
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(state.filteredRecipes) { recipe ->
                        RecipeCard(
                            recipe = recipe,
                            onClick = { onNavigateToRecipeDetail(recipe.id) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryFilters(
    selectedCategory: RecipeCategory?,
    onCategorySelected: (RecipeCategory?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FilterChip(
            selected = selectedCategory == null,
            onClick = { onCategorySelected(null) },
            label = { Text("Todas") },
        )

        @Suppress("MagicNumber")
        RecipeCategory.entries.take(4).forEach { category ->
            FilterChip(
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) },
                label = { Text(category.displayName, maxLines = 1, overflow = TextOverflow.Ellipsis) },
            )
        }
    }
}

@Suppress("LongMethod")
@Composable
private fun RecipeCard(
    recipe: Recipe,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column {
            // Imagem (placeholder se não houver)
            if (recipe.fotoUrl.isNotEmpty()) {
                AsyncImage(
                    model = recipe.fotoUrl,
                    contentDescription = recipe.nome,
                    contentScale = ContentScale.Crop,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
                )
            } else {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "🍳",
                        style = MaterialTheme.typography.displayLarge,
                    )
                }
            }

            // Informações
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
            ) {
                Text(
                    text = recipe.nome,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "⏱️ ${recipe.tempoPreparo}min",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = recipe.dificuldade.displayName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = recipe.categoria.displayName,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}
