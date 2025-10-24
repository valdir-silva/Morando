@file:Suppress("TooManyFunctions")

package com.alunando.morando.feature.cooking.ui

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.alunando.morando.core.ui.ImagePicker
import com.alunando.morando.domain.model.CookingStep
import com.alunando.morando.domain.model.Ingredient
import com.alunando.morando.domain.model.MiseEnPlaceStep
import com.alunando.morando.domain.model.Product
import com.alunando.morando.domain.model.Recipe
import com.alunando.morando.domain.model.RecipeCategory
import com.alunando.morando.domain.model.RecipeDifficulty
import com.alunando.morando.domain.model.StoveType
import com.alunando.morando.domain.usecase.GetProductsUseCase
import com.alunando.morando.feature.cooking.presentation.CookingEffect
import com.alunando.morando.feature.cooking.presentation.CookingIntent
import com.alunando.morando.feature.cooking.presentation.CookingViewModel
import kotlinx.coroutines.flow.firstOrNull
import org.koin.compose.koinInject

/**
 * Tela de formulário completo para criar/editar receitas
 */
@Suppress("LongMethod", "CyclomaticComplexMethod", "MagicNumber", "TooManyFunctions")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeFormScreen(
    viewModel: CookingViewModel,
    recipeId: String?,
    onNavigateBack: () -> Unit,
    onNavigateToCookingList: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val getProductsUseCase: GetProductsUseCase = koinInject()

    // Lista de produtos disponíveis para vincular
    var availableProducts by remember { mutableStateOf<List<Product>>(emptyList()) }

    // Carrega produtos disponíveis
    LaunchedEffect(Unit) {
        availableProducts = getProductsUseCase().firstOrNull() ?: emptyList()
    }

    // Observa efeitos do ViewModel
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is CookingEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }

                is CookingEffect.ShowError -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_LONG).show()
                }

                is CookingEffect.NavigateToCookingList -> onNavigateToCookingList()
                is CookingEffect.NavigateBack -> onNavigateBack()
                else -> { // Outros efeitos são tratados em outros lugares
                }
            }
        }
    }

    // Estados básicos
    var nome by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf(RecipeCategory.PRATO_PRINCIPAL) }
    var tempoPreparo by remember { mutableIntStateOf(30) }
    var porcoes by remember { mutableIntStateOf(4) }
    var dificuldade by remember { mutableStateOf(RecipeDifficulty.MEDIA) }
    var tipoFogaoPadrao by remember { mutableStateOf(StoveType.INDUCTION) }

    // Imagem
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageUrl by remember { mutableStateOf<String?>(null) }
    var imageData by remember { mutableStateOf<ByteArray?>(null) }

    // Listas dinâmicas
    val ingredientes = remember { mutableStateListOf<Ingredient>() }
    val etapasMiseEnPlace = remember { mutableStateListOf<MiseEnPlaceStep>() }
    val etapasPreparo = remember { mutableStateListOf<CookingStep>() }

    // Carrega dados da receita ao editar
    LaunchedEffect(recipeId) {
        if (recipeId != null) {
            viewModel.handleIntent(CookingIntent.SelectRecipe(recipeId))
        }
    }

    // Observa a receita selecionada e preenche os campos
    LaunchedEffect(Unit) {
        viewModel.state.collect { state ->
            state.selectedRecipe?.let { recipe ->
                if (recipe.id == recipeId) {
                    nome = recipe.nome
                    descricao = recipe.descricao
                    categoria = recipe.categoria
                    tempoPreparo = recipe.tempoPreparo
                    porcoes = recipe.porcoes
                    dificuldade = recipe.dificuldade
                    tipoFogaoPadrao = recipe.tipoFogaoPadrao
                    imageUrl = recipe.fotoUrl.takeIf { it.isNotEmpty() }

                    // Preenche listas (apenas se ainda estiverem vazias)
                    if (ingredientes.isEmpty() && recipe.ingredientes.isNotEmpty()) {
                        ingredientes.addAll(recipe.ingredientes)
                    }
                    if (etapasMiseEnPlace.isEmpty() && recipe.etapasMiseEnPlace.isNotEmpty()) {
                        etapasMiseEnPlace.addAll(recipe.etapasMiseEnPlace)
                    }
                    if (etapasPreparo.isEmpty() && recipe.etapasPreparo.isNotEmpty()) {
                        etapasPreparo.addAll(recipe.etapasPreparo)
                    }
                }
            }
        }
    }

    // Limpa seleção ao sair
    DisposableEffect(Unit) {
        onDispose {
            viewModel.handleIntent(CookingIntent.ClearSelection)
        }
    }

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
            // === INFORMAÇÕES BÁSICAS ===
            SectionTitle("Informações Básicas")

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome da Receita") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            Spacer(modifier = Modifier.height(12.dp))

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
            CategoryDropdown(
                categoria = categoria,
                onCategoriaChange = { categoria = it },
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Tempo e porções em linha
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = tempoPreparo.toString(),
                    onValueChange = { tempoPreparo = it.toIntOrNull() ?: 30 },
                    label = { Text("⏱️ Tempo de Preparo (min)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    supportingText = {
                        val tempoEtapas = etapasPreparo.sumOf { it.tempoMinutos }
                        if (tempoEtapas > 0) {
                            Text(
                                "Total: ${tempoPreparo + tempoEtapas} min (preparo + cozimento)",
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    },
                )

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedTextField(
                    value = porcoes.toString(),
                    onValueChange = { porcoes = it.toIntOrNull() ?: 1 },
                    label = { Text("👥 Porções") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Dificuldade
            DifficultyDropdown(
                dificuldade = dificuldade,
                onDificuldadeChange = { dificuldade = it },
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Tipo de fogão padrão
            StoveTypeDropdown(
                tipoFogao = tipoFogaoPadrao,
                onTipoFogaoChange = { tipoFogaoPadrao = it },
            )

            Spacer(modifier = Modifier.height(24.dp))

            // === IMAGEM ===
            SectionTitle("Foto da Receita")

            Spacer(modifier = Modifier.height(8.dp))

            ImagePicker(
                imageUri = imageUri,
                imageUrl = imageUrl,
                onImageSelected = { bytes ->
                    imageData = bytes
                    imageUrl = null // Nova imagem selecionada, remove a URL antiga
                },
                onImageRemoved = {
                    imageUri = null
                    imageData = null
                    imageUrl = null
                },
            )

            Spacer(modifier = Modifier.height(24.dp))

            // === INGREDIENTES ===
            IngredientsSection(
                ingredientes = ingredientes,
                availableProducts = availableProducts,
                onAddIngredient = {
                    ingredientes.add(
                        Ingredient(
                            nome = "",
                            quantidade = 1.0,
                            unidade = "",
                            observacoes = "",
                            productId = null,
                        ),
                    )
                },
                onRemoveIngredient = { index -> ingredientes.removeAt(index) },
            )

            Spacer(modifier = Modifier.height(24.dp))

            // === MISE EN PLACE ===
            MiseEnPlaceSection(
                etapas = etapasMiseEnPlace,
                onAddEtapa = {
                    etapasMiseEnPlace.add(
                        MiseEnPlaceStep(
                            ordem = etapasMiseEnPlace.size + 1,
                            ingrediente = "",
                            quantidade = "",
                            instrucao = "",
                            tipoDePreparo = "",
                        ),
                    )
                },
                onRemoveEtapa = { index -> etapasMiseEnPlace.removeAt(index) },
            )

            Spacer(modifier = Modifier.height(24.dp))

            // === ETAPAS DE PREPARO ===
            CookingStepsSection(
                etapas = etapasPreparo,
                onAddEtapa = {
                    etapasPreparo.add(
                        CookingStep(
                            ordem = etapasPreparo.size + 1,
                            titulo = "",
                            tempoMinutos = 0,
                            instrucoesGerais = "",
                        ),
                    )
                },
                onRemoveEtapa = { index -> etapasPreparo.removeAt(index) },
            )

            Spacer(modifier = Modifier.height(24.dp))

            // === BOTÃO SALVAR ===
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
                            fotoUrl = imageUrl ?: "",
                            ingredientes = ingredientes.toList(),
                            etapasMiseEnPlace = etapasMiseEnPlace.toList(),
                            etapasPreparo = etapasPreparo.toList(),
                            tipoFogaoPadrao = tipoFogaoPadrao,
                        )

                    if (recipeId == null) {
                        viewModel.handleIntent(CookingIntent.CreateRecipe(recipe, imageData))
                    } else {
                        viewModel.handleIntent(CookingIntent.UpdateRecipe(recipe, imageData))
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled =
                    nome.isNotBlank() &&
                        descricao.isNotBlank() &&
                        ingredientes.isNotEmpty() &&
                        etapasPreparo.isNotEmpty(),
            ) {
                Text(if (recipeId == null) "Criar Receita" else "Salvar Alterações")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryDropdown(
    categoria: RecipeCategory,
    onCategoriaChange: (RecipeCategory) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        OutlinedTextField(
            value = categoria.displayName,
            onValueChange = {},
            readOnly = true,
            label = { Text("Categoria") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            RecipeCategory.entries.forEach { cat ->
                DropdownMenuItem(
                    text = { Text(cat.displayName) },
                    onClick = {
                        onCategoriaChange(cat)
                        expanded = false
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DifficultyDropdown(
    dificuldade: RecipeDifficulty,
    onDificuldadeChange: (RecipeDifficulty) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        OutlinedTextField(
            value = dificuldade.displayName,
            onValueChange = {},
            readOnly = true,
            label = { Text("Dificuldade") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            RecipeDifficulty.entries.forEach { dif ->
                DropdownMenuItem(
                    text = { Text(dif.displayName) },
                    onClick = {
                        onDificuldadeChange(dif)
                        expanded = false
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StoveTypeDropdown(
    tipoFogao: StoveType,
    onTipoFogaoChange: (StoveType) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        OutlinedTextField(
            value = tipoFogao.displayName,
            onValueChange = {},
            readOnly = true,
            label = { Text("Tipo de Fogão Padrão") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            StoveType.entries.forEach { type ->
                DropdownMenuItem(
                    text = { Text(type.displayName) },
                    onClick = {
                        onTipoFogaoChange(type)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Suppress("LongMethod")
@Composable
private fun IngredientsSection(
    ingredientes: List<Ingredient>,
    availableProducts: List<Product>,
    onAddIngredient: () -> Unit,
    onRemoveIngredient: (Int) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            ) {
                Text(
                    text = "Ingredientes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )
                IconButton(onClick = onAddIngredient) {
                    Icon(Icons.Default.Add, "Adicionar ingrediente")
                }
            }

            if (ingredientes.isEmpty()) {
                Text(
                    text = "Nenhum ingrediente adicionado",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            } else {
                ingredientes.forEachIndexed { index, ingrediente ->
                    Spacer(modifier = Modifier.height(8.dp))
                    IngredientItem(
                        ingrediente = ingrediente,
                        availableProducts = availableProducts,
                        onRemove = { onRemoveIngredient(index) },
                        onUpdate = { updated ->
                            if (ingredientes is androidx.compose.runtime.snapshots.SnapshotStateList) {
                                ingredientes[index] = updated
                            }
                        },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IngredientItem(
    ingrediente: Ingredient,
    availableProducts: List<Product>,
    onRemove: () -> Unit,
    onUpdate: (Ingredient) -> Unit,
) {
    var expandedProducts by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            ) {
                Text(
                    text = "Ingrediente",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.weight(1f),
                )
                IconButton(onClick = onRemove) {
                    Icon(
                        Icons.Default.Delete,
                        "Remover",
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Dropdown para vincular produto do estoque
            if (availableProducts.isNotEmpty()) {
                ExposedDropdownMenuBox(
                    expanded = expandedProducts,
                    onExpandedChange = { expandedProducts = it },
                ) {
                    OutlinedTextField(
                        value =
                            ingrediente.productId?.let { id ->
                                availableProducts.find { it.id == id }?.nome ?: "Produto vinculado"
                            } ?: "Selecione um produto (opcional)",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("🔗 Produto do Estoque") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedProducts) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                    )

                    ExposedDropdownMenu(
                        expanded = expandedProducts,
                        onDismissRequest = { expandedProducts = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text("Nenhum (digitar manualmente)") },
                            onClick = {
                                onUpdate(ingrediente.copy(productId = null))
                                expandedProducts = false
                            },
                        )
                        availableProducts.forEach { product ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(product.nome)
                                        Text(
                                            "${product.categoria}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                },
                                onClick = {
                                    onUpdate(
                                        ingrediente.copy(
                                            productId = product.id,
                                            nome = product.nome,
                                        ),
                                    )
                                    expandedProducts = false
                                },
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            OutlinedTextField(
                value = ingrediente.nome,
                onValueChange = { onUpdate(ingrediente.copy(nome = it)) },
                label = { Text("Nome do Ingrediente") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = ingrediente.productId == null,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = ingrediente.quantidade.toString(),
                    onValueChange = {
                        onUpdate(ingrediente.copy(quantidade = it.toDoubleOrNull() ?: 0.0))
                    },
                    label = { Text("Quantidade") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                )

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedTextField(
                    value = ingrediente.unidade,
                    onValueChange = { onUpdate(ingrediente.copy(unidade = it)) },
                    label = { Text("Unidade") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = ingrediente.observacoes,
                onValueChange = { onUpdate(ingrediente.copy(observacoes = it)) },
                label = { Text("Observações (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2,
            )
        }
    }
}

@Suppress("LongMethod")
@Composable
private fun MiseEnPlaceSection(
    etapas: List<MiseEnPlaceStep>,
    onAddEtapa: () -> Unit,
    onRemoveEtapa: (Int) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Mise en Place",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "Preparação prévia (opcional)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
                    )
                }
                IconButton(onClick = onAddEtapa) {
                    Icon(Icons.Default.Add, "Adicionar etapa")
                }
            }

            if (etapas.isEmpty()) {
                Text(
                    text = "Nenhuma etapa de mise en place",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f),
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            } else {
                etapas.forEachIndexed { index, etapa ->
                    Spacer(modifier = Modifier.height(8.dp))
                    MiseEnPlaceItem(
                        etapa = etapa,
                        onRemove = { onRemoveEtapa(index) },
                        onUpdate = { updated ->
                            if (etapas is androidx.compose.runtime.snapshots.SnapshotStateList) {
                                etapas[index] = updated
                            }
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun MiseEnPlaceItem(
    etapa: MiseEnPlaceStep,
    onRemove: () -> Unit,
    onUpdate: (MiseEnPlaceStep) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            ) {
                Text(
                    text = "Etapa ${etapa.ordem}",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.weight(1f),
                )
                IconButton(onClick = onRemove) {
                    Icon(
                        Icons.Default.Delete,
                        "Remover",
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = etapa.ingrediente,
                    onValueChange = { onUpdate(etapa.copy(ingrediente = it)) },
                    label = { Text("Ingrediente") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                )

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedTextField(
                    value = etapa.quantidade,
                    onValueChange = { onUpdate(etapa.copy(quantidade = it)) },
                    label = { Text("Quantidade") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = etapa.tipoDePreparo,
                onValueChange = { onUpdate(etapa.copy(tipoDePreparo = it)) },
                label = { Text("Tipo (ex: Cortar, Separar)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = etapa.instrucao,
                onValueChange = { onUpdate(etapa.copy(instrucao = it)) },
                label = { Text("Instrução") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4,
            )
        }
    }
}

@Suppress("LongMethod")
@Composable
private fun CookingStepsSection(
    etapas: List<CookingStep>,
    onAddEtapa: () -> Unit,
    onRemoveEtapa: (Int) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            ) {
                Text(
                    text = "Modo de Preparo",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )
                IconButton(onClick = onAddEtapa) {
                    Icon(Icons.Default.Add, "Adicionar etapa")
                }
            }

            if (etapas.isEmpty()) {
                Text(
                    text = "Nenhuma etapa de preparo adicionada",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.6f),
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            } else {
                etapas.forEachIndexed { index, etapa ->
                    Spacer(modifier = Modifier.height(8.dp))
                    CookingStepItem(
                        etapa = etapa,
                        onRemove = { onRemoveEtapa(index) },
                        onUpdate = { updated ->
                            if (etapas is androidx.compose.runtime.snapshots.SnapshotStateList) {
                                etapas[index] = updated
                            }
                        },
                    )
                }
            }
        }
    }
}

@Suppress("LongMethod")
@Composable
private fun CookingStepItem(
    etapa: CookingStep,
    onRemove: () -> Unit,
    onUpdate: (CookingStep) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            ) {
                Text(
                    text = "Etapa ${etapa.ordem}",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.weight(1f),
                )
                IconButton(onClick = onRemove) {
                    Icon(
                        Icons.Default.Delete,
                        "Remover",
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = etapa.titulo,
                    onValueChange = { onUpdate(etapa.copy(titulo = it)) },
                    label = { Text("Título") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                )

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedTextField(
                    value = etapa.tempoMinutos.toString(),
                    onValueChange = {
                        @Suppress("MagicNumber")
                        onUpdate(etapa.copy(tempoMinutos = it.toIntOrNull() ?: 0))
                    },
                    label = { Text("Tempo (min)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(0.7f),
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = etapa.instrucoesGerais,
                onValueChange = { onUpdate(etapa.copy(instrucoesGerais = it)) },
                label = { Text("Instruções Gerais") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Instruções específicas por tipo de fogão (opcional)",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = etapa.instrucaoInducao,
                onValueChange = { onUpdate(etapa.copy(instrucaoInducao = it)) },
                label = { Text("Indução") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 3,
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = etapa.instrucaoGas,
                onValueChange = { onUpdate(etapa.copy(instrucaoGas = it)) },
                label = { Text("Gás") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 3,
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = etapa.instrucaoEletrico,
                onValueChange = { onUpdate(etapa.copy(instrucaoEletrico = it)) },
                label = { Text("Elétrico") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 3,
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = etapa.instrucaoLenha,
                onValueChange = { onUpdate(etapa.copy(instrucaoLenha = it)) },
                label = { Text("Lenha") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 3,
            )
        }
    }
}
