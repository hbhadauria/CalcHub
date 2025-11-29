package com.hsb.calchub.domain.logic.tax

import org.junit.Assert.assertEquals
import org.junit.Test

class IncomeTaxCalculatorTest {

    @Test
    fun `calculate Income Tax returns correctly`() {
        val annualIncome = 1200000.0
        val deductions = 0.0 // Simplified for test
        
        val result = IncomeTaxCalculator.calculate(annualIncome, deductions)
        
        // Taxable = 12,00,000
        // 0 - 2.5L = 0
        // 2.5L - 5L = 5% of 2.5L = 12,500
        // 5L - 10L = 20% of 5L = 1,00,000
        // 10L - 12L = 30% of 2L = 60,000
        
        // Total Tax = 12,500 + 1,00,000 + 60,000 = 1,72,500
        
        assertEquals(1200000.0, result.first, 0.01)
        assertEquals(172500.0, result.second, 0.01)
        assertEquals(1027500.0, result.third, 0.01) // Net Income
    }
}
