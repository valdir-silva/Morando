# Getting Started - Morando

Guia rápido para começar a desenvolver no projeto.

## ⚡ Quick Start

### 1. Clone e Setup

```bash
git clone <seu-repositorio>
cd Morando
```

### 2. Configurar Firebase

**Opção A: Usar build type mock (sem Firebase)**
```bash
./gradlew assembleMock
```

**Opção B: Configurar Firebase (recomendado)**

1. Acesse [Firebase Console](https://console.firebase.google.com/)
2. Crie novo projeto "Morando"
3. Adicione app Android:
   - Package name: `com.alunando.morando`
   - Debug SHA-1: (opcional para desenvolvimento)
4. Baixe `google-services.json`
5. Copie para `app/google-services.json`

### 3. Configurar Firestore

No Firebase Console > Firestore Database:

1. Criar database no modo **teste** (regras abertas por 30 dias)
2. Depois mudar para regras de produção (ver abaixo)

**Regras de Segurança**:
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId}/{document=**} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
    match /ui_configs/{screenId} {
      allow read: if request.auth != null;
    }
  }
}
```

### 4. Habilitar Authentication

Firebase Console > Authentication:

1. Ativar **Anonymous**
2. (Opcional) Ativar **Email/Password**
3. (Opcional) Ativar **Google**

### 5. Build e Run

```bash
# Debug build com Firebase
./gradlew assembleDebug

# Ou abrir no Android Studio e rodar
```

## 🔨 Comandos Úteis

### Build
```bash
./gradlew assembleDebug        # Build debug
./gradlew assembleMock          # Build mock (sem Firebase)
./gradlew bundleRelease         # Build release (AAB)
```

### Testes
```bash
./gradlew testDebugUnitTest            # Testes unitários
./gradlew connectedDebugAndroidTest    # Testes instrumentados
```

### Code Quality
```bash
./gradlew ktlintCheck          # Verificar formatação
./gradlew ktlintFormat         # Auto-formatar código
./gradlew detekt               # Análise estática
```

### Fastlane
```bash
fastlane lint                  # Lint + Detekt
fastlane unit_tests            # Testes unitários
fastlane ui_tests              # Testes de UI
fastlane ci                    # Pipeline completo
```

## 📁 Estrutura do Projeto

```
Morando/
├── app/                    # App Android principal
├── core/                   # Utilities puras (Result, Extensions)
├── domain/                 # Business logic (Models, Use Cases)
├── data/                   # Firebase implementation
├── sdui/                   # Server-Driven UI engine
└── feature/
    ├── feature-tasks/      # Tarefas (✅ Completo)
    ├── feature-inventory/  # Estoque (🚧 Estrutura)
    ├── feature-shopping/   # Lista compras (🚧 Estrutura)
    └── feature-barcode/    # Scanner (🚧 Estrutura)
```

## 🎯 Próximos Passos para Desenvolver

### 1. Implementar InventoryRemoteDataSource

Criar `data/src/main/java/.../datasource/InventoryRemoteDataSource.kt`:

```kotlin
class InventoryRemoteDataSource(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val authManager: AuthManager
) {
    fun getProducts(): Flow<List<Product>> { /* ... */ }
    suspend fun addProduct(product: Product) { /* ... */ }
    suspend fun uploadImage(productId: String, bytes: ByteArray): String { /* ... */ }
}
```

### 2. Implementar InventoryViewModel

Seguir padrão MVI de `TasksViewModel`:

```kotlin
sealed interface InventoryIntent {
    data object LoadProducts : InventoryIntent
    data class AddProduct(val product: Product) : InventoryIntent
    // ...
}

data class InventoryState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false
)

sealed interface InventoryEffect {
    data class ShowToast(val message: String) : InventoryEffect
}

class InventoryViewModel(/* ... */) : ViewModel() { /* ... */ }
```

### 3. Criar InventoryScreen

Similar a `TasksScreen.kt`:

```kotlin
@Composable
fun InventoryScreen(
    viewModel: InventoryViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    
    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
        items(state.products) { product ->
            ProductCard(product)
        }
    }
}
```

### 4. Implementar Barcode Scanner

Use ML Kit + CameraX em `feature-barcode`:

```kotlin
@Composable
fun BarcodeScannerScreen(
    onBarcodeScanned: (String) -> Unit
) {
    val context = LocalContext.current
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    
    // CameraX Preview + ML Kit Barcode Scanning
}
```

### 5. Adicionar Navigation

Criar `app/src/main/java/.../navigation/`:

```kotlin
@Composable
fun MorandoNavigation(
    navController: NavHostController
) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen() }
        composable("tasks") { TasksScreen() }
        composable("inventory") { InventoryScreen() }
        composable("shopping") { ShoppingScreen() }
        composable("scanner") { BarcodeScannerScreen() }
    }
}
```

## 📚 Recursos de Aprendizado

### Documentação do Projeto
- `README.md` - Visão geral
- `memory-bank/projectbrief.md` - Requisitos
- `memory-bank/systemPatterns.md` - Arquitetura detalhada
- `memory-bank/techContext.md` - Stack técnico
- `memory-bank/progress.md` - Progresso e próximas tasks

### Referências Externas
- [Firebase Android](https://firebase.google.com/docs/android/setup)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [MVI Pattern](https://hannesdorfmann.com/android/model-view-intent/)
- [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [ML Kit Barcode](https://developers.google.com/ml-kit/vision/barcode-scanning/android)

## 🐛 Troubleshooting

### Build Failed - google-services.json
**Erro**: `File google-services.json is missing`

**Solução**: Use build type mock ou adicione o arquivo:
```bash
./gradlew assembleMock  # Não precisa Firebase
```

### Firebase Auth Error
**Erro**: `FirebaseException: Auth exception`

**Solução**: Habilite Anonymous Auth no Firebase Console

### Gradle Sync Failed
**Erro**: Dependências não resolvem

**Solução**:
```bash
./gradlew clean
./gradlew build --refresh-dependencies
```

## 💡 Dicas de Desenvolvimento

### 1. Use o build type mock para desenvolvimento offline
```bash
./gradlew assembleMock
```

### 2. Execute ktlint antes de commit
```bash
./gradlew ktlintFormat
git add -A
git commit -m "feat: ..."
```

### 3. Siga o padrão MVI existente
Veja `feature-tasks` como referência completa

### 4. Documente no memory-bank
Atualize `progress.md` conforme implementa features

### 5. Faça commits semânticos
```bash
git commit -m "feat: add inventory screen"
git commit -m "fix: resolve barcode scanning issue"
git commit -m "docs: update progress.md"
```

## 🚀 Deploy

### Debug APK
```bash
./gradlew assembleDebug
# APK em: app/build/outputs/apk/debug/
```

### Release AAB
```bash
./gradlew bundleRelease
# AAB em: app/build/outputs/bundle/release/
```

### GitHub Actions
Push para `main` ou `develop` dispara CI automaticamente

## 📞 Suporte

- Issues: Use o GitHub Issues
- Documentação: Veja `memory-bank/`
- Código de exemplo: `feature-tasks` está completo

---

**Bom desenvolvimento! 🚀**

