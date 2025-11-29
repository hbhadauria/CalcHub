package com.hsb.calchub.domain.logic.investment

import org.junit.Assert.assertEquals
import org.junit.Test

class NscCalculatorTest {

    @Test
    fun `calculate NSC returns correctly`() {
        val investment = 10000.0
        val interestRate = 7.7
        
        val result = NscCalculator.calculate(investment, interestRate)
        
        // A = P(1 + r/100)^5
        // A = 10000 * (1.077)^5
        // A ≈ 10000 * 1.449 = 14490
        
        assertEquals(10000.0, result.first, 0.01)
        assertEquals(14490.0, result.third, 1.0)
        assertEquals(4490.0, result.second, 1.0)
    }
}
