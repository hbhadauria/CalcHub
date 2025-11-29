package com.hsb.calchub.domain.logic.investment

import org.junit.Assert.assertEquals
import org.junit.Test

class PpfCalculatorTest {

    @Test
    fun `calculate PPF returns correctly`() {
        val yearlyInvestment = 150000.0
        val interestRate = 7.1
        val timePeriodYears = 15.0
        
        val result = PpfCalculator.calculate(yearlyInvestment, interestRate, timePeriodYears)
        
        // Invested: 150000 * 15 = 22,50,000
        assertEquals(2250000.0, result.first, 0.01)
        
        // Total Value check
        assert(result.third > result.first)
        assertEquals(result.third - result.first, result.second, 0.01)
    }
}
