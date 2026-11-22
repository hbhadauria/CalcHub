package com.hsb.calchub.domain.logic.tax

import org.junit.Assert.assertEquals
import org.junit.Test

class TdsCalculatorTest {

    @Test
    fun `calculate TDS returns correctly`() {
        val result = TdsCalculator.calculate(100000.0, 10.0)
        assertEquals(100000.0, result.first, 0.01)
        assertEquals(10000.0, result.second, 0.01)
        assertEquals(90000.0, result.third, 0.01)
    }

    @Test
    fun `calculate TDS guards invalid inputs and clamps above 100 percent`() {
        assertEquals(0.0, TdsCalculator.calculate(-1000.0, 10.0).second, 0.01)
        assertEquals(0.0, TdsCalculator.calculate(1000.0, -5.0).second, 0.01)

        val capped = TdsCalculator.calculate(1000.0, 150.0)
        assertEquals(1000.0, capped.second, 0.01)
        assertEquals(0.0, capped.third, 0.01)
    }
}
