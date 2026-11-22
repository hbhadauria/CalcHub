package com.hsb.calchub.domain.logic.loan

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LoanScheduleCalculatorTest {

    @Test
    fun `computeAmortizationSummary sums principal to loan amount`() {
        val principal = 1_000_000.0
        val rate = 9.0
        val years = 10
        val (emi, totalInterest, _) = EmiCalculator.calculate(principal, rate, years.toDouble())

        val summary = LoanScheduleCalculator.computeAmortizationSummary(principal, rate, years, emi)

        assertEquals(years, summary.yearlyBreakdown.size)
        val totalPrincipalPaid = summary.yearlyBreakdown.sumOf { it.first }
        val totalInterestPaid = summary.yearlyBreakdown.sumOf { it.second }
        assertEquals(principal, totalPrincipalPaid, 5.0)
        assertEquals(totalInterest, totalInterestPaid, 5.0)
        assertTrue(summary.crossoverYear in 1..years)
    }

    @Test
    fun `simulateHomeLoanSchedule extra EMI reduces months and total interest`() {
        val principal = 5_000_000.0
        val rate = 8.5
        val years = 20
        val (emi, _, _) = EmiCalculator.calculate(principal, rate, years.toDouble())

        val standard = LoanScheduleCalculator.simulateHomeLoanSchedule(principal, rate, years, emi, extraEmiEachYear = false)
        val extraEmi = LoanScheduleCalculator.simulateHomeLoanSchedule(principal, rate, years, emi, extraEmiEachYear = true)

        assertEquals(years * 12, standard.totalMonths)
        assertTrue(extraEmi.totalMonths < standard.totalMonths)
        assertTrue(extraEmi.totalInterest < standard.totalInterest)
    }

    @Test
    fun `schedule helpers guard against invalid inputs`() {
        val emptySummary = LoanScheduleCalculator.computeAmortizationSummary(0.0, 9.0, 10, 10000.0)
        assertTrue(emptySummary.yearlyBreakdown.isEmpty())

        val emptySchedule = LoanScheduleCalculator.simulateHomeLoanSchedule(-1000.0, 8.5, 20, 10000.0, true)
        assertTrue(emptySchedule.yearlyBreakdown.isEmpty())
        assertEquals(0, emptySchedule.totalMonths)
    }
}
