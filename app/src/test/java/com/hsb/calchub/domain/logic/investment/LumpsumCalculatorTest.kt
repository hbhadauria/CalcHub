package com.hsb.calchub.domain.logic.investment

import org.junit.Assert.assertEquals
import org.junit.Test

class LumpsumCalculatorTest {

    @Test
    fun `calculate Lumpsum returns correctly`() {
        val totalInvestment = 100000.0
        val expectedReturnRate = 12.0
        val timePeriodYears = 5.0

        val result = LumpsumCalculator.calculate(totalInvestment, expectedReturnRate, timePeriodYears)

        // A = P(1 + r/100)^t
        // A = 100000 * (1.12)^5
        // A ≈ 100000 * 1.7623 = 176234
        
        assertEquals(100000.0, result.first, 0.01)
        assertEquals(176234.0, result.third, 1.0)
        assertEquals(76234.0, result.second, 1.0)
    }
}
