package com.hsb.calchub.domain.logic.trading

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MarginCalculatorTest {

    @Test
    fun `calculate Margin returns correctly`() {
        val result = MarginCalculator.calculate(100.0, 1000.0, 5.0)
        assertEquals(20000.0, result.first, 0.01)
        assertEquals(100000.0, result.second, 0.01)
        assertEquals(100000.0, result.third, 0.01)
    }

    @Test
    fun `calculate Margin guards zero leverage and negative inputs`() {
        val zeroLev = MarginCalculator.calculate(100.0, 1000.0, 0.0)
        assertTrue(zeroLev.first.isFinite())
        assertEquals(0.0, zeroLev.first, 0.01)
        assertEquals(0.0, MarginCalculator.calculate(-10.0, 1000.0, 5.0).second, 0.01)
    }
}
