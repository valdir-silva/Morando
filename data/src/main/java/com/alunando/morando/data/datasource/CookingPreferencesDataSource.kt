package com.alunando.morando.data.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.alunando.morando.domain.model.StoveType
import kotlinx.coroutines.flow.first

/**
 * DataSource para gerenciar preferências de cozinha (tipo de fogão)
 */
class CookingPreferencesDataSource(
    private val context: Context,
) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "cooking_preferences")

    companion object {
        private val STOVE_TYPE_KEY = stringPreferencesKey("stove_type")
    }

    /**
     * Obtém o tipo de fogão preferido
     */
    suspend fun getStoveType(): StoveType {
        val preferences = context.dataStore.data.first()
        val stoveTypeString = preferences[STOVE_TYPE_KEY] ?: StoveType.INDUCTION.name
        return try {
            StoveType.valueOf(stoveTypeString)
        } catch (
            @Suppress("SwallowedException") e: IllegalArgumentException,
        ) {
            StoveType.INDUCTION
        }
    }

    /**
     * Salva o tipo de fogão preferido
     */
    suspend fun saveStoveType(stoveType: StoveType) {
        context.dataStore.edit { preferences ->
            preferences[STOVE_TYPE_KEY] = stoveType.name
        }
    }
}
