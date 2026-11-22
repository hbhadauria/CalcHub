package com.hsb.calchub.domain.logic.retirement

import org.junit.Assert.assertEquals
import org.junit.Test

class GratuityCalculatorTest {

    @Test
    fun `calculate Gratuity returns correctly below and above exempt limit`() {
        val belowLimit = GratuityCalculator.calculate(50000.0, 10.0)
        assertEquals(288461.54, belowLimit.first, 1.0)
        assertEquals(288461.54, belowLimit.second, 1.0)
        assertEquals(0.0, belowLimit.third, 0.01)

        val aboveLimit = GratuityCalculator.calculate(200000.0, 25.0)
        val expected = (200000.0 * 25.0 * 15.0) / 26.0
        assertEquals(expected, aboveLimit.first, 1.0)
        assertEquals(2_000_000.0, aboveLimit.second, 0.01)
        assertEquals(expected - 2_000_000.0, aboveLimit.third, 1.0)
    }

    @Test
    fun `calculate Gratuity guards invalid inputs`() {
        assertEquals(0.0, GratuityCalculator.calculate(0.0, 10.0).first, 0.01)
        assertEquals(0.0, GratuityCalculator.calculate(50000.0, -5.0).first, 0.01)
        assertEquals(0.0, GratuityCalculator.calculate(Double.NaN, 5.0).first, 0.01)
    }
}
