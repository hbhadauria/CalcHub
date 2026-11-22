package com.hsb.calchub.domain.logic.investment

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EpfCalculatorTest {

    @Test
    fun `calculate EPF models monthly accrual credited annually`() {
        val twoYear = EpfCalculator.calculate(10_000.0, 12.0, 2.0)
        // Year 1: 120,000 + 0.01 * 10,000 * (1+..+12) = 127,800
        // Year 2: 127,800 + 120,000 + 0.01 * (12*127,800 + 780,000) = 270,936
        assertEquals(240_000.0, twoYear.first, 0.01)
        assertEquals(30_936.0, twoYear.second, 0.01)
        assertEquals(270_936.0, twoYear.third, 0.01)

        val thirtyYear = EpfCalculator.calculate(5000.0, 8.25, 30.0)
        assertEquals(1800000.0, thirtyYear.first, 0.01)
        assertTrue(thirtyYear.third > thirtyYear.first)
        assertEquals(thirtyYear.third - thirtyYear.first, thirtyYear.second, 0.01)
    }

    @Test
    fun `calculate EPF handles zero rate and invalid inputs`() {
        val zeroRate = EpfCalculator.calculate(5000.0, 0.0, 5.0)
        assertEquals(300000.0, zeroRate.first, 0.01)
        assertEquals(0.0, zeroRate.second, 0.01)
        assertEquals(300000.0, zeroRate.third, 0.01)

        assertEquals(0.0, EpfCalculator.calculate(-5000.0, 8.25, 10.0).third, 0.01)
        assertEquals(0.0, EpfCalculator.calculate(5000.0, 8.25, 0.0).third, 0.01)
    }
}
