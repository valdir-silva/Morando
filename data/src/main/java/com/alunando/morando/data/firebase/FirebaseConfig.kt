package com.alunando.morando.data.firebase

/**
 * Constantes de configuração do Firebase
 */
object FirebaseConfig {
    // Collections
    const val COLLECTION_USERS = "users"
    const val COLLECTION_TASKS = "tasks"
    const val COLLECTION_PRODUCTS = "products"
    const val COLLECTION_SHOPPING_LIST = "shopping_list"
    const val COLLECTION_UI_CONFIGS = "ui_configs"

    // Storage paths
    const val STORAGE_PRODUCTS = "products"
    const val STORAGE_IMAGES = "images"

    // Fields - Common
    const val FIELD_USER_ID = "userId"
    const val FIELD_CREATED_AT = "createdAt"

    // Fields - Tasks
    const val FIELD_TIPO = "tipo"
    const val FIELD_COMPLETA = "completa"

    // Fields - Products
    const val FIELD_NOME = "nome"
    const val FIELD_CATEGORIA = "categoria"
    const val FIELD_CODIGO_BARRAS = "codigoBarras"
    const val FIELD_FOTO_URL = "fotoUrl"
    const val FIELD_DATA_COMPRA = "dataCompra"
    const val FIELD_VALOR = "valor"
    const val FIELD_DETALHES = "detalhes"
    const val FIELD_DATA_VENCIMENTO = "dataVencimento"

    // Fields - Shopping
    const val FIELD_COMPRADO = "comprado"
    const val FIELD_PRODUTO_ID = "produtoId"
    const val FIELD_QUANTIDADE = "quantidade"
}
