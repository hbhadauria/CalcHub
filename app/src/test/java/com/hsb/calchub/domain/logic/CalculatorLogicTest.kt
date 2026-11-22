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

        val cg = CalculatorLogic.calculateCapitalGains(300_000.0, 550_000.0, 18, isEquity = true)
        assertDoubleEquals(16_250.0, cg.totalTax, 0.01)

        val goalSip = CalculatorLogic.calculateGoalSIP(1_000_000.0, 12.0, 10.0)
        assertTrue(goalSip.requiredMonthlySip > 0.0)

        val prepay = CalculatorLogic.calculateLoanPrepayment(1_000_000.0, 9.0, 10.0, 2000.0, 0.0)
        assertTrue(prepay.interestSaved > 0.0)

        val card = CalculatorLogic.calculateCreditCardPayoff(50_000.0, 36.0, 5_000.0)
        assertTrue(card.willPayOffFixed)
    }
}
