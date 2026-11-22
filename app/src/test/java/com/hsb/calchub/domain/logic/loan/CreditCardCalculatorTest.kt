package com.hsb.calchub.domain.logic.loan

import com.hsb.calchub.domain.logic.CalculatorLogic
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CreditCardCalculatorTest {

    @Test
    fun `fixed payment clears balance faster and saves interest vs 5 percent minimum due`() {
        val res = CalculatorLogic.calculateCreditCardPayoff(100_000.0, 36.0, 10_000.0)
        assertTrue(res.willPayOffFixed)
        assertTrue(res.fixedMonths < res.minDueMonths)
        assertTrue(res.fixedTotalInterest < res.minDueTotalInterest)
        assertTrue(res.interestSaved > 0.0)
    }

    @Test
    fun `insufficient fixed payment flags inability to pay off`() {
        // 100,000 @ 36% APR => 3,000/mo interest; paying 2,500/mo never reduces principal
        val res = CreditCardCalculator.calculate(100_000.0, 36.0, 2_500.0)
        assertFalse(res.willPayOffFixed)
        assertEquals(0.0, res.interestSaved, 0.01)
    }

    @Test
    fun `credit card calculator guards zero negative and NaN inputs`() {
        val invalid = CreditCardCalculator.calculate(0.0, 36.0, 5000.0)
        assertFalse(invalid.willPayOffFixed)
        assertEquals(0, invalid.fixedMonths)
        assertEquals(0.0, CreditCardCalculator.calculate(-1000.0, 36.0, 500.0).fixedTotalPaid, 0.01)
    }
}
