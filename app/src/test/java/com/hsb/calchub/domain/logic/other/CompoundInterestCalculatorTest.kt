package com.hsb.calchub.domain.logic.other

import org.junit.Assert.assertEquals
import org.junit.Test

class CompoundInterestCalculatorTest {

    @Test
    fun `calculate Compound Interest returns correctly`() {
        val result = CompoundInterestCalculator.calculate(10000.0, 10.0, 2.0, 1)
        assertEquals(10000.0, result.first, 0.01)
        assertEquals(2100.0, result.second, 0.01)
        assertEquals(12100.0, result.third, 0.01)
    }

    @Test
    fun `calculate Compound Interest guards zero frequency and negative inputs`() {
        val zeroFreq = CompoundInterestCalculator.calculate(10000.0, 10.0, 2.0, 0)
        assertEquals(12100.0, zeroFreq.third, 0.01)

        assertEquals(0.0, CompoundInterestCalculator.calculate(-1000.0, 10.0, 2.0, 1).third, 0.01)
        assertEquals(0.0, CompoundInterestCalculator.calculate(1000.0, -5.0, 2.0, 1).third, 0.01)
    }
}
