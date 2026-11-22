package com.hsb.calchub.domain.logic.investment

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SipCalculatorTest {

    @Test
    fun `calculate SIP returns correctly`() {
        val result = SipCalculator.calculate(5000.0, 12.0, 10.0)
        assertEquals(600000.0, result.first, 0.01)
        assertEquals(1161695.0, result.third, 1.0)
        assertEquals(561695.0, result.second, 1.0)
    }

    @Test
    fun `calculate SIP with zero return rate returns invested amount`() {
        val result = SipCalculator.calculate(5000.0, 0.0, 5.0)
        assertEquals(300000.0, result.first, 0.01)
        assertEquals(0.0, result.second, 0.01)
        assertEquals(300000.0, result.third, 0.01)
    }

    @Test
    fun `calculate SIP guards against zero negative and NaN inputs`() {
        listOf(
            SipCalculator.calculate(0.0, 12.0, 10.0),
            SipCalculator.calculate(-5000.0, 12.0, 10.0),
            SipCalculator.calculate(5000.0, -5.0, 10.0),
            SipCalculator.calculate(5000.0, 12.0, 0.0),
            SipCalculator.calculate(5000.0, 12.0, -2.0),
            SipCalculator.calculate(Double.NaN, 12.0, 10.0)
        ).forEach { res ->
            assertTrue(res.first.isFinite() && res.second.isFinite() && res.third.isFinite())
            assertEquals(0.0, res.first, 0.01)
            assertEquals(0.0, res.second, 0.01)
            assertEquals(0.0, res.third, 0.01)
        }
    }
}
