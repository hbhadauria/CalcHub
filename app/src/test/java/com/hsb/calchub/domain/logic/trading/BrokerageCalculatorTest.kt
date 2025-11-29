package com.hsb.calchub.domain.logic.trading

import org.junit.Assert.assertEquals
import org.junit.Test

class BrokerageCalculatorTest {

    @Test
    fun `calculate Brokerage returns correctly for delivery`() {
        val tradeValue = 100000.0
        val brokerageRate = 0.5
        val isIntraday = false
        
        val result = BrokerageCalculator.calculate(tradeValue, brokerageRate, isIntraday)
        
        // Brokerage = 100000 * 0.5% = 500
        // STT Delivery = 100000 * 0.1% = 100
        // Total Charges = 600
        
        assertEquals(100000.0, result.first, 0.01)
        assertEquals(600.0, result.second, 0.01)
        assertEquals(99400.0, result.third, 0.01)
    }

    @Test
    fun `calculate Brokerage returns correctly for intraday`() {
        val tradeValue = 100000.0
        val brokerageRate = 0.03
        val isIntraday = true
        
        val result = BrokerageCalculator.calculate(tradeValue, brokerageRate, isIntraday)
        
        // Brokerage = 100000 * 0.03% = 30
        // STT Intraday = 100000 * 0.025% = 25
        // Total Charges = 55
        
        assertEquals(100000.0, result.first, 0.01)
        assertEquals(55.0, result.second, 0.01)
        assertEquals(99945.0, result.third, 0.01)
    }
}
