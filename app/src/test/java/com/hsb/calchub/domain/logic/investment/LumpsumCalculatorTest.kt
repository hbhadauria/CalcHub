package com.hsb.calchub.domain.logic.investment

import org.junit.Assert.assertEquals
import org.junit.Test

class LumpsumCalculatorTest {

    @Test
    fun `calculate Lumpsum returns correctly`() {
        val result = LumpsumCalculator.calculate(100000.0, 12.0, 5.0)
        assertEquals(100000.0, result.first, 0.01)
        assertEquals(176234.0, result.third, 1.0)
        assertEquals(76234.0, result.second, 1.0)
    }

    @Test
    fun `calculate Lumpsum handles zero years and zero rate`() {
        val zeroYears = LumpsumCalculator.calculate(100000.0, 12.0, 0.0)
        assertEquals(100000.0, zeroYears.third, 0.01)
        assertEquals(0.0, zeroYears.second, 0.01)

        val zeroRate = LumpsumCalculator.calculate(100000.0, 0.0, 5.0)
        assertEquals(100000.0, zeroRate.third, 0.01)
        assertEquals(0.0, zeroRate.second, 0.01)
    }

    @Test
    fun `calculate Lumpsum guards invalid inputs`() {
        listOf(
            LumpsumCalculator.calculate(0.0, 12.0, 5.0),
            LumpsumCalculator.calculate(-1000.0, 12.0, 5.0),
            LumpsumCalculator.calculate(100000.0, -5.0, 5.0),
            LumpsumCalculator.calculate(100000.0, 12.0, -1.0),
            LumpsumCalculator.calculate(Double.NaN, 12.0, 5.0)
        ).forEach {
            assertEquals(0.0, it.first, 0.01)
            assertEquals(0.0, it.second, 0.01)
            assertEquals(0.0, it.third, 0.01)
        }
    }
}
