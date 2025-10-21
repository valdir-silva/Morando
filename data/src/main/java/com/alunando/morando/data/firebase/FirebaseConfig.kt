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

    // Fields
    const val FIELD_USER_ID = "userId"
    const val FIELD_CREATED_AT = "createdAt"
    const val FIELD_TIPO = "tipo"
    const val FIELD_COMPLETA = "completa"
    const val FIELD_COMPRADO = "comprado"
    const val FIELD_DIAS_PARA_ACABAR = "diasParaAcabar"
}

