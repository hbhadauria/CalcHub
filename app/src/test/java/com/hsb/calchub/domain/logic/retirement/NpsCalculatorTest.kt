package com.hsb.calchub.domain.logic.retirement

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NpsCalculatorTest {

    @Test
    fun `calculate NPS returns correctly`() {
        val result = NpsCalculator.calculate(5000.0, 10.0, 30.0, 60.0)
        assertEquals(1800000.0, result.first, 0.01)
        assertTrue(result.third > result.first)
        assertEquals(result.third - result.first, result.second, 0.01)
    }

    @Test
    fun `calculate NPS handles zero return and invalid ages`() {
        val zeroRate = NpsCalculator.calculate(5000.0, 0.0, 30.0, 40.0)
        assertEquals(600000.0, zeroRate.first, 0.01)
        assertEquals(0.0, zeroRate.second, 0.01)
        assertEquals(600000.0, zeroRate.third, 0.01)

        assertEquals(0.0, NpsCalculator.calculate(5000.0, 10.0, 60.0, 60.0).third, 0.01)
        assertEquals(0.0, NpsCalculator.calculate(-500.0, 10.0, 30.0, 60.0).third, 0.01)
    }
}
