# System Patterns: Morando
*Version: 1.0*
*Created: 2025-10-21*
*Last Updated: 2025-10-21*

## Architecture Overview
O projeto Morando segue **MVI (Model-View-Intent)** + **Clean Architecture** com modularização por features e suporte a SDUI (Server-Driven UI).

## Key Components

### Presentation Layer (MVI)
- **Intent**: Sealed interface com ações do usuário
- **State**: Data class representando estado da tela
- **Effect**: Sealed interface para eventos únicos (toasts, navegação)
- **ViewModel**: Processa Intents, atualiza State, emite Effects

```kotlin
// Exemplo: TasksViewModel
sealed interface TasksIntent {
    data object LoadTasks : TasksIntent
    data class ToggleComplete(val id: String) : TasksIntent
}

data class TasksState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = false
)

sealed interface TasksEffect {
    data class ShowToast(val message: String) : TasksEffect
}
```

### Domain Layer (Business Logic)
- **Models**: Entidades de negócio (Task, Product, ShoppingItem)
- **Repository Interfaces**: Contratos para acesso a dados
- **Use Cases**: Encapsulam lógica de negócio específica
- **Camada pura Kotlin**: Sem dependências Android

### Data Layer (Data Access)
- **Repository Implementations**: Implementam interfaces do domain
- **Data Sources**: TasksRemoteDataSource (Firestore)
- **Firebase Integration**: Auth, Firestore, Storage
- **Mappers**: Conversão entre modelos de dados e domínio

### SDUI Layer
- **Models**: SDUIComponent, SDUIScreen, SDUIAction
- **Engine**: SDUIEngine renderiza componentes dinamicamente
- **Components**: Text, Button, List, Column, Row
- **Configuração**: JSON no Firestore define layout das telas

## Design Patterns in Use

### MVI (Model-View-Intent)
- **Unidirectional Data Flow**: Intent → ViewModel → State → View
- **Single Source of Truth**: State é a única fonte de verdade
- **Immutability**: States são imutáveis
- **Predictability**: Fluxo previsível facilita debugging

### Repository Pattern
- Abstração do acesso a dados
- Permite troca fácil entre fontes (Firebase, Room, Mock)
- Facilita testes com mocks

### Use Case Pattern
- Cada operação CRUD tem seu Use Case
- Encapsula lógica de negócio específica
- Reutilizável e testável

### Observer Pattern
- Flow para comunicação reativa
- StateFlow para estado da UI
- Channel para eventos únicos (Effects)

### Factory Pattern (via Koin)
- Injeção de dependências
- Criação de ViewModels, Repositories, Use Cases

### SDUI Pattern
- UI configurada via servidor (Firestore)
- Permite mudanças sem deploy
- Facilita A/B testing

## Data Flow

```
User Action → Intent → ViewModel → Use Case → Repository → Firebase
                ↓
             State ← ViewModel ← Repository ← Firebase
                ↓
              View
```

### Fluxo Detalhado:
1. **User** interage com a UI (clique, input)
2. **View** dispara um **Intent**
3. **ViewModel** processa Intent, chama **Use Case**
4. **Use Case** executa lógica, chama **Repository**
5. **Repository** acessa **Firebase** (Firestore/Storage)
6. Dados retornam pela cadeia até **ViewModel**
7. **ViewModel** atualiza **State**
8. **View** recompõe com novo State

### Fluxo SDUI:
1. App busca configuração JSON do Firestore
2. SDUIEngine parseia JSON em componentes
3. Componentes são renderizados como Composables
4. Ações dos componentes disparam Intents do MVI

## Modularização

### Estrutura de Módulos
```
:app                → Injeção de dependências, MainActivity
:core               → Utilities puras (Result, Extensions)
:domain             → Models, Repository interfaces, Use Cases
:data               → Firebase, Repository implementations
:sdui               → Engine SDUI, Models de componentes
:feature-*          → Features isoladas (presentation + UI)
```

### Dependências entre Módulos
```
:app → todos os módulos
:feature-* → :core, :domain, :sdui
:data → :core, :domain
:sdui → :core
:domain → :core
```

## Key Technical Decisions

### MVI sobre MVVM
- **Rationale**: Fluxo unidirecional mais previsível
- **Benefit**: Facilita debugging, estados consistentes

### Clean Architecture
- **Rationale**: Separação clara de responsabilidades
- **Benefit**: Testabilidade, manutenibilidade, preparação para KMP

### SDUI
- **Rationale**: Flexibilidade para mudar UI sem deploy
- **Benefit**: A/B testing, personalização por usuário

### Modularização por Features
- **Rationale**: Escalabilidade e ownership claro
- **Benefit**: Build paralelo, isolamento de features

### Firebase sobre Room
- **Rationale**: Backend gerenciado, sincronização automática
- **Benefit**: Menos código de infraestrutura, cloud-ready

### Koin sobre Hilt
- **Rationale**: Simplicidade, menos boilerplate
- **Benefit**: Setup rápido, KMP-ready

## Error Handling Strategy
- **Result<T>**: Sealed class para sucesso/erro/loading
- **Try-Catch**: Em camada de dados
- **Effects**: Para mostrar erros na UI
- **Logging**: TODO: Implementar Crashlytics

## Testing Strategy
- **Unit Tests**: Use Cases, ViewModels (Mockk, Turbine)
- **Integration Tests**: Repositories (Firebase emulator)
- **UI Tests**: Telas Compose (Espresso + Compose Testing)
- **Screenshots**: Fastlane Screengrab
- **Mock Strategy**: Build type "mock" sem Firebase

## Performance Considerations
- **LazyColumn**: Para listas grandes
- **StateFlow**: Atualizações eficientes
- **Flow**: Streams reativos
- **Coil**: Cache automático de imagens
- **ProGuard**: Minificação em release

## Code Style
- **Detekt**: Análise estática
- **ktlint**: Formatação automática
- **4 espaços**: Indentação
- **120 caracteres**: Máximo por linha
- **Comentários em português**: Para clareza da equipe

---

*Este documento captura a arquitetura e padrões de design do projeto.*

