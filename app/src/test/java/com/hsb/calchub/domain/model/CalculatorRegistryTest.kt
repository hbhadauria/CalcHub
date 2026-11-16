package com.hsb.calchub.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CalculatorRegistryTest {
    @Test
    fun `allCalculators contains 34 unique calculators with complete metadata`() {
        assertEquals(34, allCalculators.size)
        val routes = allCalculators.map { it.route }
        assertEquals(34, routes.toSet().size)
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
        assertEquals(CalculatorCategory.LOAN, categoryForRoute("home_loan_emi"))
        assertEquals(CalculatorCategory.TAX, categoryForRoute("income_tax"))
        assertEquals(CalculatorCategory.RETIREMENT, categoryForRoute("retirement"))
        assertEquals(CalculatorCategory.TRADING, categoryForRoute("cagr"))
        assertEquals(CalculatorCategory.OTHER, categoryForRoute("inflation"))
    }
}
