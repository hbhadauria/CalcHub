package com.hsb.calchub.domain.logic.loan

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EmiCalculatorTest {

    @Test
    fun `calculate EMI returns correctly`() {
        val result = EmiCalculator.calculate(100000.0, 10.0, 1.0)
        assertEquals(8791.59, result.first, 1.0)
        assertEquals(105499.0, result.third, 12.0)
        assertEquals(5499.0, result.second, 12.0)
    }

    @Test
    fun `calculate EMI handles zero interest and invalid inputs`() {
        val zeroInterest = EmiCalculator.calculate(120000.0, 0.0, 1.0)
        assertEquals(10000.0, zeroInterest.first, 0.01)
        assertEquals(0.0, zeroInterest.second, 0.01)
        assertEquals(120000.0, zeroInterest.third, 0.01)

        assertEquals(0.0, EmiCalculator.calculate(100000.0, 10.0, 0.0).first, 0.01)
        assertEquals(0.0, EmiCalculator.calculate(-100000.0, 10.0, 5.0).first, 0.01)
        assertTrue(EmiCalculator.calculate(Double.NaN, 10.0, 5.0).first.isFinite())
    }
}
