# Progress: Morando
*Version: 1.4*
*Created: 2025-10-21*
*Last Updated: 2025-10-24*

## ✅ Completed Tasks

### Foundation (100%)
- [x] Setup de estrutura de módulos (:core, :domain, :data, :sdui, :feature-*)
- [x] Configuração Gradle com libs.versions.toml e bundles
- [x] Build types (debug, mock, release) com ProGuard
- [x] Detekt e ktlint configurados
- [x] Gitignore e repositório Git inicializado

### Core Module (100%)
- [x] Result<T> sealed class para error handling
- [x] DateExtensions para manipulação de datas
- [x] FlowExtensions para conversão de Flow em Result

### Domain Layer (100%)
- [x] Models: Task, Product, ShoppingItem
- [x] Repository interfaces: TasksRepository, InventoryRepository, ShoppingRepository
- [x] Use Cases principais:
  - GetDailyTasksUseCase, GetWeeklyTasksUseCase
  - MarkTaskCompleteUseCase, AddTaskUseCase
  - GetProductsUseCase, AddProductUseCase
  - GetShoppingItemsUseCase, GenerateShoppingListUseCase

### Data Layer (70%)
- [x] AuthManager para Firebase Auth
- [x] FirebaseConfig com constantes
- [x] TasksRemoteDataSource com Firestore
- [x] TasksRepositoryImpl completo
- [x] InventoryRepositoryImpl (stub)
- [x] ShoppingRepositoryImpl (stub)

### SDUI Layer (60%)
- [x] SDUIComponent models (Text, Button, List, Column, Row)
- [x] SDUIEngine básico com renderização
- [ ] Parser JSON completo
- [ ] Integração com Firestore para configs
- [ ] Componentes avançados

### Features (50%)
- [x] **feature-tasks**: MVI completo (Intent, State, Effect, ViewModel, Screen)
- [x] **feature-barcode**: Estrutura básica (ViewModel, Screen stub)
- [x] **feature-inventory**: Placeholder screen criado
- [x] **feature-shopping**: Placeholder screen criado
- [x] **icone do app**: drawable com o icone do app Morando

### App Module (100%)
- [x] Koin DI setup completo (AppModule)
- [x] MorandoApplication com Firebase init
- [x] MainActivity com navegação
- [x] AndroidManifest com permissões
- [x] HomeScreen com cards de navegação
- [x] Navigation Compose implementado (AppNavigation)

### CI/CD (100%)
- [x] Fastlane configurado (Fastfile, Screengrabfile, Appfile)
- [x] GitHub Actions workflow completo:
  - lint-and-test job
  - ui-tests job com matrix (API 30, 33)
  - build job com artifacts
  - Screenshots automatizados

### Documentation (100%)
- [x] README.md completo
- [x] memory-bank/projectbrief.md
- [x] memory-bank/systemPatterns.md
- [x] memory-bank/techContext.md
- [x] memory-bank/progress.md

## 🚧 In Progress

### Data Layer
- [ ] Implementar InventoryRemoteDataSource com Firestore + Storage
- [ ] Implementar ShoppingRemoteDataSource com Firestore
- [ ] Testes unitários para repositories

### Features
- [x] feature-tasks: Refatoração completa para timeline/agenda ✅
- [ ] feature-tasks: Integração com Google Calendar API
- [x] feature-contas: Esqueleto completo criado ✅
- [ ] feature-inventory: Implementação completa com MVI expandido
- [ ] feature-barcode: Integração ML Kit + CameraX
- [ ] feature-shopping: Implementação completa com MVI
- [ ] feature-ai-assistant: Módulo de IA com suporte multi-provider

### Feature Cooking - MVP (100%) ✅ COMPLETO
- [x] Domain: Models (Recipe, Ingredient, Steps, enums, CookingSession, IngredientAvailability)
- [x] Domain: CookingRepository interface
- [x] Domain: Use Cases (GetRecipes, GetRecipeById, Add, Update, Delete, CheckIngredients, StartSession, StovePreferences)
- [x] Data: CookingRepositoryMock com 6+ receitas variadas
- [x] Data: CookingPreferencesDataSource (DataStore)
- [x] Feature: MVI (Intent, State, Effect, ViewModel com timer)
- [x] Feature: CookingListScreen (grid com filtros)
- [x] Feature: RecipeDetailScreen (detalhes + verificação de ingredientes)
- [x] Feature: CookingSessionScreen (parceiro de cozinha - mise en place + preparo + cronômetro)
- [x] Feature: RecipeFormScreen (CRUD completo)
- [x] Feature: StoveSettingsScreen (seleção de tipo de fogão)
- [x] Feature: Componentes compartilhados (Timer, AvailabilityChip, ProgressIndicator)
- [x] Feature: build.gradle.kts
- [x] App: Integração DI (AppModule)
- [x] App: Navegação (AppNavigation, AppRoute)
- [x] App: Card no HomeScreen
- [x] Build: Incluir módulo em settings.gradle.kts
- [x] Fix: Dependências (DataStore adicionado)
- [x] Fix: Ícones corrigidos (substituídos por emojis onde necessário)
- [x] Enhancement: Temperatura adicionada em TODOS os tipos de fogão (indução, gás, elétrico, lenha)

