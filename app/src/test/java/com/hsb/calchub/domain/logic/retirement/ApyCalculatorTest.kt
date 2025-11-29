package com.hsb.calchub.domain.logic.retirement

import org.junit.Assert.assertEquals
import org.junit.Test

class ApyCalculatorTest {

    @Test
    fun `calculate APY returns correctly for 5000 pension`() {
        val currentAge = 20.0
        val pensionAmount = 5000.0
        
        val result = ApyCalculator.calculate(currentAge, pensionAmount)
        
        // Age <= 20, Pension 5000 -> Monthly = 210
        assertEquals(210.0, result.first, 0.01)
        
        // Years = 60 - 20 = 40
        // Total = 210 * 12 * 40 = 100800
        assertEquals(100800.0, result.second, 0.01)
        
        assertEquals(5000.0, result.third, 0.01)
    }
}
