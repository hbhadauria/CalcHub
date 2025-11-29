package com.hsb.calchub.domain.logic.investment

import org.junit.Assert.assertEquals
import org.junit.Test

class SipCalculatorTest {

    @Test
    fun `calculate SIP returns correctly`() {
        val monthlyInvestment = 5000.0
        val expectedReturnRate = 12.0
        val timePeriodYears = 10.0

        val result = SipCalculator.calculate(monthlyInvestment, expectedReturnRate, timePeriodYears)

        // Expected values calculated manually or using a trusted calculator
        // P = 5000, i = 12%/12 = 0.01, n = 120
        // M = 5000 * ((1.01)^120 - 1) / 0.01 * 1.01
        // M ≈ 5000 * (3.30038 - 1) / 0.01 * 1.01
        // M ≈ 5000 * 230.038 * 1.01 ≈ 11,61,695
        
        // Invested: 5000 * 120 = 6,00,000
        
        assertEquals(600000.0, result.first, 0.01)
        assertEquals(1161695.0, result.third, 1.0) // Allow small delta for rounding
        assertEquals(561695.0, result.second, 1.0)
    }
}
