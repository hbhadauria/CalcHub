package com.hsb.calchub.domain.logic.investment

import org.junit.Assert.assertEquals
import org.junit.Test

class RdCalculatorTest {

    @Test
    fun `calculate RD returns correctly`() {
        val monthlyDeposit = 5000.0
        val interestRate = 6.5
        val timePeriodYears = 5.0
        
        val result = RdCalculator.calculate(monthlyDeposit, interestRate, timePeriodYears)
        
        // Invested: 5000 * 60 = 3,00,000
        assertEquals(300000.0, result.first, 0.01)
        
        // Total Value check
        assert(result.third > result.first)
        assertEquals(result.third - result.first, result.second, 0.01)
    }
}
