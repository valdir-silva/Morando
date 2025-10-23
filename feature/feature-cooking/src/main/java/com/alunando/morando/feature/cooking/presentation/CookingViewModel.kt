package com.alunando.morando.feature.cooking.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alunando.morando.core.onError
import com.alunando.morando.core.onSuccess
import com.alunando.morando.domain.model.CookingSession
import com.alunando.morando.domain.model.Recipe
import com.alunando.morando.domain.model.RecipeCategory
import com.alunando.morando.domain.model.StoveType
import com.alunando.morando.domain.usecase.AddRecipeUseCase
import com.alunando.morando.domain.usecase.CheckIngredientsAvailabilityUseCase
import com.alunando.morando.domain.usecase.DeleteRecipeUseCase
import com.alunando.morando.domain.usecase.GetRecipeByIdUseCase
import com.alunando.morando.domain.usecase.GetRecipesUseCase
import com.alunando.morando.domain.usecase.GetUserStovePreferenceUseCase
import com.alunando.morando.domain.usecase.SaveUserStovePreferenceUseCase
import com.alunando.morando.domain.usecase.UpdateRecipeUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para gerenciar receitas e sessões de cozinha
 */
@Suppress("TooManyFunctions", "LongParameterList")
class CookingViewModel(
    private val getRecipesUseCase: GetRecipesUseCase,
    private val getRecipeByIdUseCase: GetRecipeByIdUseCase,
    private val addRecipeUseCase: AddRecipeUseCase,
    private val updateRecipeUseCase: UpdateRecipeUseCase,
    private val deleteRecipeUseCase: DeleteRecipeUseCase,
    private val checkIngredientsAvailabilityUseCase: CheckIngredientsAvailabilityUseCase,
    private val getUserStovePreferenceUseCase: GetUserStovePreferenceUseCase,
    private val saveUserStovePreferenceUseCase: SaveUserStovePreferenceUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(CookingState())
    val state: StateFlow<CookingState> = _state.asStateFlow()

    private val _effect = Channel<CookingEffect>()
    val effect = _effect.receiveAsFlow()

    private var timerJob: Job? = null

    init {
        loadStovePreference()
    }

    /**
     * Processa intents do usuário
     */
    @Suppress("CyclomaticComplexMethod")
    fun handleIntent(intent: CookingIntent) {
        when (intent) {
            is CookingIntent.LoadRecipes -> loadRecipes()
            is CookingIntent.FilterByCategory -> filterByCategory(intent.category)
            is CookingIntent.SelectRecipe -> selectRecipe(intent.id)
            is CookingIntent.ClearSelection -> clearSelection()
            is CookingIntent.StartCooking -> startCookingSession(intent.recipeId)
            is CookingIntent.NextStep -> nextStep()
            is CookingIntent.PreviousStep -> previousStep()
            is CookingIntent.ToggleMiseEnPlaceStep -> toggleMiseEnPlaceStep(intent.stepIndex)
            is CookingIntent.StartMiseEnPlacePhase -> startMiseEnPlacePhase()
            is CookingIntent.StartCookingPhase -> startCookingPhase()
            is CookingIntent.StartTimer -> startTimer(intent.seconds)
            is CookingIntent.PauseTimer -> pauseTimer()
            is CookingIntent.ResumeTimer -> resumeTimer()
            is CookingIntent.StopTimer -> stopTimer()
            is CookingIntent.TickTimer -> tickTimer()
            is CookingIntent.StopCooking -> stopCooking()
            is CookingIntent.CreateRecipe -> createRecipe(intent.recipe)
            is CookingIntent.UpdateRecipe -> updateRecipe(intent.recipe)
            is CookingIntent.DeleteRecipe -> deleteRecipe(intent.id)
            is CookingIntent.SelectStoveType -> selectStoveType(intent.type)
            is CookingIntent.LoadStovePreference -> loadStovePreference()
            is CookingIntent.CheckIngredients -> checkIngredients(intent.recipeId)
        }
    }

    // ==================== RECEITAS ====================

    private fun loadRecipes() {
        _state.value = _state.value.copy(isLoading = true)
        getRecipesUseCase()
            .onEach { recipes ->
                _state.value =
                    _state.value.copy(
                        recipes = recipes,
                        filteredRecipes = filterRecipesByCategory(recipes, _state.value.selectedCategory),
                        isLoading = false,
                    )
            }.launchIn(viewModelScope)
    }

    private fun filterByCategory(category: RecipeCategory?) {
        _state.value =
            _state.value.copy(
                selectedCategory = category,
                filteredRecipes = filterRecipesByCategory(_state.value.recipes, category),
            )
    }

    private fun filterRecipesByCategory(
        recipes: List<Recipe>,
        category: RecipeCategory?,
    ): List<Recipe> = if (category == null) recipes else recipes.filter { it.categoria == category }

    private fun selectRecipe(id: String) {
        viewModelScope.launch {
            val result = getRecipeByIdUseCase(id)
            result
                .onSuccess { recipe ->
                    _state.value = _state.value.copy(selectedRecipe = recipe)
                    checkIngredients(id)
                }.onError {
                    sendEffect(CookingEffect.ShowError("Erro ao carregar receita"))
                }
        }
    }

    private fun clearSelection() {
        _state.value = _state.value.copy(selectedRecipe = null, ingredientsAvailability = emptyMap())
    }

    private fun createRecipe(recipe: Recipe) {
        viewModelScope.launch {
            val result = addRecipeUseCase(recipe)
            result
                .onSuccess {
                    sendEffect(CookingEffect.ShowToast("Receita adicionada com sucesso"))
                    sendEffect(CookingEffect.NavigateBack)
                }.onError {
                    sendEffect(CookingEffect.ShowError("Erro ao adicionar receita"))
                }
        }
    }

    private fun updateRecipe(recipe: Recipe) {
        viewModelScope.launch {
            val result = updateRecipeUseCase(recipe)
            result
                .onSuccess {
                    sendEffect(CookingEffect.ShowToast("Receita atualizada com sucesso"))
                    sendEffect(CookingEffect.NavigateBack)
                }.onError {
                    sendEffect(CookingEffect.ShowError("Erro ao atualizar receita"))
                }
        }
    }

    private fun deleteRecipe(id: String) {
        viewModelScope.launch {
            val result = deleteRecipeUseCase(id)
            result
                .onSuccess {
                    sendEffect(CookingEffect.ShowToast("Receita removida"))
                    sendEffect(CookingEffect.NavigateBack)
                }.onError {
                    sendEffect(CookingEffect.ShowError("Erro ao remover receita"))
                }
        }
    }

    // ==================== SESSÃO DE COZINHA ====================

    private fun startCookingSession(recipeId: String) {
        viewModelScope.launch {
            val result = getRecipeByIdUseCase(recipeId)
            result.onSuccess { recipe ->
                val session =
                    CookingSession(
                        recipe = recipe,
                        stoveType = _state.value.currentStoveType,
                        currentPhase = CookingSession.CookingPhase.MISE_EN_PLACE,
                        currentStepIndex = 0,
                        miseEnPlaceCompleted = List(recipe.etapasMiseEnPlace.size) { false },
                        timerRunning = false,
                        timerSecondsRemaining = 0,
                    )
                _state.value = _state.value.copy(cookingSession = session)
            }
        }
    }

    private fun nextStep() {
        val session = _state.value.cookingSession ?: return
        if (!session.canGoNext()) {
            // Se não pode avançar e está na fase de cooking, completar
            if (session.currentPhase == CookingSession.CookingPhase.COOKING) {
                completeCooking()
            }
            return
        }

        stopTimer()
        val newIndex = session.currentStepIndex + 1
        _state.value = _state.value.copy(cookingSession = session.copy(currentStepIndex = newIndex))
    }

    private fun previousStep() {
        val session = _state.value.cookingSession ?: return
        if (!session.canGoPrevious()) return

        stopTimer()
        val newIndex = session.currentStepIndex - 1
        _state.value = _state.value.copy(cookingSession = session.copy(currentStepIndex = newIndex))
    }

    private fun toggleMiseEnPlaceStep(stepIndex: Int) {
        val session = _state.value.cookingSession ?: return
        val completed = session.miseEnPlaceCompleted.toMutableList()
        if (stepIndex in completed.indices) {
            completed[stepIndex] = !completed[stepIndex]
            _state.value = _state.value.copy(cookingSession = session.copy(miseEnPlaceCompleted = completed))
        }
    }

    private fun startMiseEnPlacePhase() {
        val session = _state.value.cookingSession ?: return
        _state.value =
            _state.value.copy(
                cookingSession =
                    session.copy(
                        currentPhase = CookingSession.CookingPhase.MISE_EN_PLACE,
                        currentStepIndex = 0,
                    ),
            )
    }

    private fun startCookingPhase() {
        val session = _state.value.cookingSession ?: return
        // Verificar se todas as etapas do mise en place estão completas
        if (!session.isMiseEnPlaceComplete()) {
            sendEffect(CookingEffect.ShowToast("Complete todas as etapas do mise en place primeiro"))
            return
        }
        _state.value =
            _state.value.copy(
                cookingSession =
                    session.copy(
                        currentPhase = CookingSession.CookingPhase.COOKING,
                        currentStepIndex = 0,
                    ),
            )
    }

    private fun stopCooking() {
        stopTimer()
        _state.value = _state.value.copy(cookingSession = null)
    }

    private fun completeCooking() {
        val session = _state.value.cookingSession ?: return
        stopTimer()
        _state.value =
            _state.value.copy(
                cookingSession = session.copy(currentPhase = CookingSession.CookingPhase.COMPLETED),
            )
        sendEffect(CookingEffect.CookingCompleted)
        sendEffect(CookingEffect.ShowToast("Receita concluída! Bom apetite! 🍽️"))
    }

    // ==================== CRONÔMETRO ====================

    private fun startTimer(seconds: Int) {
        val session = _state.value.cookingSession ?: return
        stopTimer() // Para qualquer timer anterior

        _state.value =
            _state.value.copy(
                cookingSession =
                    session.copy(
                        timerRunning = true,
                        timerSecondsRemaining = seconds,
                    ),
            )

        timerJob =
            viewModelScope.launch {
                while (_state.value.cookingSession?.timerSecondsRemaining ?: 0 > 0) {
                    @Suppress("MagicNumber")
                    delay(1000)
                    handleIntent(CookingIntent.TickTimer)
                }
                // Timer finished
                sendEffect(CookingEffect.TimerFinished)
                sendEffect(CookingEffect.ShowToast("Tempo finalizado!"))
            }
    }

    private fun pauseTimer() {
        val session = _state.value.cookingSession ?: return
        timerJob?.cancel()
        _state.value = _state.value.copy(cookingSession = session.copy(timerRunning = false))
    }

    private fun resumeTimer() {
        val session = _state.value.cookingSession ?: return
        val remaining = session.timerSecondsRemaining
        if (remaining > 0) {
            startTimer(remaining)
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
        val session = _state.value.cookingSession ?: return
        _state.value =
            _state.value.copy(
                cookingSession =
                    session.copy(
                        timerRunning = false,
                        timerSecondsRemaining = 0,
                    ),
            )
    }

    private fun tickTimer() {
        val session = _state.value.cookingSession ?: return
        val remaining = session.timerSecondsRemaining - 1
        _state.value =
            _state.value.copy(
                cookingSession =
                    session.copy(
                        timerSecondsRemaining = maxOf(0, remaining),
                    ),
            )
    }

    // ==================== CONFIGURAÇÕES ====================

    private fun selectStoveType(type: StoveType) {
        viewModelScope.launch {
            saveUserStovePreferenceUseCase(type)
                .onSuccess {
                    _state.value = _state.value.copy(currentStoveType = type)
                    // Atualizar sessão se estiver ativa
                    _state.value.cookingSession?.let { session ->
                        _state.value = _state.value.copy(cookingSession = session.copy(stoveType = type))
                    }
                    sendEffect(CookingEffect.ShowToast("Tipo de fogão atualizado"))
                }
        }
    }

    private fun loadStovePreference() {
        viewModelScope.launch {
            val stoveType = getUserStovePreferenceUseCase()
            _state.value = _state.value.copy(currentStoveType = stoveType)
        }
    }

    // ==================== INGREDIENTES ====================

    private fun checkIngredients(recipeId: String) {
        viewModelScope.launch {
            val result = getRecipeByIdUseCase(recipeId)
            result.onSuccess { recipe ->
                val availability = checkIngredientsAvailabilityUseCase(recipe)
                _state.value = _state.value.copy(ingredientsAvailability = availability)
            }
        }
    }

    // ==================== HELPERS ====================

    private fun sendEffect(effect: CookingEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }
}
