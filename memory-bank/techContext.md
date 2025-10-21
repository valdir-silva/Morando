# Technical Context: Morando
*Version: 1.0*
*Created: 2025-10-21*
*Last Updated: 2025-10-21*

## Technology Stack
- **Platform**: Android (Kotlin)
- **UI Framework**: Jetpack Compose
- **Architecture**: MVI + Clean Architecture
- **Backend**: Firebase (Firestore, Storage, Auth)
- **DI**: Koin 3.5.3
- **Networking**: Retrofit 2.9.0 + Moshi 1.15.0
- **Camera**: CameraX 1.3.1 + ML Kit Barcode Scanning 17.2.0
- **Images**: Coil 2.5.0
- **CI/CD**: GitHub Actions + Fastlane
- **Code Quality**: Detekt 1.23.4 + ktlint

## Development Environment Setup
- **IDE**: Android Studio Hedgehog ou superior / Cursor IDE
- **SDK**: Android API 24+ (Android 7.0)
- **Build System**: Gradle com Kotlin DSL
- **Language**: Kotlin 2.0.21
- **Minimum SDK**: API 24
- **Target SDK**: API 36
- **Compile SDK**: API 36
- **JDK**: 17

## Dependencies

### gradle/libs.versions.toml

```toml
[versions]
agp = "8.13.0"
kotlin = "2.0.21"
koin = "3.5.3"
firebaseBom = "33.1.2"
retrofit = "2.9.0"
moshi = "1.15.0"
mlkitBarcode = "17.2.0"
camerax = "1.3.1"
coil = "2.5.0"
coroutines = "1.7.3"
detekt = "1.23.4"
ktlint = "11.6.1"
```

### Bundles
- **kotlin-coroutines**: core + android
- **koin**: core + android + compose
- **firebase**: firestore + storage + auth
- **camerax**: core + camera2 + lifecycle + view
- **retrofit**: retrofit + converter-moshi + moshi + moshi-kotlin
- **compose**: ui + graphics + tooling-preview + material3
- **testing**: junit + mockk + coroutines-test + turbine + koin-test

## Build Configuration

### Build Types
```kotlin
buildTypes {
    debug {
        isMinifyEnabled = false
        applicationIdSuffix = ".debug"
        buildConfigField("String", "BACKEND_TYPE", "\"FIREBASE\"")
    }
    
    create("mock") {
        isMinifyEnabled = false
        applicationIdSuffix = ".mock"
        buildConfigField("String", "BACKEND_TYPE", "\"MOCK\"")
    }
    
    release {
        isMinifyEnabled = true
        isShrinkResources = true
        proguardFiles(...)
        buildConfigField("String", "BACKEND_TYPE", "\"FIREBASE\"")
    }
}
```

## Firebase Configuration

### Collections Structure
```
/users/{userId}
  - createdAt, email, isAnonymous

/users/{userId}/tasks/{taskId}
  - titulo, descricao, tipo, completa, createdAt

/users/{userId}/products/{productId}
  - nome, categoria, codigoBarras, fotoUrl
  - dataCompra, valor, detalhes, dataVencimento
  - diasParaAcabar, createdAt

/users/{userId}/shopping_list/{itemId}
  - produtoId, nome, quantidade, comprado, createdAt

/ui_configs/{screenId}
  - version, layout, updatedAt
```

### Storage Structure
```
/users/{userId}/products/{productId}/images/{imageId}.jpg
```

