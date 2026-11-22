package com.hsb.calchub.domain.logic.investment

import kotlin.math.pow

/**
 * Calculator logic for Sukanya Samriddhi Yojana (SSY).
 * SSY is a government-backed savings scheme for the girl child where deposits run up to 15 years
 * and the corpus continues compounding through maturity (up to 21 years).
 */
object SsyCalculator {
    private const val MAX_DEPOSIT_YEARS = 15.0

    /**
     * Calculates the maturity amount and interest earned for SSY.
     *
     * @param yearlyDeposit The amount deposited yearly (for up to 15 years).
     * @param interestRate The annual interest rate in percent (default is 8.2%).
     * @param timePeriodYears The total duration in years (deposits capped at 15 years; remaining years compound).
     * @return A [Triple] containing:
     *  - First: Total Invested Amount
     *  - Second: Estimated Returns (Interest Earned)
     *  - Third: Total Value (Maturity Amount)
     */
    fun calculate(
        yearlyDeposit: Double,
        interestRate: Double = 8.2,
        timePeriodYears: Double = 15.0
    ): Triple<Double, Double, Double> {
        if (!yearlyDeposit.isFinite() || !interestRate.isFinite() || !timePeriodYears.isFinite() ||
            yearlyDeposit <= 0.0 || timePeriodYears <= 0.0 || interestRate < 0.0
        ) {
            return Triple(0.0, 0.0, 0.0)
        }

        val depositYears = minOf(timePeriodYears, MAX_DEPOSIT_YEARS)
        val extraCompoundingYears = (timePeriodYears - depositYears).coerceAtLeast(0.0)
        val investedAmount = yearlyDeposit * depositYears

        val totalValue = if (interestRate == 0.0) {
            investedAmount
        } else {
            val i = interestRate / 100.0
            val valueAfterDeposits = yearlyDeposit * ((1.0 + i).pow(depositYears) - 1.0) / i * (1.0 + i)
            valueAfterDeposits * (1.0 + i).pow(extraCompoundingYears)
        }
        val estimatedReturns = totalValue - investedAmount

        return Triple(investedAmount, estimatedReturns, totalValue)
    }
}
