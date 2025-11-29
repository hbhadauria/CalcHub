package com.hsb.calchub.domain.logic.other

import org.junit.Assert.assertEquals
import org.junit.Test

class SimpleInterestCalculatorTest {

    @Test
    fun `calculate Simple Interest returns correctly`() {
        val principal = 10000.0
        val rate = 10.0
        val timeYears = 2.0
        
        val result = SimpleInterestCalculator.calculate(principal, rate, timeYears)
        
        // Interest = 10000 * 10 * 2 / 100 = 2000
        
        assertEquals(10000.0, result.first, 0.01)
        assertEquals(2000.0, result.second, 0.01)
        assertEquals(12000.0, result.third, 0.01)
    }
}
