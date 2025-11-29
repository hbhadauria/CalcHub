package com.hsb.calchub.domain.logic.loan

import org.junit.Assert.assertEquals
import org.junit.Test

class FlatVsReducingCalculatorTest {

    @Test
    fun `calculate Flat vs Reducing returns correctly`() {
        val loanAmount = 100000.0
        val flatRate = 10.0
        val tenureYears = 1.0
        
        val result = FlatVsReducingCalculator.calculate(loanAmount, flatRate, tenureYears)
        
        // Flat Interest = 100000 * 10% * 1 = 10000
        // Total Flat = 110000
        // EMI Flat = 110000 / 12 = 9166.67
        assertEquals(9166.67, result.first, 1.0)
        
        // Reducing Rate approx = 10 * 1.8 = 18% (as per logic)
        // EMI Reducing at 18% for 1 year
        // r = 1.5% = 0.015
        // EMI = 100000 * 0.015 * (1.015)^12 / ((1.015)^12 - 1)
        // EMI ≈ 9168
        
        assertEquals(9168.0, result.second, 10.0)
        
        // Difference
        assertEquals(result.first - result.second, result.third, 0.01)
    }
}
