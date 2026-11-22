package com.hsb.calchub.domain.logic.trading

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CagrCalculatorTest {

    @Test
    fun `calculate CAGR returns correctly`() {
        val result = CagrCalculator.calculate(10000.0, 20000.0, 5.0)
        assertEquals(14.87, result.first, 0.01)
        assertEquals(100.0, result.second, 0.01)
        assertEquals(10000.0, result.third, 0.01)
    }

    @Test
    fun `calculate CAGR guards zero initial value, zero years, and NaN`() {
        listOf(
            CagrCalculator.calculate(0.0, 20000.0, 5.0),
            CagrCalculator.calculate(10000.0, 20000.0, 0.0),
            CagrCalculator.calculate(10000.0, -500.0, 5.0),
            CagrCalculator.calculate(Double.NaN, 20000.0, 5.0)
        ).forEach {
            assertTrue(it.first.isFinite() && it.second.isFinite() && it.third.isFinite())
            assertEquals(0.0, it.first, 0.01)
        }
    }
}
