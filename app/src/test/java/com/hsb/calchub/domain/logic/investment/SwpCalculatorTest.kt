package com.hsb.calchub.domain.logic.investment

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SwpCalculatorTest {

    @Test
    fun `calculate SWP returns correctly when returns exceed withdrawals`() {
        val result = SwpCalculator.calculate(1000000.0, 5000.0, 8.0, 5.0)
        assertEquals(1000000.0, result.first, 0.01)
        assertEquals(300000.0, result.second, 0.01)
        assertTrue(result.third > 1000000.0)
    }

    @Test
    fun `calculate SWP stops counting withdrawals once balance depletes`() {
        val depleted = SwpCalculator.calculate(100_000.0, 10_000.0, 0.0, 2.0)
        assertEquals(100_000.0, depleted.first, 0.01)
        assertEquals(100_000.0, depleted.second, 0.01)
        assertEquals(0.0, depleted.third, 0.01)

        val partialFinal = SwpCalculator.calculate(100_000.0, 60_000.0, 0.0, 1.0)
        assertEquals(100_000.0, partialFinal.second, 0.01)
        assertEquals(0.0, partialFinal.third, 0.01)
    }

    @Test
    fun `calculate SWP guards against invalid inputs`() {
        val negInv = SwpCalculator.calculate(-10000.0, 1000.0, 8.0, 5.0)
        assertEquals(0.0, negInv.first, 0.01)
        assertEquals(0.0, negInv.second, 0.01)
        assertEquals(0.0, negInv.third, 0.01)

        val negRate = SwpCalculator.calculate(100000.0, 1000.0, -5.0, 5.0)
        assertEquals(100000.0, negRate.first, 0.01)
        assertEquals(0.0, negRate.second, 0.01)
        assertEquals(100000.0, negRate.third, 0.01)
    }
}