### Feature Tasks - Refatoração Timeline/Agenda (100%) ✅ COMPLETO
- [x] Domain: Adicionado enum RecurrenceType (NONE, DAILY, WEEKLY, MONTHLY)
- [x] Domain: TaskType simplificado para NORMAL e COMMITMENT
- [x] Domain: Campo recurrence adicionado ao modelo Task
- [x] Domain: Novos métodos no TasksRepository (getTasksForDate, getSubTasks)
- [x] Domain: Novos Use Cases (GetTasksForDateUseCase, GetSubTasksUseCase)
- [x] Data: Lógica de recorrência implementada em TasksRemoteDataSource
- [x] Data: Métodos getTasksForDate e getSubTasks implementados
- [x] Data: TasksRepositoryMock atualizado com dados de exemplo
- [x] Feature: State atualizado para trabalhar com data selecionada
- [x] Feature: Intent atualizado com navegação de datas
- [x] Feature: ViewModel refatorado para timeline
- [x] Feature: TasksScreen redesenhado como timeline/agenda
- [x] Feature: DateNavigationHeader com navegação entre dias
- [x] Feature: CommitmentCard sempre exibindo sub-tarefas
- [x] Feature: TaskFormDialog com seleção de tipo e recorrência
- [x] Feature: Gestão dinâmica de sub-tarefas em compromissos
- [x] App: DI atualizado com novos use cases

### Feature Contas - Esqueleto Completo (100%) ✅ COMPLETO
- [x] Domain: Models (Conta, ContaCategoria, ContaStatus, ContaRecorrencia, ContaTotais)
- [x] Domain: ContasRepository interface com métodos CRUD e filtros
- [x] Domain: Use Cases (8 use cases: Get, Add, Update, Delete, MarkPaga, GetByMonth, GetPendentes, GetTotais)
- [x] Data: ContasRepositoryMock com 10 contas de exemplo
- [x] Presentation: MVI completo (Intent, State, Effect, ViewModel)
- [x] UI: ContasScreen com lista de contas e card de totais
- [x] UI: ContaFormDialog com formulário completo
- [x] Build: build.gradle.kts e AndroidManifest.xml
- [x] Integration: Módulo adicionado ao settings.gradle.kts
- [x] Integration: Dependência adicionada ao app/build.gradle.kts
- [x] Integration: DI configurado no AppModule (repository + 8 use cases + ViewModel)
- [x] Integration: Rota de navegação adicionada (AppRoute.Contas)
- [x] Integration: Card adicionado no HomeScreen

## 📋 Next Tasks

### Milestone 2: Core Features (Expandido) (12-18 dias)

#### High Priority
1. **Integração Google Calendar API** (3 dias) 🆕
   - Configurar Google Calendar API no projeto
   - Autenticação OAuth2 para Google
   - Sincronização de compromissos com Google Calendar
   - Leitura de eventos do calendário
   - Escrita de compromissos no calendário
   - Configuração de permissões e scopes
   - UI para conectar/desconectar conta Google
   - Domain: Use Cases para sincronização
   - Data: GoogleCalendarDataSource

2. **Internacionalização (i18n)** (3 dias) 🆕
   - Configurar suporte multi-idioma no projeto Android
   - Criar resources strings.xml para português (pt-BR) (padrão)
   - Criar resources strings.xml para inglês (en)
   - Migrar todas as strings hardcoded do código para resources
   - Implementar seletor de idioma nas configurações do app
   - Adicionar preferência de idioma no DataStore
   - Traduzir todas as telas e componentes para inglês
   - Traduzir labels de enums e constantes exibidas ao usuário
   - Traduzir mensagens de erro e toasts
   - Testar app em ambos os idiomas (pt-BR e en)
   - Documentar processo de adição de novos idiomas

3. **Inventário Expandido** (5 dias)
   - Grupos/categorias de produtos
   - Cache de nomes por código de barras (Firestore collection)
   - Campos: data de vencimento, quantidade
   - Marcar produto como esgotado
   - Sistema de tracking de uso (histórico de consumo)
   - Domain: expandir modelo Product com novos campos
   - Use Cases: MarkProductDepletedUseCase, TrackProductUsageUseCase

