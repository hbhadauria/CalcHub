package com.hsb.calchub.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class FavoritesRepositoryTest {

    private lateinit var context: Context
    private lateinit var repository: FavoritesRepository
    private lateinit var prefs: SharedPreferences

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        // Clear any existing instance
        clearSingleton()
        // Clear SharedPreferences
        context.getSharedPreferences("calc_favorites", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
        repository = FavoritesRepository(context)
        prefs = context.getSharedPreferences("calc_favorites", Context.MODE_PRIVATE)
    }

    @After
    fun tearDown() {
        // Clear SharedPreferences after each test
        prefs.edit().clear().commit()
        clearSingleton()
    }

    private fun clearSingleton() {
        // Use reflection to clear the singleton instance
        try {
            val instanceField = FavoritesRepository::class.java.getDeclaredField("INSTANCE")
            instanceField.isAccessible = true
            instanceField.set(null, null)
        } catch (e: Exception) {
            // Ignore if field doesn't exist
        }
    }

    @Test
    fun `initial favorites should be empty`() = runTest {
        val favorites = repository.favorites.first()
        assertTrue(favorites.isEmpty())
    }

    @Test
    fun `toggleFavorite should add calculator to favorites`() = runTest {
        val calculatorId = "sip"
        
        repository.toggleFavorite(calculatorId)
        
        val favorites = repository.favorites.first()
        assertTrue(favorites.contains(calculatorId))
        assertEquals(1, favorites.size)
    }

    @Test
    fun `toggleFavorite should remove calculator from favorites`() = runTest {
        val calculatorId = "sip"
        
        // Add favorite
        repository.toggleFavorite(calculatorId)
        assertTrue(repository.favorites.first().contains(calculatorId))
        
        // Remove favorite
        repository.toggleFavorite(calculatorId)
        
        val favorites = repository.favorites.first()
        assertFalse(favorites.contains(calculatorId))
        assertTrue(favorites.isEmpty())
    }

    @Test
    fun `toggleFavorite should handle multiple calculators`() = runTest {
        val calc1 = "sip"
        val calc2 = "emi"
        val calc3 = "gst"
        
        repository.toggleFavorite(calc1)
        repository.toggleFavorite(calc2)
        repository.toggleFavorite(calc3)
        
        val favorites = repository.favorites.first()
        assertEquals(3, favorites.size)
        assertTrue(favorites.contains(calc1))
        assertTrue(favorites.contains(calc2))
        assertTrue(favorites.contains(calc3))
    }

    @Test
    fun `isFavorite should return true for favorited calculator`() {
        val calculatorId = "sip"
        
        repository.toggleFavorite(calculatorId)
        
        assertTrue(repository.isFavorite(calculatorId))
    }

    @Test
    fun `isFavorite should return false for non-favorited calculator`() {
        val calculatorId = "sip"
        
        assertFalse(repository.isFavorite(calculatorId))
    }

    @Test
    fun `favorites should persist in SharedPreferences`() {
        val calculatorId = "sip"
        
        repository.toggleFavorite(calculatorId)
        
        // Verify it's saved in SharedPreferences
        val savedFavorites = prefs.getStringSet("favorite_ids", emptySet())
        assertNotNull(savedFavorites)
        assertTrue(savedFavorites!!.contains(calculatorId))
    }

    @Test
    fun `favorites should be loaded from SharedPreferences on init`() {
        val calculatorId = "sip"
        
        // Manually save to SharedPreferences
        prefs.edit()
            .putStringSet("favorite_ids", setOf(calculatorId))
            .commit()
        
        // Create new repository instance
        clearSingleton()
        val newRepository = FavoritesRepository(context)
        
        assertTrue(newRepository.isFavorite(calculatorId))
    }

    @Test
    fun `getInstance should return same instance`() {
        val instance1 = FavoritesRepository.getInstance(context)
        val instance2 = FavoritesRepository.getInstance(context)
        
        assertSame(instance1, instance2)
    }

    @Test
    fun `getInstance should use application context`() {
        val instance = FavoritesRepository.getInstance(context)
        
        assertNotNull(instance)
    }

    @Test
    fun `favorites flow should emit updates when toggled`() = runTest {
        val calculatorId = "sip"
        
        // Initial state
        var favorites = repository.favorites.first()
        assertTrue(favorites.isEmpty())
        
        // Add favorite
        repository.toggleFavorite(calculatorId)
        favorites = repository.favorites.first()
        assertTrue(favorites.contains(calculatorId))
        
        // Remove favorite
        repository.toggleFavorite(calculatorId)
        favorites = repository.favorites.first()
        assertFalse(favorites.contains(calculatorId))
    }

    @Test
    fun `toggleFavorite should handle same calculator multiple times`() = runTest {
        val calculatorId = "sip"
        
        // Add
        repository.toggleFavorite(calculatorId)
        assertTrue(repository.isFavorite(calculatorId))
        
        // Remove
        repository.toggleFavorite(calculatorId)
        assertFalse(repository.isFavorite(calculatorId))
        
        // Add again
        repository.toggleFavorite(calculatorId)
        assertTrue(repository.isFavorite(calculatorId))
        
        // Remove again
        repository.toggleFavorite(calculatorId)
        assertFalse(repository.isFavorite(calculatorId))
    }

    @Test
    fun `empty SharedPreferences should return empty set`() = runTest {
        // Ensure SharedPreferences is empty
        prefs.edit().clear().commit()
        
        clearSingleton()
        val newRepository = FavoritesRepository(context)
        
        val favorites = newRepository.favorites.first()
        assertTrue(favorites.isEmpty())
    }
}
