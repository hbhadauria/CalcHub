package com.hsb.calchub.domain.logic.trading

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class XirrCalculatorTest {

    @Test
    fun `calculate XIRR returns correctly`() {
        val result = XirrCalculator.calculate(listOf(10000.0, 20000.0), 40000.0)
        assertEquals(30000.0, result.first, 0.01)
        assertEquals(10000.0, result.second, 0.01)
        assertEquals(33.33, result.third, 0.01)
    }

    @Test
    fun `calculate XIRR guards empty, zero, and negative investments`() {
        listOf(
            XirrCalculator.calculate(emptyList(), 10000.0),
            XirrCalculator.calculate(listOf(0.0, 0.0), 10000.0),
            XirrCalculator.calculate(listOf(-1000.0, 2000.0), 5000.0)
        ).forEach {
            assertTrue(it.first.isFinite() && it.second.isFinite() && it.third.isFinite())
            assertEquals(0.0, it.first, 0.01)
            assertEquals(0.0, it.third, 0.01)
        }
    }
}
