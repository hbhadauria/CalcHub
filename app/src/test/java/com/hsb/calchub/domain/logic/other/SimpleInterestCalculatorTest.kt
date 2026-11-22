package com.hsb.calchub.domain.logic.other

import org.junit.Assert.assertEquals
import org.junit.Test

class SimpleInterestCalculatorTest {

    @Test
    fun `calculate Simple Interest returns correctly`() {
        val result = SimpleInterestCalculator.calculate(10000.0, 10.0, 2.0)
        assertEquals(10000.0, result.first, 0.01)
        assertEquals(2000.0, result.second, 0.01)
        assertEquals(12000.0, result.third, 0.01)
    }

    @Test
    fun `calculate Simple Interest guards invalid inputs`() {
        assertEquals(0.0, SimpleInterestCalculator.calculate(0.0, 10.0, 2.0).third, 0.01)
        assertEquals(0.0, SimpleInterestCalculator.calculate(10000.0, -5.0, 2.0).third, 0.01)
    }
}
