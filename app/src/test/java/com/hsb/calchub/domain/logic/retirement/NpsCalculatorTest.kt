package com.hsb.calchub.domain.logic.retirement

import org.junit.Assert.assertEquals
import org.junit.Test

class NpsCalculatorTest {

    @Test
    fun `calculate NPS returns correctly`() {
        val monthlyContribution = 5000.0
        val expectedReturnRate = 10.0
        val currentAge = 30.0
        val retirementAge = 60.0
        
        val result = NpsCalculator.calculate(monthlyContribution, expectedReturnRate, currentAge, retirementAge)
        
        // Invested: 5000 * 12 * 30 = 18,00,000
        assertEquals(1800000.0, result.first, 0.01)
        
        // Total Value check
        assert(result.third > result.first)
        assertEquals(result.third - result.first, result.second, 0.01)
    }
}
