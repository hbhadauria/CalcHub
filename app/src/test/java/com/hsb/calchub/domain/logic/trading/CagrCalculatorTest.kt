package com.hsb.calchub.domain.logic.trading

import org.junit.Assert.assertEquals
import org.junit.Test

class CagrCalculatorTest {

    @Test
    fun `calculate CAGR returns correctly`() {
        val initialValue = 10000.0
        val finalValue = 20000.0
        val years = 5.0
        
        val result = CagrCalculator.calculate(initialValue, finalValue, years)
        
        // CAGR = ((20000/10000)^(1/5) - 1) * 100
        // = (2^0.2 - 1) * 100
        // ≈ (1.1487 - 1) * 100 = 14.87%
        
        assertEquals(14.87, result.first, 0.01)
        
        // Total Return = (10000/10000) * 100 = 100%
        assertEquals(100.0, result.second, 0.01)
        
        // Absolute Return = 10000
        assertEquals(10000.0, result.third, 0.01)
    }
}
