# Project Brief: Morando
*Version: 1.0*
*Created: 2025-10-21*
*Last Updated: 2025-10-21*

## Project Overview
App Android para gerenciar casa: tarefas diárias/semanais e estoque de alimentos/produtos com alertas de vencimento e geração automática de lista de compras.

## Core Requirements

### Tarefas
- CRUD de tarefas diárias e semanais
- Interface simples com checkbox
- Separação por tipo (diária/semanal)

### Estoque de Produtos
- Cadastro com: nome, foto, código de barras, data de compra, valor, detalhes, data de vencimento, tempo previsto para acabar
- Scanner de código de barras (ML Kit)
- Upload de fotos (Firebase Storage)
- Alertas de vencimento e produtos acabando

### Lista de Compras
- Geração automática baseada em produtos acabando
- CRUD manual de itens
- Checkbox para marcar como comprado

## Success Criteria
- App funcional com Firebase backend
- Arquitetura MVI + Clean Architecture
- SDUI implementado
- Modularização por features
- CI/CD com GitHub Actions e Fastlane
- Testes unitários e de UI
- Build types (mock, debug, release)

## Arquitetura
- **Padrão**: MVI + Clean Architecture
- **SDUI**: UI configurável via Firestore para flexibilidade
- **Modularização**: Por features (:feature-tasks, :feature-inventory, etc)
- **Backend**: Firebase (Firestore + Storage + Auth anônimo inicial)

## Tecnologias Core
- Kotlin, Jetpack Compose, Koin, Firebase
- Retrofit, Moshi, ML Kit, CameraX, Coil
- Fastlane, GitHub Actions, Detekt, ktlint

## Scope

### In Scope
- CRUD completo de tarefas, produtos e lista de compras
- Scanner de código de barras
- Upload de fotos de produtos
- Autenticação anônima (preparado para email/senha e Google Sign-In)
- SDUI para componentes básicos
- CI/CD completo
- Documentação técnica

### Out of Scope (Futuro)
- Múltiplos usuários / compartilhamento
- Notificações push
- Sincronização offline avançada
- Versões iOS e Web (preparado para KMP)

## Timeline
- **Fase 1 (Foundation)**: Setup, módulos, CI/CD - ✅ Completo
- **Fase 2 (Core Features)**: Implementação das features - 🚧 Em andamento
- **Fase 3 (SDUI)**: Engine SDUI completa - 📋 Planejado
- **Fase 4 (KMP)**: Preparação multiplataforma - 📋 Futuro

## Stakeholders
- Valdir Silva (Desenvolvedor e Usuário Primário)
- Futuros usuários domésticos

---

*Este documento serve como fundação do projeto e informa todos os outros arquivos de memória.*

