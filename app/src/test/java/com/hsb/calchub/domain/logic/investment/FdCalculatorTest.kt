package com.hsb.calchub.domain.logic.investment

import org.junit.Assert.assertEquals
import org.junit.Test

class FdCalculatorTest {

    @Test
    fun `calculate FD returns correctly`() {
        val result = FdCalculator.calculate(100000.0, 7.0, 5.0)
        assertEquals(100000.0, result.first, 0.01)
        assertEquals(141477.0, result.third, 1.0)
        assertEquals(41477.0, result.second, 1.0)
    }

    @Test
    fun `calculate FD guards zero compounding frequency and negative inputs`() {
        val clampedFreq = FdCalculator.calculate(10000.0, 10.0, 2.0, 0)
        assertEquals(12100.0, clampedFreq.third, 0.01)

        val negative = FdCalculator.calculate(-1000.0, 7.0, 5.0)
        assertEquals(0.0, negative.third, 0.01)

        val nan = FdCalculator.calculate(10000.0, Double.NaN, 5.0)
        assertEquals(0.0, nan.third, 0.01)
    }
}
