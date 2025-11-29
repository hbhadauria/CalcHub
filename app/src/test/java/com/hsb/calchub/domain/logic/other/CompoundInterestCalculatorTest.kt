package com.hsb.calchub.domain.logic.other

import org.junit.Assert.assertEquals
import org.junit.Test

class CompoundInterestCalculatorTest {

    @Test
    fun `calculate Compound Interest returns correctly`() {
        val principal = 10000.0
        val rate = 10.0
        val timeYears = 2.0
        val compoundingFrequency = 1 // Annually
        
        val result = CompoundInterestCalculator.calculate(principal, rate, timeYears, compoundingFrequency)
        
        // A = 10000 * (1 + 0.1)^2
        // A = 10000 * 1.21 = 12100
        
        assertEquals(10000.0, result.first, 0.01)
        assertEquals(2100.0, result.second, 0.01)
        assertEquals(12100.0, result.third, 0.01)
    }
}
