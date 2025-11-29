package com.hsb.calchub.domain.logic.investment

import org.junit.Assert.assertEquals
import org.junit.Test

class SsyCalculatorTest {

    @Test
    fun `calculate SSY returns correctly`() {
        val yearlyDeposit = 100000.0
        val interestRate = 8.2
        val timePeriodYears = 15.0 // SSY usually matures at 21, but deposits for 15
        
        val result = SsyCalculator.calculate(yearlyDeposit, interestRate, timePeriodYears)
        
        // Invested: 100000 * 15 = 15,00,000
        assertEquals(1500000.0, result.first, 0.01)
        
        // Total Value check
        assert(result.third > result.first)
        assertEquals(result.third - result.first, result.second, 0.01)
    }
}
