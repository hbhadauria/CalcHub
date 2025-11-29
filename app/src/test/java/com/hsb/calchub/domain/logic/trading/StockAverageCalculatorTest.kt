package com.hsb.calchub.domain.logic.trading

import org.junit.Assert.assertEquals
import org.junit.Test

class StockAverageCalculatorTest {

    @Test
    fun `calculate Stock Average returns correctly`() {
        val firstQuantity = 100.0
        val firstPrice = 100.0
        val secondQuantity = 100.0
        val secondPrice = 200.0
        
        val result = StockAverageCalculator.calculate(firstQuantity, firstPrice, secondQuantity, secondPrice)
        
        // Total Qty = 200
        // Total Inv = 10000 + 20000 = 30000
        // Avg Price = 30000 / 200 = 150
        
        assertEquals(200.0, result.first, 0.01)
        assertEquals(150.0, result.second, 0.01)
        assertEquals(30000.0, result.third, 0.01)
    }
}
