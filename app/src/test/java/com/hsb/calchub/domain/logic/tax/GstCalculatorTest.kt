package com.hsb.calchub.domain.logic.tax

import org.junit.Assert.assertEquals
import org.junit.Test

class GstCalculatorTest {

    @Test
    fun `calculate GST exclusive returns correctly`() {
        val result = GstCalculator.calculate(1000.0, 18.0, isInclusive = false)
        assertEquals(1000.0, result.first, 0.01)
        assertEquals(180.0, result.second, 0.01)
        assertEquals(1180.0, result.third, 0.01)
    }

    @Test
    fun `calculate GST inclusive extracts base amount and GST accurately`() {
        val result = GstCalculator.calculate(1180.0, 18.0, isInclusive = true)
        assertEquals(1000.0, result.first, 0.01)
        assertEquals(180.0, result.second, 0.01)
        assertEquals(1180.0, result.third, 0.01)
    }

    @Test
    fun `calculate GST guards against zero rate and invalid inputs`() {
        val zeroRate = GstCalculator.calculate(1000.0, 0.0, isInclusive = true)
        assertEquals(1000.0, zeroRate.first, 0.01)
        assertEquals(0.0, zeroRate.second, 0.01)
        assertEquals(1000.0, zeroRate.third, 0.01)

        assertEquals(0.0, GstCalculator.calculate(-100.0, 18.0, false).third, 0.01)
        assertEquals(0.0, GstCalculator.calculate(1000.0, -5.0, true).third, 0.01)
    }
}
