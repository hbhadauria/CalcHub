package com.hsb.calchub.domain.logic.tax

import org.junit.Assert.assertEquals
import org.junit.Test

class GstCalculatorTest {

    @Test
    fun `calculate GST exclusive returns correctly`() {
        val amount = 1000.0
        val gstRate = 18.0
        val isInclusive = false
        
        val result = GstCalculator.calculate(amount, gstRate, isInclusive)
        
        // GST = 1000 * 18% = 180
        // Total = 1180
        
        assertEquals(1000.0, result.first, 0.01)
        assertEquals(180.0, result.second, 0.01)
        assertEquals(1180.0, result.third, 0.01)
    }

    @Test
    fun `calculate GST inclusive returns correctly`() {
        val amount = 1180.0
        val gstRate = 18.0
        val isInclusive = true
        
        val result = GstCalculator.calculate(amount, gstRate, isInclusive)
        
        // Original Amount = 1180 / (1 + 18/100) = 1180 / 1.18 = 1000
        // GST = 1180 - 1000 = 180
        
        assertEquals(1180.0, result.first, 0.01)
        assertEquals(180.0, result.second, 0.01)
        assertEquals(1180.0, result.third, 0.01) // Total amount remains same as input for inclusive
    }
}
