package com.hsb.calchub.domain.logic.trading

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StockAverageCalculatorTest {

    @Test
    fun `calculate Stock Average returns correctly`() {
        val result = StockAverageCalculator.calculate(100.0, 100.0, 100.0, 200.0)
        assertEquals(200.0, result.first, 0.01)
        assertEquals(150.0, result.second, 0.01)
        assertEquals(30000.0, result.third, 0.01)
    }

    @Test
    fun `calculate Stock Average guards zero total quantity and negative values`() {
        val zeroQty = StockAverageCalculator.calculate(0.0, 100.0, 0.0, 200.0)
        assertTrue(zeroQty.second.isFinite())
        assertEquals(0.0, zeroQty.second, 0.01)

        assertEquals(0.0, StockAverageCalculator.calculate(-5.0, 100.0, 10.0, 200.0).first, 0.01)
    }
}
