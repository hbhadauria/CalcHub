package com.hsb.calchub.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CalculatorRegistryTest {
    @Test
    fun `allCalculators contains 42 unique calculators with complete metadata`() {
        assertEquals(42, allCalculators.size)
        val routes = allCalculators.map { it.route }
        assertEquals(42, routes.toSet().size)
        assertEquals(14, allCalculators.count { it.isPopular })
        allCalculators.forEach { calc ->
            assertTrue("Missing subtitle for ${calc.name}", calc.subtitle.isNotBlank())
            assertTrue("Missing sampleValue for ${calc.name}", calc.sampleValue.isNotBlank())
            assertTrue("Missing formula for ${calc.name}", calc.formula.isNotBlank())
        }
    }

    @Test
    fun `categoryForRoute resolves expected screen accent category`() {
        assertEquals(CalculatorCategory.INVESTMENT, categoryForRoute("sip"))
        assertEquals(CalculatorCategory.INVESTMENT, categoryForRoute("step_up_sip"))
        assertEquals(CalculatorCategory.INVESTMENT, categoryForRoute("goal_sip"))
        assertEquals(CalculatorCategory.INVESTMENT, categoryForRoute("spend_vs_invest"))
        assertEquals(CalculatorCategory.LOAN, categoryForRoute("home_loan_emi"))
        assertEquals(CalculatorCategory.LOAN, categoryForRoute("loan_prepayment"))
        assertEquals(CalculatorCategory.LOAN, categoryForRoute("credit_card_emi"))
        assertEquals(CalculatorCategory.LOAN, categoryForRoute("buy_vs_rent"))
        assertEquals(CalculatorCategory.TAX, categoryForRoute("income_tax"))
        assertEquals(CalculatorCategory.TAX, categoryForRoute("capital_gains"))
        assertEquals(CalculatorCategory.TAX, categoryForRoute("freelance_tax"))
        assertEquals(CalculatorCategory.RETIREMENT, categoryForRoute("retirement"))
        assertEquals(CalculatorCategory.RETIREMENT, categoryForRoute("fire"))
        assertEquals(CalculatorCategory.TRADING, categoryForRoute("cagr"))
        assertEquals(CalculatorCategory.OTHER, categoryForRoute("inflation"))
    }
}
