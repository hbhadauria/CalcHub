package com.hsb.calchub.domain.logic.loan

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FlatVsReducingCalculatorTest {

    @Test
    fun `calculate Flat vs Reducing compares same quoted rate accurately`() {
        val loanAmount = 100000.0
        val rate = 10.0
        val tenureYears = 1.0

        val result = FlatVsReducingCalculator.calculate(loanAmount, rate, tenureYears)

        // Flat EMI = (100000 + 10000) / 12 = 9166.67
        assertEquals(9166.67, result.first, 0.1)
        // Reducing EMI at same 10% = 8791.59
        assertEquals(8791.59, result.second, 0.1)
        // Flat EMI is strictly higher than reducing EMI at positive rate
        assertEquals(result.first - result.second, result.third, 0.01)
        assertTrue(result.third > 0.0)
    }

    @Test
    fun `calculate Flat vs Reducing handles zero rate and invalid inputs`() {
        val zeroRate = FlatVsReducingCalculator.calculate(120000.0, 0.0, 1.0)
        assertEquals(10000.0, zeroRate.first, 0.01)
        assertEquals(10000.0, zeroRate.second, 0.01)
        assertEquals(0.0, zeroRate.third, 0.01)

        assertEquals(0.0, FlatVsReducingCalculator.calculate(0.0, 10.0, 2.0).first, 0.01)
        assertEquals(0.0, FlatVsReducingCalculator.calculate(100000.0, -5.0, 2.0).first, 0.01)
    }
}
