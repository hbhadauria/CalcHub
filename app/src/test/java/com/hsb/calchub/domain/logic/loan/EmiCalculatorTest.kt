package com.hsb.calchub.domain.logic.loan

import org.junit.Assert.assertEquals
import org.junit.Test

class EmiCalculatorTest {

    @Test
    fun `calculate EMI returns correctly`() {
        val loanAmount = 100000.0
        val interestRate = 10.0
        val tenureYears = 1.0
        
        val result = EmiCalculator.calculate(loanAmount, interestRate, tenureYears)
        
        // r = 10/12/100 = 0.008333
        // n = 12
        // EMI = (100000 * 0.008333 * (1.008333)^12) / ((1.008333)^12 - 1)
        // EMI ≈ 8791.59
        
        assertEquals(8791.59, result.first, 1.0)
        
        // Total Payment = 8791.59 * 12 = 105499
        assertEquals(105499.0, result.third, 12.0) // Allow delta for cumulative rounding
        
        // Interest = 5499
        assertEquals(5499.0, result.second, 12.0)
    }
}
