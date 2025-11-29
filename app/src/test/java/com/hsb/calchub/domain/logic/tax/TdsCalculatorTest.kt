package com.hsb.calchub.domain.logic.tax

import org.junit.Assert.assertEquals
import org.junit.Test

class TdsCalculatorTest {

    @Test
    fun `calculate TDS returns correctly`() {
        val amount = 100000.0
        val tdsRate = 10.0
        
        val result = TdsCalculator.calculate(amount, tdsRate)
        
        // TDS = 100000 * 10% = 10000
        // Net = 90000
        
        assertEquals(100000.0, result.first, 0.01)
        assertEquals(10000.0, result.second, 0.01)
        assertEquals(90000.0, result.third, 0.01)
    }
}
