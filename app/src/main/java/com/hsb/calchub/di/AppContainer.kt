package com.hsb.calchub.di

import android.content.Context
import androidx.compose.runtime.staticCompositionLocalOf
import com.hsb.calchub.data.repository.FavoritesRepository
import com.hsb.calchub.data.repository.ThemeRepository
import com.hsb.calchub.data.repository.UserDataRepository

/**
 * Lightweight pure-Kotlin Dependency Injection container (Decision D4 — Option A).
 * Replaces ad-hoc Context lookups inside composables without annotation codegen overhead.
 */
class AppContainer(context: Context) {
    private val appContext = context.applicationContext
    val favoritesRepository: FavoritesRepository = FavoritesRepository.getInstance(appContext)
    val themeRepository: ThemeRepository = ThemeRepository(appContext)
    val userDataRepository: UserDataRepository = UserDataRepository(appContext)
}

val LocalAppContainer = staticCompositionLocalOf<AppContainer> {
    error("AppContainer not provided in CompositionLocal tree")
}
