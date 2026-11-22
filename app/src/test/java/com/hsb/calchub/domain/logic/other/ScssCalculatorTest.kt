package com.hsb.calchub.domain.logic.other

import org.junit.Assert.assertEquals
import org.junit.Test

class ScssCalculatorTest {

    @Test
    fun `calculate SCSS returns correctly`() {
        val result = ScssCalculator.calculate(100000.0, 8.2)
        assertEquals(100000.0, result.first, 0.01)
        assertEquals(41000.0, result.second, 0.01)
        assertEquals(141000.0, result.third, 0.01)
    }

    @Test
    fun `calculate SCSS guards negative and zero inputs`() {
        assertEquals(0.0, ScssCalculator.calculate(-5000.0, 8.2).third, 0.01)
        assertEquals(0.0, ScssCalculator.calculate(100000.0, -2.0).third, 0.01)
    }
}
