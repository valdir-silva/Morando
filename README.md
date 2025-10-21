# Morando - Gerenciamento Doméstico

Aplicativo Android para gerenciar tarefas domésticas e estoque de produtos com geração automática de lista de compras.

## 📱 Sobre o Projeto

Morando é um app completo para ajudar no gerenciamento da casa, permitindo:
- **Tarefas Diárias/Semanais**: Organize suas rotinas com listas de tarefas simples e eficientes
- **Estoque de Produtos**: Cadastre alimentos e produtos de limpeza com fotos, código de barras, data de vencimento e previsão de quando acabam
- **Lista de Compras Automática**: Gere listas baseadas em produtos que estão acabando ou vencidos
- **Scanner de Código de Barras**: Cadastre produtos rapidamente usando a câmera

## 🏗 Arquitetura

O projeto segue **MVI (Model-View-Intent)** + **Clean Architecture** com:

- **Camada de Apresentação**: ViewModels com MVI (Intent → State → Effect)
- **Camada de Domínio**: Use Cases e Repository interfaces (puro Kotlin)
- **Camada de Dados**: Implementação com Firebase Firestore e Storage
- **SDUI (Server-Driven UI)**: Interface configurável via Firebase para testes A/B

### Modularização

```
:app                    → Aplicação principal
:core                   → Utilities e extensions (puro Kotlin)
:domain                 → Lógica de negócio, models e interfaces
:data                   → Firebase e implementação de repositórios
:sdui                   → Engine de renderização SDUI
:feature
  :feature-tasks        → Tarefas diárias/semanais
  :feature-inventory    → Estoque de produtos
  :feature-shopping     → Lista de compras
  :feature-barcode      → Scanner de código de barras
```

## 🚀 Tecnologias

- **Linguagem**: Kotlin 2.0.21
- **UI**: Jetpack Compose com Material 3
- **Backend**: Firebase (Firestore, Storage, Auth)
- **DI**: Koin
- **Networking**: Retrofit + Moshi
- **Câmera**: CameraX + ML Kit Barcode Scanning
- **Imagens**: Coil
- **Testes**: JUnit, Mockk, Turbine
- **CI/CD**: GitHub Actions + Fastlane
- **Code Quality**: Detekt + ktlint

## 🔧 Build Variants

O projeto possui 3 build types:

- **debug**: Desenvolvimento local com Firebase
- **mock**: Dados mockados (sem necessidade de Firebase)
- **release**: Produção com ProGuard ativo

```bash
# Build debug
./gradlew assembleDebug

# Build mock
./gradlew assembleMock

# Build release
./gradlew bundleRelease
```

## 📦 Setup

### Pré-requisitos

- JDK 17
- Android Studio Hedgehog ou superior
- SDK Android 24+

### Configuração Firebase

1. Crie um projeto no [Firebase Console](https://console.firebase.google.com/)
2. Adicione um app Android com o package `com.alunando.morando`
3. Baixe o arquivo `google-services.json` e coloque em `app/`
4. Configure as regras de segurança do Firestore (veja `memory-bank/techContext.md`)

### Executar o Projeto

```bash
# Clone o repositório
git clone https://github.com/seu-usuario/morando.git
cd morando

# Execute
./gradlew assembleDebug
```

## 🧪 Testes

```bash
# Testes unitários
./gradlew testDebugUnitTest

# Testes instrumentados
./gradlew connectedDebugAndroidTest

# Lint e code style
./gradlew ktlintCheck detekt
```

### Fastlane

```bash
# CI completo
fastlane ci

# Apenas lint
fastlane lint

# Apenas testes unitários
fastlane unit_tests

# Testes de UI
fastlane ui_tests

# Screenshots
fastlane screenshots
```

## 📊 CI/CD

O projeto usa GitHub Actions com 3 jobs:

1. **lint-and-test**: Executa ktlint, detekt e testes unitários
2. **ui-tests**: Executa testes instrumentados em Android API 30 e 33
3. **build**: Gera APKs debug/mock e AAB release

## 🎯 Roadmap

### ✅ Fase 1: Foundation (Concluído)
- [x] Setup de módulos
- [x] Firebase configuration
- [x] Build types
- [x] CI/CD com GitHub Actions e Fastlane

### 🚧 Fase 2: Core Features (Em Desenvolvimento)
- [x] Feature Tasks (MVI completo)
- [ ] Feature Inventory (implementação completa)
- [ ] Feature Barcode (ML Kit + CameraX)
- [ ] Feature Shopping (geração automática)

### 📋 Fase 3: SDUI e Melhorias
- [ ] Engine SDUI completa
- [ ] Configurações de telas no Firestore
- [ ] Testes de UI completos
- [ ] Performance optimization

### 🌍 Futuro: Multiplataforma
- [ ] Preparação para KMP
- [ ] Versão iOS
- [ ] Versão Web

## 📚 Documentação

Veja a pasta `memory-bank/` para documentação detalhada:

- `projectbrief.md`: Visão geral do projeto
- `systemPatterns.md`: Arquitetura e padrões
- `techContext.md`: Stack técnico e configurações
- `progress.md`: Progresso e próximas tasks

## 🤝 Contribuindo

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'feat: add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 📄 Licença

Este projeto está sob a licença MIT.

## 👨‍💻 Autor

Desenvolvido por Valdir Silva (@alunando)

---

**Nota**: Este projeto foi desenvolvido seguindo as melhores práticas de Clean Architecture, MVI e modularização, preparado para escalar para Kotlin Multiplatform no futuro.

