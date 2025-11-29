package com.hsb.calchub.domain.logic

import org.junit.jupiter.api.Test
import org.junit.Assert.*
import kotlin.math.abs
import kotlin.math.roundToInt

class CalculatorLogicTest {

    private fun assertDoubleEquals(expected: Double, actual: Double, delta: Double = 0.01) {
        assertTrue("Expected $expected but got $actual", abs(expected - actual) < delta)
    }

    @org.junit.jupiter.api.Test
    fun testCalculateSIP() {
        val result = CalculatorLogic.calculateSIP(5000.0, 12.0, 10.0)
        assertDoubleEquals(600000.0, result.first, 1.0)
        assertDoubleEquals(561695.0, result.second, 1000.0)
        assertDoubleEquals(1161695.0, result.third, 1000.0)
    }

    @org.junit.jupiter.api.Test
    fun testCalculateEMI() {
        val result = CalculatorLogic.calculateEMI(1000000.0, 8.5, 5.0)
        
        assertEquals(20516.0, result.first.roundToInt().toDouble(), 5.0)
    }
}
