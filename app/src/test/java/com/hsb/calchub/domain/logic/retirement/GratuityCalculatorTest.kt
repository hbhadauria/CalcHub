package com.hsb.calchub.domain.logic.retirement

import org.junit.Assert.assertEquals
import org.junit.Test

class GratuityCalculatorTest {

    @Test
    fun `calculate Gratuity returns correctly`() {
        val lastSalary = 50000.0
        val yearsOfService = 10.0
        
        val result = GratuityCalculator.calculate(lastSalary, yearsOfService)
        
        // Gratuity = (50000 * 10 * 15) / 26
        // = 7500000 / 26 ≈ 288461.53
        
        assertEquals(288461.53, result.first, 1.0)
        
        // Tax Free Limit = 20L. Since 2.88L < 20L, all is tax free.
        assertEquals(288461.53, result.second, 1.0)
        assertEquals(0.0, result.third, 1.0)
    }
}
