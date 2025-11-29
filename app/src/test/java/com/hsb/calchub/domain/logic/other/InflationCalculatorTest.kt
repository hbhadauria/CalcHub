package com.hsb.calchub.domain.logic.other

import org.junit.Assert.assertEquals
import org.junit.Test

class InflationCalculatorTest {

    @Test
    fun `calculate Inflation returns correctly`() {
        val currentPrice = 100.0
        val inflationRate = 10.0
        val years = 2.0
        
        val result = InflationCalculator.calculate(currentPrice, inflationRate, years)
        
        // Future = 100 * (1.1)^2 = 121
        
        assertEquals(100.0, result.first, 0.01)
        assertEquals(21.0, result.second, 0.01)
        assertEquals(121.0, result.third, 0.01)
    }
}
