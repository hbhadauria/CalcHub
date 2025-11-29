package com.hsb.calchub.domain.logic.trading

import org.junit.Assert.assertEquals
import org.junit.Test

class MarginCalculatorTest {

    @Test
    fun `calculate Margin returns correctly`() {
        val quantity = 100.0
        val price = 1000.0
        val leverage = 5.0
        
        val result = MarginCalculator.calculate(quantity, price, leverage)
        
        // Total Value = 1,00,000
        // Margin Required = 1,00,000 / 5 = 20,000
        
        assertEquals(20000.0, result.first, 0.01)
        assertEquals(100000.0, result.second, 0.01)
        assertEquals(100000.0, result.third, 0.01)
    }
}