3. **Implementar InventoryRemoteDataSource** (2 dias)
   - Firestore CRUD operations
   - Firebase Storage para upload de imagens
   - Queries otimizadas para produtos acabando
   - Cache de barcode -> nome do produto

4. **Feature Inventory Completa** (3 dias)
   - MVI: Intent, State, Effect, ViewModel
   - UI: Lista de produtos com grid e categorias
   - Formulário de cadastro com upload de foto
   - Integração com barcode scanner
   - Alertas visuais para vencimento próximo
   - UI para marcar produto como esgotado

5. **Feature Barcode Completa** (2 dias)
   - CameraX Preview implementation
   - ML Kit Barcode Scanning integration
   - Callback para inventory feature
   - Permissões de câmera
   - Lookup de produtos existentes por barcode

6. **Lista de Compras Completa** (3 dias)
   - ShoppingRemoteDataSource com Firestore
   - MVI implementation completo
   - UI com checkbox, categorias e ordenação
   - Geração automática baseada em estoque baixo
   - Sincronização com inventário
   - Botão para adicionar/remover itens manualmente

#### Medium Priority
7. **Testes Unitários** (2 dias)
   - ViewModels com Mockk e Turbine
   - Use Cases
   - Repositories

8. **Testes de UI** (2 dias)
   - Compose Testing para TasksScreen
   - Screenshot tests com Screengrab
   - Navigation tests

#### Low Priority
9. **Bottom Navigation** (1 dia)
   - Bottom navigation bar (opcional)
   - Navigation drawer (opcional)
   - Deep linking

10. **Melhorias de UX** (2 dias)
    - Loading states
    - Error handling UI
    - Empty states
    - Animations

11. **Firebase Google-services.json** (1 dia)
    - Setup do projeto Firebase
    - Configurar regras de segurança
    - Dados de exemplo

### Milestone 3: IA Assistant e Cozinhando (10-12 dias)

#### High Priority
1. **IA Provider Abstraction** (2 dias)
   - Interface AIProvider (domain layer)
   - GeminiProvider implementation (data layer)
   - Factory pattern para seleção de provider
   - OpenAIProvider stub (preparar para futuro)
   - ClaudeProvider stub (preparar para futuro)
   - Configuração persistente de provider (DataStore)
   - Domain: AIRepository interface

2. **IA Assistant Core** (3 dias)
   - Análise de dados (tarefas, inventário, compras)
   - Geração de dicas e sugestões personalizadas
   - Previsão de estoque por uso histórico com ML
   - Conversação com confirmação de ações
   - Use Cases: GenerateInsightsUseCase, PredictStockDepletionUseCase
   - Use Cases: ChatWithAIUseCase, ConfirmAIActionUseCase

3. **Feature AI Assistant** (3 dias)
   - MVI: Intent, State, Effect, ViewModel
   - Chat UI com Compose e LazyColumn
   - Tela de configuração de provedor de IA
   - Integração com TasksRepository, InventoryRepository, ShoppingRepository
   - UI para confirmar ações sugeridas pela IA
   - Bubble messages com markdown support

4. **Previsão Inteligente de Estoque** (2 dias)
   - Sistema de tracking contínuo de uso
   - Algoritmo de previsão (média móvel + tendências)
   - Notificações quando produto está acabando
   - Use Case: CalculateStockTrendUseCase
   - UI: badge/indicator em produtos

5. **Feature Cooking** (5 dias)
   - Estrutura MVI completa (Intent, State, Effect, ViewModel)
   - Domain models: Recipe, Ingredient, RecipeCategory
   - RecipeRepository interface e implementação com Firestore
   - CRUD de receitas (criar, editar, remover, listar)
   - Verificação de ingredientes vs inventário
   - Alertas visuais (vencimento próximo, estoque baixo)
   - UI: lista de receitas com filtros
   - UI: detalhes da receita com ingredientes
   - UI: formulário de criação/edição
   - Use Cases: GetRecipesUseCase, CheckIngredientsAvailabilityUseCase

#### Medium Priority
6. **Integração IA com Cooking** (2 dias)
   - IA sugere receitas baseado no inventário
   - IA sugere o que comprar para receita específica
   - IA sugere aproveitamento de produtos próximos ao vencimento
   - Use Case: SuggestRecipesFromInventoryUseCase

### Milestone 4: SDUI e Polish (5-7 dias)

1. **Engine SDUI Avançada**
   - Componentes complexos (forms, cards)
   - Data binding dinâmico
   - Actions avançadas
   - Cache de configurações

2. **Configurações SDUI no Firestore**
   - JSON para cada tela
   - Versionamento
   - A/B testing structure

