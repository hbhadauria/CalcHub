package com.hsb.calchub.domain.logic.investment

import org.junit.Assert.assertEquals
import org.junit.Test

class NscCalculatorTest {

    @Test
    fun `calculate NSC returns correctly`() {
        val result = NscCalculator.calculate(10000.0, 7.7)
        assertEquals(10000.0, result.first, 0.01)
        assertEquals(14490.0, result.third, 1.0)
        assertEquals(4490.0, result.second, 1.0)
    }

    @Test
    fun `calculate NSC handles zero rate and invalid inputs`() {
        val zeroRate = NscCalculator.calculate(10000.0, 0.0)
        assertEquals(10000.0, zeroRate.third, 0.01)
        assertEquals(0.0, zeroRate.second, 0.01)

        assertEquals(0.0, NscCalculator.calculate(-1000.0, 7.7).third, 0.01)
        assertEquals(0.0, NscCalculator.calculate(10000.0, -2.0).third, 0.01)
    }
}
