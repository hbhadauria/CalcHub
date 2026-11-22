package com.hsb.calchub.domain.logic.investment

import kotlin.math.roundToInt

/**
 * Calculator logic for Systematic Withdrawal Plan (SWP).
 * SWP allows an investor to withdraw a fixed amount from their mutual fund scheme on a monthly basis.
 */
object SwpCalculator {
    /**
     * Calculates the remaining balance and actual total withdrawn amount for SWP.
     * Stops withdrawals if and when the corpus depletes to zero.
     *
     * @param totalInvestment The initial lumpsum amount invested.
     * @param withdrawalPerMonth The target amount withdrawn monthly.
     * @param expectedReturnRate The expected annual return rate on the remaining balance.
     * @param timePeriodYears The duration of the withdrawal plan in years.
     * @return A [Triple] containing:
     *  - First: Total Investment (Initial Value)
     *  - Second: Actual Total Withdrawn Amount
     *  - Third: Final Balance Amount
     */
    fun calculate(
        totalInvestment: Double,
        withdrawalPerMonth: Double,
        expectedReturnRate: Double,
        timePeriodYears: Double
    ): Triple<Double, Double, Double> {
        val safeInvestment = if (totalInvestment.isFinite()) totalInvestment.coerceAtLeast(0.0) else 0.0
        if (safeInvestment <= 0.0 || !withdrawalPerMonth.isFinite() || !expectedReturnRate.isFinite() ||
            !timePeriodYears.isFinite() || timePeriodYears <= 0.0 || withdrawalPerMonth < 0.0 ||
            expectedReturnRate < 0.0
        ) {
            return Triple(safeInvestment, 0.0, safeInvestment)
        }

        val r = expectedReturnRate / 12.0 / 100.0
        val months = (timePeriodYears * 12.0).roundToInt().coerceAtLeast(0)

        var balance = safeInvestment
        var totalWithdrawn = 0.0

        for (month in 1..months) {
            if (balance <= 0.0) break
            balance *= (1.0 + r)
            val actualWithdrawal = minOf(withdrawalPerMonth, balance)
            balance -= actualWithdrawal
            totalWithdrawn += actualWithdrawal
        }

        return Triple(safeInvestment, totalWithdrawn, balance.coerceAtLeast(0.0))
    }
}
