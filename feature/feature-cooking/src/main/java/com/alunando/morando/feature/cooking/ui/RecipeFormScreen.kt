package com.alunando.morando.feature.cooking.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.alunando.morando.domain.model.Ingredient
import com.alunando.morando.domain.model.Recipe
import com.alunando.morando.domain.model.RecipeCategory
import com.alunando.morando.domain.model.RecipeDifficulty
import com.alunando.morando.domain.model.StoveType
import com.alunando.morando.feature.cooking.presentation.CookingIntent
import com.alunando.morando.feature.cooking.presentation.CookingViewModel

/**
 * Tela de formulário para criar/editar receitas
 * (Versão simplificada - MVP)
 */
@Suppress("LongMethod", "CyclomaticComplexMethod", "MagicNumber")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeFormScreen(
    viewModel: CookingViewModel,
    recipeId: String?,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var nome by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf(RecipeCategory.PRATO_PRINCIPAL) }
    var tempoPreparo by remember { mutableIntStateOf(30) }
    var porcoes by remember { mutableIntStateOf(4) }
    var dificuldade by remember { mutableStateOf(RecipeDifficulty.MEDIA) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (recipeId == null) "Nova Receita" else "Editar Receita") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar")
                    }
                },
            )
        },
        modifier = modifier,
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
        ) {
            Text(
                text = "Informações Básicas",
                style = MaterialTheme.typography.titleLarge,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Nome
            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome da Receita") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Descrição
            OutlinedTextField(
                value = descricao,
                onValueChange = { descricao = it },
                label = { Text("Descrição") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5,
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Categoria
            var expandedCategory by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedCategory,
                onExpandedChange = { expandedCategory = it },
            ) {
                OutlinedTextField(
                    value = categoria.displayName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoria") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                )
                ExposedDropdownMenu(
                    expanded = expandedCategory,
                    onDismissRequest = { expandedCategory = false },
                ) {
                    RecipeCategory.entries.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat.displayName) },
                            onClick = {
                                categoria = cat
                                expandedCategory = false
                            },
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Dificuldade
            var expandedDifficulty by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedDifficulty,
                onExpandedChange = { expandedDifficulty = it },
            ) {
                OutlinedTextField(
                    value = dificuldade.displayName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Dificuldade") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDifficulty) },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                )
                ExposedDropdownMenu(
                    expanded = expandedDifficulty,
                    onDismissRequest = { expandedDifficulty = false },
                ) {
                    RecipeDifficulty.entries.forEach { diff ->
                        DropdownMenuItem(
                            text = { Text(diff.displayName) },
                            onClick = {
                                dificuldade = diff
                                expandedDifficulty = false
                            },
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tempo de preparo
            OutlinedTextField(
                value = tempoPreparo.toString(),
                onValueChange = { tempoPreparo = it.toIntOrNull() ?: 0 },
                label = { Text("Tempo de Preparo (minutos)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Porções
            OutlinedTextField(
                value = porcoes.toString(),
                onValueChange = { porcoes = it.toIntOrNull() ?: 1 },
                label = { Text("Porções") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text =
                    "Nota: Para o MVP, os ingredientes e etapas devem ser adicionados posteriormente via " +
                        "API/Database",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botão salvar
            Button(
                onClick = {
                    val recipe =
                        Recipe(
                            id = recipeId ?: "",
                            nome = nome,
                            descricao = descricao,
                            categoria = categoria,
                            tempoPreparo = tempoPreparo,
                            porcoes = porcoes,
                            dificuldade = dificuldade,
                            fotoUrl = "",
                            ingredientes =
                                listOf(
                                    Ingredient("Exemplo", 1.0, "unidade"),
                                ),
                            etapasMiseEnPlace = emptyList(),
                            etapasPreparo = emptyList(),
                            tipoFogaoPadrao = StoveType.INDUCTION,
                        )

                    if (recipeId == null) {
                        viewModel.handleIntent(CookingIntent.CreateRecipe(recipe))
                    } else {
                        viewModel.handleIntent(CookingIntent.UpdateRecipe(recipe))
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = nome.isNotBlank() && descricao.isNotBlank(),
            ) {
                Text(if (recipeId == null) "Criar Receita" else "Salvar Alterações")
            }
        }
    }
}
