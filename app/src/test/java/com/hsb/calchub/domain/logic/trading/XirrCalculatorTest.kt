package com.hsb.calchub.domain.logic.trading

import org.junit.Assert.assertEquals
import org.junit.Test

class XirrCalculatorTest {

    @Test
    fun `calculate XIRR returns correctly`() {
        val investments = listOf(10000.0, 20000.0)
        val returns = 40000.0
        
        val result = XirrCalculator.calculate(investments, returns)
        
        // Total Invested = 30000
        // Profit = 10000
        // Return % = 33.33%
        
        assertEquals(30000.0, result.first, 0.01)
        assertEquals(10000.0, result.second, 0.01)
        assertEquals(33.33, result.third, 0.01)
    }
}
