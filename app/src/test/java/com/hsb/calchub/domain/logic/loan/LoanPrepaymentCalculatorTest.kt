package com.hsb.calchub.domain.logic.loan

import com.hsb.calchub.domain.logic.CalculatorLogic
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LoanPrepaymentCalculatorTest {

    @Test
    fun `extra monthly and lump-sum prepayments reduce interest and tenure`() {
        val noPrepay = CalculatorLogic.calculateLoanPrepayment(3_000_000.0, 8.5, 20.0, 0.0, 0.0)
        assertEquals(0, noPrepay.monthsSaved)
        assertEquals(0.0, noPrepay.interestSaved, 1.0)

        val withPrepay = CalculatorLogic.calculateLoanPrepayment(3_000_000.0, 8.5, 20.0, 5_000.0, 200_000.0, 12)
        assertTrue(withPrepay.monthsSaved > 0)
        assertTrue(withPrepay.interestSaved > 0.0)
        assertTrue(withPrepay.newTotalInterest < withPrepay.baseTotalInterest)
        assertEquals(withPrepay.baseMonths - withPrepay.newMonths, withPrepay.monthsSaved)
    }

    @Test
    fun `loan prepayment guards invalid inputs`() {
        val invalid = LoanPrepaymentCalculator.calculate(-100_000.0, 8.5, 10.0, 1000.0, 0.0)
        assertEquals(0.0, invalid.baseEmi, 0.01)
        assertEquals(0, invalid.baseMonths)
    }
}