### Security Rules
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId}/{document=**} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
    match /ui_configs/{screenId} {
      allow read: if request.auth != null;
      allow write: if false; // Admin only
    }
  }
}
```

## Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/alunando/morando/
│   │   │   ├── di/
│   │   │   │   └── AppModule.kt
│   │   │   ├── ui/theme/
│   │   │   ├── MainActivity.kt
│   │   │   └── MorandoApplication.kt
│   │   ├── AndroidManifest.xml
│   │   └── res/
│   ├── test/
│   └── androidTest/

core/
└── src/main/java/com/alunando/morando/core/
    ├── Result.kt
    ├── DateExtensions.kt
    └── FlowExtensions.kt

domain/
└── src/main/java/com/alunando/morando/domain/
    ├── model/
    │   ├── Task.kt
    │   ├── Product.kt
    │   └── ShoppingItem.kt
    ├── repository/
    │   ├── TasksRepository.kt
    │   ├── InventoryRepository.kt
    │   └── ShoppingRepository.kt
    └── usecase/
        ├── GetDailyTasksUseCase.kt
        ├── MarkTaskCompleteUseCase.kt
        └── ...

data/
└── src/main/java/com/alunando/morando/data/
    ├── firebase/
    │   ├── AuthManager.kt
    │   └── FirebaseConfig.kt
    ├── datasource/
    │   └── TasksRemoteDataSource.kt
    └── repository/
        └── TasksRepositoryImpl.kt

sdui/
└── src/main/java/com/alunando/morando/sdui/
    ├── models/
    │   └── SDUIComponent.kt
    ├── engine/
    │   └── SDUIEngine.kt
    └── components/

feature/
├── feature-tasks/
│   └── src/main/java/...feature/tasks/
│       ├── presentation/
│       │   ├── TasksIntent.kt
│       │   ├── TasksState.kt
│       │   ├── TasksEffect.kt
│       │   └── TasksViewModel.kt
│       └── ui/
│           └── TasksScreen.kt
├── feature-inventory/
├── feature-shopping/
└── feature-barcode/
```

## Code Style Guidelines

### Import Organization
1. Imports do Kotlin (kotlin.*)
2. Imports do Android (android.*)
3. Imports do AndroidX (androidx.*)
4. Imports do Jetpack Compose (androidx.compose.*)
5. Imports de bibliotecas externas (com.*, org.*)
6. Imports locais do projeto (com.alunando.morando.*)

### Formatting
- **Indentação**: 4 espaços
- **Linha máxima**: 120 caracteres
- **Braces**: Mesma linha
- **Naming**: camelCase (variáveis/funções), PascalCase (classes)
- **Comentários**: Português

### Detekt Rules
- Complexidade máxima: 10
- Parâmetros máximos: 6
- Linha máxima: 120
- Forbidden comments: TODO, FIXME, STOPSHIP

## CI/CD Pipeline

### GitHub Actions Workflow
1. **lint-and-test** (Ubuntu):
   - ktlint, detekt
   - Testes unitários
   - Upload de reports

2. **ui-tests** (macOS):
   - Matrix: API 30, 33
   - Testes instrumentados
   - Screenshots (API 30)
   - Upload de artifacts

3. **build** (Ubuntu):
   - Depende de lint-and-test e ui-tests
   - Build debug, mock, release
   - Upload de APKs/AAB

### Fastlane Lanes
- `fastlane lint`: ktlint + detekt
- `fastlane unit_tests`: Testes unitários
- `fastlane ui_tests`: Testes instrumentados
- `fastlane screenshots`: Captura screenshots
- `fastlane build_debug/mock/release`: Builds
- `fastlane ci`: Pipeline completo

## Testing Approach
- **Unit Tests**: ViewModels, Use Cases (Mockk)
- **Flow Tests**: Turbine para testar Flows
- **UI Tests**: Compose Testing + Espresso
- **Screenshot Tests**: Fastlane Screengrab
- **Mock Build**: Build type sem Firebase

## Performance Considerations
- **LazyColumn**: Listas virtualizadas
- **StateFlow**: Recomposições otimizadas
- **Coil**: Cache de imagens
- **ProGuard**: Minificação e ofuscação
- **Firebase**: Queries otimizadas com índices

## Known Issues & TODO
- [ ] Implementar Crashlytics para logging de erros
- [ ] Adicionar Analytics para tracking de eventos
- [ ] Implementar cache local com DataStore
- [ ] Adicionar suporte a temas (dark mode)
- [ ] Internacionalização (i18n)

## Preparação para KMP
- `:core` e `:domain` são puro Kotlin
- Repositories são interfaces
- Nenhuma dependência Android em domain
- ViewModels preparados para serem shared
- Lógica de negócio isolada em use cases

---

*Este documento descreve as tecnologias usadas no projeto e como estão configuradas.*

