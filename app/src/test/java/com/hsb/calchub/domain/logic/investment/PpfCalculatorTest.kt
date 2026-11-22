package com.hsb.calchub.domain.logic.investment

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PpfCalculatorTest {

    @Test
    fun `calculate PPF returns correctly`() {
        val result = PpfCalculator.calculate(150000.0, 7.1, 15.0)
        assertEquals(2250000.0, result.first, 0.01)
        assertTrue(result.third > result.first)
        assertEquals(result.third - result.first, result.second, 0.01)
    }

    @Test
    fun `calculate PPF handles zero rate and invalid inputs`() {
        val zeroRate = PpfCalculator.calculate(100000.0, 0.0, 15.0)
        assertEquals(1500000.0, zeroRate.first, 0.01)
        assertEquals(0.0, zeroRate.second, 0.01)
        assertEquals(1500000.0, zeroRate.third, 0.01)

        val invalid = PpfCalculator.calculate(100000.0, -5.0, 15.0)
        assertEquals(0.0, invalid.third, 0.01)
    }
}
