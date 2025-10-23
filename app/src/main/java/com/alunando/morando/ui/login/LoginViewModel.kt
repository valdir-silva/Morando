package com.alunando.morando.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alunando.morando.data.firebase.AuthManager
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para tela de login
 */
class LoginViewModel(
    private val authManager: AuthManager,
) : ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    private val _effect = Channel<LoginEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        // Verifica se já está autenticado
        checkCurrentUser()
    }

    fun handleIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.LoginAnonymously -> loginAnonymously()
            is LoginIntent.LoginWithPassword -> loginWithPassword(intent.email, intent.password)
        }
    }

    private fun checkCurrentUser() {
        viewModelScope.launch {
            val user = authManager.currentUser
            if (user != null) {
                Log.d("LoginViewModel", "Usuário já autenticado: ${user.uid}")
                _state.value = _state.value.copy(userId = user.uid)
                _effect.send(LoginEffect.NavigateToHome)
            }
        }
    }

    private fun loginAnonymously() {
        _state.value = _state.value.copy(isLoading = true)
        viewModelScope.launch {
            Log.d("LoginViewModel", "Tentando login anônimo...")
            val result = authManager.signInAnonymously()
            result
                .onSuccess { user ->
                    Log.d("LoginViewModel", "✅ Login anônimo bem-sucedido: ${user.uid}")
                    _state.value =
                        _state.value.copy(
                            isLoading = false,
                            userId = user.uid,
                        )
                    _effect.send(LoginEffect.NavigateToHome)
                }.onFailure { error ->
                    Log.e("LoginViewModel", "❌ Erro no login anônimo: ${error.message}", error)
                    _state.value = _state.value.copy(isLoading = false)
                    _effect.send(LoginEffect.ShowError(error.message ?: "Erro ao fazer login anônimo"))
                }
        }
    }

    private fun loginWithPassword(
        email: String,
        password: String,
    ) {
        // Valida senha
        if (password != "123") {
            viewModelScope.launch {
                _effect.send(LoginEffect.ShowError("Senha incorreta! Use: 123"))
            }
            return
        }

        _state.value = _state.value.copy(isLoading = true)
        viewModelScope.launch {
            Log.d("LoginViewModel", "Tentando login com email: $email")

            // Tenta fazer login
            var result = authManager.signInWithEmailPassword(email, password)

            // Se falhar (usuário não existe), cria a conta
            if (result.isFailure) {
                Log.d("LoginViewModel", "Usuário não existe, criando conta...")
                result = authManager.registerWithEmailPassword(email, password)
            }

            result
                .onSuccess { user ->
                    Log.d("LoginViewModel", "✅ Login bem-sucedido: ${user.uid}")
                    _state.value =
                        _state.value.copy(
                            isLoading = false,
                            userId = user.uid,
                        )
                    _effect.send(LoginEffect.NavigateToHome)
                }.onFailure { error ->
                    Log.e("LoginViewModel", "❌ Erro no login: ${error.message}", error)
                    _state.value = _state.value.copy(isLoading = false)
                    _effect.send(LoginEffect.ShowError(error.message ?: "Erro ao fazer login"))
                }
        }
    }
}

/**
 * Estado da tela de login
 */
data class LoginState(
    val isLoading: Boolean = false,
    val userId: String = "",
)

/**
 * Intents do usuário
 */
sealed class LoginIntent {
    data object LoginAnonymously : LoginIntent()

    data class LoginWithPassword(
        val email: String,
        val password: String,
    ) : LoginIntent()
}

/**
 * Efeitos colaterais
 */
sealed class LoginEffect {
    data object NavigateToHome : LoginEffect()

    data class ShowError(
        val message: String,
    ) : LoginEffect()
}
