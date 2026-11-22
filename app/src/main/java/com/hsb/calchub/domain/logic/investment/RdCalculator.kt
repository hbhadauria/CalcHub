package com.hsb.calchub.domain.logic.investment

import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * Calculator logic for Recurring Deposit (RD).
 * Uses standard Indian bank quarterly compounding on each monthly deposit installment.
 */
object RdCalculator {
    /**
     * Calculates the maturity amount and interest earned for a Recurring Deposit.
     *
     * @param monthlyDeposit The amount deposited monthly.
     * @param interestRate The annual interest rate in percent.
     * @param timePeriodYears The duration of the deposit in years.
     * @return A [Triple] containing:
     *  - First: Total Invested Amount
     *  - Second: Estimated Returns (Interest Earned)
     *  - Third: Total Value (Maturity Amount)
     */
    fun calculate(
        monthlyDeposit: Double,
        interestRate: Double,
        timePeriodYears: Double
    ): Triple<Double, Double, Double> {
        if (!monthlyDeposit.isFinite() || !interestRate.isFinite() || !timePeriodYears.isFinite() ||
            monthlyDeposit <= 0.0 || timePeriodYears <= 0.0 || interestRate < 0.0
        ) {
            return Triple(0.0, 0.0, 0.0)
        }
        val months = (timePeriodYears * 12.0).roundToInt().coerceAtLeast(0)
        if (months == 0) {
            return Triple(0.0, 0.0, 0.0)
        }
        val investedAmount = monthlyDeposit * months
        val totalValue = if (interestRate == 0.0) {
            investedAmount
        } else {
            val r = interestRate / 100.0
            var maturity = 0.0
            for (k in 1..months) {
                maturity += monthlyDeposit * (1.0 + r / 4.0).pow(4.0 * k / 12.0)
            }
            maturity
        }
        val estimatedReturns = totalValue - investedAmount
        return Triple(investedAmount, estimatedReturns, totalValue)
    }
}
