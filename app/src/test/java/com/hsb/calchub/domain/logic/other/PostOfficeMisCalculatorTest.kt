package com.hsb.calchub.domain.logic.other

import org.junit.Assert.assertEquals
import org.junit.Test

class PostOfficeMisCalculatorTest {

    @Test
    fun `calculate Post Office MIS returns correctly`() {
        val investment = 100000.0
        val interestRate = 7.4
        
        val result = PostOfficeMisCalculator.calculate(investment, interestRate)
        
        // Monthly Income = (100000 * 7.4%) / 12
        // = 7400 / 12 ≈ 616.67
        
        assertEquals(100000.0, result.first, 0.01)
        assertEquals(616.67, result.second, 1.0)
        
        // Total 5 years = 616.67 * 12 * 5 = 37000
        assertEquals(37000.0, result.third, 10.0) // Allow delta
    }
}
