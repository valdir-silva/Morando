# App Module

## Firebase Setup

Para rodar o projeto, você precisa configurar o Firebase:

1. Crie um projeto no [Firebase Console](https://console.firebase.google.com/)
2. Adicione um app Android com package name: `com.alunando.morando`
3. Baixe o arquivo `google-services.json`
4. Copie o arquivo para `app/google-services.json`

Um arquivo de exemplo está disponível em `app/google-services.json.example`.

## Build Variants

- **debug**: Desenvolvimento com Firebase
- **mock**: Dados mockados (não precisa Firebase)
- **release**: Produção com ProGuard

