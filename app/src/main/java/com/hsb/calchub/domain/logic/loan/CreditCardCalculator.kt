package com.hsb.calchub.domain.logic.loan

/**
 * Comparison between paying a fixed monthly amount vs. only the minimum due on a credit card balance.
 */
data class CreditCardPayoffResult(
    val fixedMonths: Int,
    val fixedTotalInterest: Double,
    val fixedTotalPaid: Double,
    val minDueMonths: Int,
    val minDueTotalInterest: Double,
    val minDueTotalPaid: Double,
    val interestSaved: Double,
    val willPayOffFixed: Boolean
)

/**
 * Calculator logic for Credit Card EMI / Minimum Due payoff analysis.
 */
object CreditCardCalculator {
    private const val MAX_SIMULATION_MONTHS = 600
    private const val MIN_DUE_FLOOR_INR = 200.0

    /**
     * Compares paying [monthlyPayment] against paying only [minDuePercent] (min ₹200) of the outstanding balance.
     */
    fun calculate(
        balance: Double,
        annualInterestRate: Double,
        monthlyPayment: Double,
        minDuePercent: Double = 5.0
    ): CreditCardPayoffResult {
        if (!balance.isFinite() || !annualInterestRate.isFinite() ||
            !monthlyPayment.isFinite() || !minDuePercent.isFinite() ||
            balance <= 0.0 || annualInterestRate < 0.0 ||
            monthlyPayment <= 0.0 || minDuePercent <= 0.0
        ) {
            return CreditCardPayoffResult(0, 0.0, 0.0, 0, 0.0, 0.0, 0.0, willPayOffFixed = false)
        }

        val monthlyRate = annualInterestRate / 12.0 / 100.0
        val firstMonthInterest = balance * monthlyRate
        val willPayOffFixed = monthlyPayment > firstMonthInterest

        var fixedBal = balance
        var fixedMonths = 0
        var fixedInterest = 0.0
        var fixedPaid = 0.0
        if (willPayOffFixed) {
            for (m in 1..MAX_SIMULATION_MONTHS) {
                if (fixedBal <= 0.01) break
                fixedMonths = m
                val intr = fixedBal * monthlyRate
                val payment = minOf(monthlyPayment, fixedBal + intr)
                fixedBal = (fixedBal + intr - payment).coerceAtLeast(0.0)
                fixedInterest += intr
                fixedPaid += payment
            }
        } else {
            fixedMonths = MAX_SIMULATION_MONTHS
            fixedInterest = firstMonthInterest * MAX_SIMULATION_MONTHS
            fixedPaid = monthlyPayment * MAX_SIMULATION_MONTHS
        }

        var minBal = balance
        var minMonths = 0
        var minInterest = 0.0
        var minPaid = 0.0
        val minFrac = minDuePercent / 100.0
        for (m in 1..MAX_SIMULATION_MONTHS) {
            if (minBal <= 0.01) break
            minMonths = m
            val intr = minBal * monthlyRate
            val due = maxOf((minBal + intr) * minFrac, MIN_DUE_FLOOR_INR)
            val payment = minOf(due, minBal + intr)
            minBal = (minBal + intr - payment).coerceAtLeast(0.0)
            minInterest += intr
            minPaid += payment
        }

        val interestSaved = if (willPayOffFixed) (minInterest - fixedInterest).coerceAtLeast(0.0) else 0.0

        return CreditCardPayoffResult(
            fixedMonths = fixedMonths,
            fixedTotalInterest = fixedInterest,
            fixedTotalPaid = fixedPaid,
            minDueMonths = minMonths,
            minDueTotalInterest = minInterest,
            minDueTotalPaid = minPaid,
            interestSaved = interestSaved,
            willPayOffFixed = willPayOffFixed
        )
    }
}
