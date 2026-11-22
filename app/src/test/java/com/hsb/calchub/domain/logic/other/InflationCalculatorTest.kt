package com.hsb.calchub.domain.logic.other

import com.hsb.calchub.domain.logic.CalculatorLogic
import org.junit.Assert.assertEquals
import org.junit.Test

class InflationCalculatorTest {

    @Test
    fun `calculate Inflation returns correctly`() {
        val result = InflationCalculator.calculate(100.0, 10.0, 2.0)
        assertEquals(100.0, result.first, 0.01)
        assertEquals(21.0, result.second, 0.01)
        assertEquals(121.0, result.third, 0.01)
    }

    @Test
    fun `adjustForInflation discounts future value to today purchasing power`() {
        val discounted = CalculatorLogic.adjustForInflation(100_000.0, 6.0, 10.0)
        assertEquals(55_839.48, discounted, 0.5)

        assertEquals(100_000.0, CalculatorLogic.adjustForInflation(100_000.0, 0.0, 10.0), 0.01)
        assertEquals(100_000.0, CalculatorLogic.adjustForInflation(100_000.0, 6.0, 0.0), 0.01)
        assertEquals(0.0, CalculatorLogic.adjustForInflation(-100.0, 6.0, 10.0), 0.01)
        assertEquals(0.0, CalculatorLogic.adjustForInflation(Double.NaN, 6.0, 10.0), 0.01)
    }

    @Test
    fun `calculate Inflation guards negative inputs`() {
        assertEquals(0.0, InflationCalculator.calculate(-100.0, 6.0, 5.0).third, 0.01)
        assertEquals(0.0, InflationCalculator.calculate(100.0, -6.0, 5.0).third, 0.01)
    }
}
