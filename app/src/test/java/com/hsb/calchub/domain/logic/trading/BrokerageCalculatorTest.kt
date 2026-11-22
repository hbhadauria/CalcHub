package com.hsb.calchub.domain.logic.trading

import org.junit.Assert.assertEquals
import org.junit.Test

class BrokerageCalculatorTest {

    @Test
    fun `calculate Brokerage returns correctly for delivery`() {
        val result = BrokerageCalculator.calculate(100000.0, 0.5, false)
        assertEquals(100000.0, result.first, 0.01)
        assertEquals(600.0, result.second, 0.01)
        assertEquals(99400.0, result.third, 0.01)
    }

    @Test
    fun `calculate Brokerage returns correctly for intraday`() {
        val result = BrokerageCalculator.calculate(100000.0, 0.03, true)
        assertEquals(100000.0, result.first, 0.01)
        assertEquals(55.0, result.second, 0.01)
        assertEquals(99945.0, result.third, 0.01)
    }

    @Test
    fun `calculate Brokerage guards invalid inputs`() {
        assertEquals(0.0, BrokerageCalculator.calculate(0.0, 0.03, true).second, 0.01)
        assertEquals(0.0, BrokerageCalculator.calculate(100000.0, -0.1, false).second, 0.01)
    }
}
