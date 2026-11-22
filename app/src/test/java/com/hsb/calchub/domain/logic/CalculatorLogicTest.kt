package com.hsb.calchub.domain.logic

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.jupiter.api.Test
import kotlin.math.abs
import kotlin.math.roundToInt

class CalculatorLogicTest {

    private fun assertDoubleEquals(expected: Double, actual: Double, delta: Double = 0.01) {
        assertTrue("Expected $expected but got $actual", abs(expected - actual) < delta)
    }

    @Test
    fun testCalculateSIP() {
        val result = CalculatorLogic.calculateSIP(5000.0, 12.0, 10.0)
        assertDoubleEquals(600000.0, result.first, 1.0)
        assertDoubleEquals(561695.0, result.second, 1000.0)
        assertDoubleEquals(1161695.0, result.third, 1000.0)
    }

    @Test
    fun testCalculateEMI() {
        val result = CalculatorLogic.calculateEMI(1000000.0, 8.5, 5.0)
        assertEquals(20516.0, result.first.roundToInt().toDouble(), 5.0)
    }

    @Test
    fun testDelegatedStepUpTaxAndInflationAdjustment() {
        val stepUp = CalculatorLogic.calculateStepUpSIP(10_000.0, 10.0, 12.0, 15)
        assertTrue(stepUp.totalValue > CalculatorLogic.calculateSIP(10_000.0, 12.0, 15.0).third)

        val fy26Tax = CalculatorLogic.calculateIncomeTaxNewRegimeFy26(1_800_000.0)
        assertDoubleEquals(150_800.0, fy26Tax.second, 1.0)

        val discounted = CalculatorLogic.adjustForInflation(100_000.0, 6.0, 10.0)
        assertDoubleEquals(55_839.48, discounted, 1.0)
    }
}
