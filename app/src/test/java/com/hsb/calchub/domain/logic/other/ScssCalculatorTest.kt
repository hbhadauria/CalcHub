package com.hsb.calchub.domain.logic.other

import org.junit.Assert.assertEquals
import org.junit.Test

class ScssCalculatorTest {

    @Test
    fun `calculate SCSS returns correctly`() {
        val investment = 100000.0
        val interestRate = 8.2
        
        val result = ScssCalculator.calculate(investment, interestRate)
        
        // Quarterly Interest = (100000 * 8.2%) / 4 = 2050
        // Yearly = 8200
        // Total 5 Years = 41000
        
        assertEquals(100000.0, result.first, 0.01)
        assertEquals(41000.0, result.second, 0.01)
        assertEquals(141000.0, result.third, 0.01)
    }
}
