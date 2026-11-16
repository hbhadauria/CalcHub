package com.hsb.calchub.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.Immutable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

@Immutable
data class RecentCalculation(
    val route: String = "sip",
    val calculatorName: String = "SIP",
    val paramsSummary: String = "₹10,000/mo · 15 yrs · 12%",
    val formattedValue: String = "₹50.46 L",
    val secondarySummary: String = "+₹32.46 L est. wealth gain"
)

@Immutable
data class SavedScenario(
    val id: String,
    val route: String,
    val title: String,
    val summary: String,
    val resultFormatted: String
)

/**
 * Offline persistence for the Home "Continue" banner and Tools Vault saved scenarios.
 */
class UserDataRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _lastUsed = MutableStateFlow(readLastUsed())
    val lastUsed: StateFlow<RecentCalculation> = _lastUsed.asStateFlow()

    private val _savedScenarios = MutableStateFlow(readSavedScenarios())
    val savedScenarios: StateFlow<List<SavedScenario>> = _savedScenarios.asStateFlow()

    private val _selectedToolsTab = MutableStateFlow(prefs.getInt(KEY_TOOLS_TAB, 0).coerceIn(0, 3))
    val selectedToolsTab: StateFlow<Int> = _selectedToolsTab.asStateFlow()

    fun setSelectedToolsTab(index: Int) {
        val clamped = index.coerceIn(0, 3)
        if (_selectedToolsTab.value == clamped) return
        _selectedToolsTab.value = clamped
        prefs.edit().putInt(KEY_TOOLS_TAB, clamped).apply()
    }

    private fun readLastUsed(): RecentCalculation {
        val route = prefs.getString(KEY_LAST_ROUTE, null) ?: return RecentCalculation()
        return RecentCalculation(
            route = route,
            calculatorName = prefs.getString(KEY_LAST_NAME, "SIP") ?: "SIP",
            paramsSummary = prefs.getString(KEY_LAST_PARAMS, "₹10,000/mo · 15 yrs · 12%")
                ?: "₹10,000/mo · 15 yrs · 12%",
            formattedValue = prefs.getString(KEY_LAST_VALUE, "₹50.46 L") ?: "₹50.46 L",
            secondarySummary = prefs.getString(KEY_LAST_SUB, "+₹32.46 L est. wealth gain")
                ?: "+₹32.46 L est. wealth gain"
        )
    }

    private fun readSavedScenarios(): List<SavedScenario> {
        val raw = prefs.getString(KEY_VAULT, null) ?: return DEFAULT_SCENARIOS
        return try {
            val array = JSONArray(raw)
            buildList {
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    add(
                        SavedScenario(
                            id = obj.getString("id"),
                            route = obj.getString("route"),
                            title = obj.getString("title"),
                            summary = obj.getString("summary"),
                            resultFormatted = obj.getString("resultFormatted")
                        )
                    )
                }
            }
        } catch (_: Exception) {
            DEFAULT_SCENARIOS
        }
    }

    fun updateLastUsed(recent: RecentCalculation) {
        if (_lastUsed.value == recent) return
        _lastUsed.value = recent
        prefs.edit()
            .putString(KEY_LAST_ROUTE, recent.route)
            .putString(KEY_LAST_NAME, recent.calculatorName)
            .putString(KEY_LAST_PARAMS, recent.paramsSummary)
            .putString(KEY_LAST_VALUE, recent.formattedValue)
            .putString(KEY_LAST_SUB, recent.secondarySummary)
            .apply()
    }

    fun saveScenario(scenario: SavedScenario) {
        val updated = listOf(scenario) + _savedScenarios.value.filterNot { it.id == scenario.id }
        persistScenarios(updated.take(20))
    }

    fun removeScenario(id: String) {
        persistScenarios(_savedScenarios.value.filterNot { it.id == id })
    }

    fun resetDefaultScenarios() {
        persistScenarios(DEFAULT_SCENARIOS)
    }

    private fun persistScenarios(list: List<SavedScenario>) {
        _savedScenarios.value = list
        val arr = JSONArray()
        list.forEach { item ->
            arr.put(
                JSONObject().apply {
                    put("id", item.id)
                    put("route", item.route)
                    put("title", item.title)
                    put("summary", item.summary)
                    put("resultFormatted", item.resultFormatted)
                }
            )
        }
        prefs.edit().putString(KEY_VAULT, arr.toString()).apply()
    }

    companion object {
        const val PREFS_NAME = "calc_user_data"
        private const val KEY_LAST_ROUTE = "last_route"
        private const val KEY_LAST_NAME = "last_name"
        private const val KEY_LAST_PARAMS = "last_params"
        private const val KEY_LAST_VALUE = "last_value"
        private const val KEY_LAST_SUB = "last_sub"
        private const val KEY_VAULT = "saved_scenarios_json"
        private const val KEY_TOOLS_TAB = "selected_tools_tab"

        val DEFAULT_SCENARIOS = listOf(
            SavedScenario(
                id = "preset_sip",
                route = "sip",
                title = "Wealth SIP Plan",
                summary = "₹10,000/mo · 15 yrs @ 12%",
                resultFormatted = "₹50.46 L"
            ),
            SavedScenario(
                id = "preset_home_loan",
                route = "home_loan_emi",
                title = "Dream Home EMI",
                summary = "₹50 L @ 8.5% · 20 yrs",
                resultFormatted = "₹43,391/mo"
            ),
            SavedScenario(
                id = "preset_fire",
                route = "retirement",
                title = "FIRE Target @ 55",
                summary = "₹60,000/mo expense · 25x corpus",
                resultFormatted = "₹7.73 Cr"
            ),
            SavedScenario(
                id = "preset_tax",
                route = "income_tax",
                title = "FY26 Tax Check",
                summary = "₹18 L CTC · New vs Old",
                resultFormatted = "₹1.51 L"
            )
        )
    }
}
