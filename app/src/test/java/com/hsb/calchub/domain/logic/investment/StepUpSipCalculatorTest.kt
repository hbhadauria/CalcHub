package com.hsb.calchub.domain.logic.investment

import com.hsb.calchub.domain.logic.CalculatorLogic
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.pow

class StepUpSipCalculatorTest {

    @Test
    fun `zero step-up matches regular SIP calculation`() {
        val stepUp = StepUpSipCalculator.calculate(10_000.0, 0.0, 12.0, 15)
        val regular = CalculatorLogic.calculateSIP(10_000.0, 12.0, 15.0)

        assertEquals(regular.first, stepUp.investedAmount, 0.01)
        assertEquals(regular.second, stepUp.estimatedReturns, 1.0)
        assertEquals(regular.third, stepUp.totalValue, 1.0)
        assertEquals(10_000.0, stepUp.finalMonthlySip, 0.01)
        assertEquals(16, stepUp.yearlyPoints.size)
    }

    @Test
    fun `step-up SIP exceeds regular SIP and scales final monthly SIP`() {
        val stepUp = CalculatorLogic.calculateStepUpSIP(10_000.0, 10.0, 12.0, 15)
        val regular = CalculatorLogic.calculateSIP(10_000.0, 12.0, 15.0)

        val expectedFinalMonthly = 10_000.0 * (1.10).pow(14)
        assertEquals(expectedFinalMonthly, stepUp.finalMonthlySip, 0.01)
        assertTrue(stepUp.investedAmount > regular.first)
        assertTrue(stepUp.totalValue > regular.third)
        assertEquals(stepUp.totalValue - stepUp.investedAmount, stepUp.estimatedReturns, 0.01)
    }

    @Test
    fun `step-up SIP handles zero return and invalid inputs safely`() {
        val zeroReturn = StepUpSipCalculator.calculate(1000.0, 10.0, 0.0, 2)
        // Year 1: 12*1000 = 12000, Year 2: 12*1100 = 13200 => 25200
        assertEquals(25_200.0, zeroReturn.investedAmount, 0.01)
        assertEquals(0.0, zeroReturn.estimatedReturns, 0.01)
        assertEquals(25_200.0, zeroReturn.totalValue, 0.01)

        val invalid = StepUpSipCalculator.calculate(-1000.0, 10.0, 12.0, 0)
        assertEquals(0.0, invalid.totalValue, 0.01)
        assertTrue(invalid.yearlyPoints.size == 1)
    }
}