3. **Polish e Refinamento**
   - Dark theme
   - Accessibility
   - Performance optimization
   - Code cleanup

### Milestone 5: KMP Preparation (Futuro)

1. **Refatoração para KMP**
   - Criar commonMain
   - Mover domain e core
   - Shared ViewModels
   - Platform-specific implementations

2. **iOS Version**
   - SwiftUI integration
   - Shared business logic
   - Platform UI

3. **Web Version**
   - Compose for Web
   - Responsive design
   - PWA features

## 📊 Metrics

### Overall Progress: ~65% ✅

| Component | Status | Progress |
|-----------|--------|----------|
| Foundation | ✅ Complete | 100% |
| Core | ✅ Complete | 100% |
| Domain | 🚧 In Progress | 92% |
| Data | 🚧 In Progress | 68% |
| SDUI | 🚧 In Progress | 60% |
| Features | 🚧 In Progress | 55% |
| Navigation | ✅ Complete | 100% |
| App Module | ✅ Complete | 100% |
| CI/CD | ✅ Complete | 100% |
| Documentation | ✅ Complete | 100% |
| Tests | 📋 TODO | 0% |
| IA Integration | 📋 TODO | 0% |
| i18n | 📋 TODO | 0% |

### Code Statistics
- **Modules**: 12 (app + 11 libraries: core, domain, data, sdui, feature-tasks, feature-barcode, feature-inventory, feature-shopping, feature-ai-assistant, feature-cooking, feature-contas)
- **Kotlin Files**: ~60+ (expandindo para ~80+ com novos módulos)
- **Lines of Code**: ~6000+ (projetado para ~10000+ com novas features)
- **Test Coverage**: 0% (TODO)

## 🎯 Immediate Next Steps

1. ~~Implementar navegação básica~~ ✅ **COMPLETO**
2. ~~Criar placeholders para features vazias~~ ✅ **COMPLETO**
3. ~~Refatorar feature-tasks para timeline/agenda~~ ✅ **COMPLETO**
4. **Integrar Google Calendar API** para sincronização de compromissos
5. Adicionar `google-services.json` ou configurar modo mock completo
6. **Expandir Domain Layer** para suportar novos campos (categorias, vencimento, quantidade)
7. **Implementar Inventário Expandido** com novos campos
8. **Implementar InventoryRemoteDataSource** com cache de barcode
9. **Completar feature-inventory** (MVI completo expandido)
10. **Implementar barcode scanner** com ML Kit e lookup de produtos
11. **Completar lista de compras** (MVI completo)
12. **Criar módulo feature-ai-assistant** com abstração de providers
13. **Integrar Google Gemini** como provider padrão
14. **Implementar previsão de estoque por IA**
15. Adicionar testes unitários básicos

## 🐛 Known Issues

- [ ] google-services.json não incluído (necessário para build Firebase, mas mock build funciona)
- [ ] Stub implementations em InventoryRepository e ShoppingRepository
- [ ] Barcode scanner apenas placeholder
- [ ] Inventory e Shopping features com tela placeholder (implementação MVI pendente)
- [ ] Nenhum teste implementado ainda
- [ ] Domain models precisam ser expandidos (Product, ShoppingItem)
- [ ] Não há sistema de tracking de uso de produtos
- [ ] Não há cache de código de barras para produtos
- [ ] Não há integração com IA
- [ ] Módulo feature-ai-assistant não existe ainda
- [ ] Não há integração com Google Calendar API

## 📝 Notes

- Projeto estruturado para escalar facilmente
- Arquitetura limpa permite adicionar features independentemente
- CI/CD pronto para uso desde o início
- Preparado para migração KMP no futuro
- SDUI permite flexibilidade sem redeploy
- **Novo**: Integração com IA usando padrão Strategy para suportar múltiplos providers
- **Novo**: Feature Cooking preparada para ser app separado no futuro
- **Novo**: Sistema de previsão de estoque baseado em ML/histórico de uso
- **Novo**: Cache de produtos por código de barras para agilizar cadastro
- **Novo**: Suporte a conversação com IA para manipular dados (com confirmação)
- **Novo**: Sistema de tarefas refatorado como timeline/agenda com recorrência
- **Novo**: Compromissos com sub-tarefas que devem ser concluídas até a data do compromisso
- **Novo**: Módulo feature-contas para gerenciamento de contas/despesas
- **Novo**: Sistema de contas com categorização, recorrência e totais por mês
- **Preparado**: Estrutura pronta para integração com Google Calendar API
- **Preparado**: Tarefa de internacionalização (i18n) documentada para suporte a inglês

---

*Este documento rastreia o progresso geral do projeto Morando.*

