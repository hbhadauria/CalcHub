package com.hsb.calchub.data.repository

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FavoritesRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("calc_favorites", Context.MODE_PRIVATE)
    private val _favorites = MutableStateFlow<Set<String>>(getFavoritesFromPrefs())
    val favorites: StateFlow<Set<String>> = _favorites.asStateFlow()

    private fun getFavoritesFromPrefs(): Set<String> {
        return prefs.getStringSet("favorite_ids", emptySet()) ?: emptySet()
    }

    fun toggleFavorite(calculatorId: String) {
        val current = _favorites.value.toMutableSet()
        if (current.contains(calculatorId)) {
            current.remove(calculatorId)
        } else {
            current.add(calculatorId)
        }
        _favorites.value = current
        prefs.edit().putStringSet("favorite_ids", current).apply()
    }

    fun isFavorite(calculatorId: String): Boolean {
        return _favorites.value.contains(calculatorId)
    }

    companion object {
        @Volatile
        private var INSTANCE: FavoritesRepository? = null

        fun getInstance(context: Context): FavoritesRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: FavoritesRepository(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
