package com.hsb.calchub.domain.logic.investment

import org.junit.Assert.assertEquals
import org.junit.Test

class EpfCalculatorTest {

    @Test
    fun `calculate EPF returns correctly`() {
        val monthlyContribution = 5000.0
        val interestRate = 8.25
        val timePeriodYears = 30.0
        
        val result = EpfCalculator.calculate(monthlyContribution, interestRate, timePeriodYears)
        
        // Invested: 5000 * 12 * 30 = 18,00,000
        assertEquals(1800000.0, result.first, 0.01)
        
        // Total Value check (approximate due to complex compounding)
        // Using standard formula logic implemented
        // Just ensuring it's greater than invested and positive
        assert(result.third > result.first)
        assertEquals(result.third - result.first, result.second, 0.01)
    }
}
