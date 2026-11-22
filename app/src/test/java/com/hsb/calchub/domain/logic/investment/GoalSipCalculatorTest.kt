package com.hsb.calchub.domain.logic.investment

import com.hsb.calchub.domain.logic.CalculatorLogic
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GoalSipCalculatorTest {

    @Test
    fun `goal SIP monthly amount compounds back to target corpus`() {
        val goal = 10_000_000.0
        val res = CalculatorLogic.calculateGoalSIP(goal, 12.0, 15.0)

        assertTrue(res.requiredMonthlySip > 0.0)
        val achieved = CalculatorLogic.calculateSIP(res.requiredMonthlySip, 12.0, 15.0).third
        assertEquals(goal, achieved, 1.0)
    }

    @Test
    fun `goal SIP scales target with inflation and handles zero return`() {
        val withInflation = GoalSipCalculator.calculate(1_000_000.0, 12.0, 10.0, inflationRate = 6.0)
        val flat = GoalSipCalculator.calculate(1_000_000.0, 12.0, 10.0, inflationRate = 0.0)
        assertTrue(withInflation.inflationAdjustedTarget > flat.inflationAdjustedTarget)
        assertTrue(withInflation.requiredMonthlySip > flat.requiredMonthlySip)

        val zeroRate = GoalSipCalculator.calculate(120_000.0, 0.0, 1.0)
        assertEquals(10_000.0, zeroRate.requiredMonthlySip, 0.01)
        assertEquals(0.0, zeroRate.wealthGain, 0.01)
    }

    @Test
    fun `goal SIP guards zero negative and NaN inputs`() {
        listOf(
            GoalSipCalculator.calculate(0.0, 12.0, 10.0),
            GoalSipCalculator.calculate(-100_000.0, 12.0, 10.0),
            GoalSipCalculator.calculate(100_000.0, -5.0, 10.0),
            GoalSipCalculator.calculate(100_000.0, 12.0, 0.0),
            GoalSipCalculator.calculate(Double.NaN, 12.0, 10.0)
        ).forEach {
            assertTrue(it.requiredMonthlySip.isFinite())
            assertEquals(0.0, it.requiredMonthlySip, 0.01)
        }
    }
}
