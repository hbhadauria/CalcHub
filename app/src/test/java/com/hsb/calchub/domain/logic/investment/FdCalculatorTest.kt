package com.hsb.calchub.domain.logic.investment

import org.junit.Assert.assertEquals
import org.junit.Test

class FdCalculatorTest {

    @Test
    fun `calculate FD returns correctly`() {
        val totalInvestment = 100000.0
        val rateOfInterest = 7.0
        val timePeriodYears = 5.0
        
        val result = FdCalculator.calculate(totalInvestment, rateOfInterest, timePeriodYears)

        // A = P(1 + r/n)^(nt)
        // n = 4 (quarterly)
        // A = 100000 * (1 + 0.07/4)^(20)
        // A = 100000 * (1.0175)^20
        // A ≈ 100000 * 1.41477 = 141477
        
        assertEquals(100000.0, result.first, 0.01)
        assertEquals(141477.0, result.third, 1.0)
        assertEquals(41477.0, result.second, 1.0)
    }
}
