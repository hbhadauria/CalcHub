package com.hsb.calchub.domain.logic.retirement

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RetirementCalculatorTest {

    @Test
    fun `calculate Retirement returns correctly`() {
        val monthlyExpenses = 50000.0
        val result = RetirementCalculator.calculate(30.0, 60.0, monthlyExpenses, 6.0, 12.0)

        assertTrue(result.third > monthlyExpenses * 12)
        assertTrue(result.second > result.third)
        assertTrue(result.first > 0)
        assertEquals(result.third * 25.0, result.second, 0.01)
    }

    @Test
    fun `calculate Retirement handles zero return and invalid inputs`() {
        val zeroReturn = RetirementCalculator.calculate(50.0, 60.0, 10000.0, 0.0, 0.0)
        // 10k * 12 * 25 = 3,000,000 corpus needed over 120 months = 25,000/mo
        assertEquals(120000.0, zeroReturn.third, 0.01)
        assertEquals(3000000.0, zeroReturn.second, 0.01)
        assertEquals(25000.0, zeroReturn.first, 0.01)

        assertEquals(0.0, RetirementCalculator.calculate(60.0, 60.0, 50000.0).second, 0.01)
        assertEquals(0.0, RetirementCalculator.calculate(30.0, 55.0, -5000.0).second, 0.01)
    }
}
