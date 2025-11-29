package com.hsb.calchub.domain.logic.investment

import org.junit.Assert.assertEquals
import org.junit.Test

class SwpCalculatorTest {

    @Test
    fun `calculate SWP returns correctly`() {
        val totalInvestment = 1000000.0
        val withdrawalPerMonth = 5000.0
        val expectedReturnRate = 8.0
        val timePeriodYears = 5.0
        
        val result = SwpCalculator.calculate(totalInvestment, withdrawalPerMonth, expectedReturnRate, timePeriodYears)
        
        // Invested: Initial investment remains the principal concept here
        assertEquals(1000000.0, result.first, 0.01)
        
        // Total Withdrawn: 5000 * 60 = 3,00,000
        assertEquals(300000.0, result.second, 0.01)
        
        // Final Balance should be less than initial if returns < withdrawals, or more if returns > withdrawals
        // Here 8% of 10L is 80k/year. Withdrawal is 60k/year. So balance should grow.
        assert(result.third > 1000000.0)
    }
}
