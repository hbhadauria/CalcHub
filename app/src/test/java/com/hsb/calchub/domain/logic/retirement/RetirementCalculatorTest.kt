package com.hsb.calchub.domain.logic.retirement

import org.junit.Assert.assertEquals
import org.junit.Test

class RetirementCalculatorTest {

    @Test
    fun `calculate Retirement returns correctly`() {
        val currentAge = 30.0
        val retirementAge = 60.0
        val monthlyExpenses = 50000.0
        val inflationRate = 6.0
        val expectedReturn = 12.0
        
        val result = RetirementCalculator.calculate(currentAge, retirementAge, monthlyExpenses, inflationRate, expectedReturn)
        
        // Future Expenses = 50000 * (1.06)^30
        // ≈ 50000 * 5.743 = 287150
        
        // Corpus Needed = 287150 * 12 * 25 ≈ 8.61 Cr
        
        // Monthly SIP needed to reach 8.61 Cr in 30 years at 12%
        
        // Just verify basic logic holds
        assert(result.third > monthlyExpenses * 12) // Future yearly expenses > current
        assert(result.second > result.third) // Corpus > 1 year expense
        assert(result.first > 0) // SIP should be positive
    }
}
