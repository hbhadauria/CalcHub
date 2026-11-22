package com.hsb.calchub.domain.logic.investment

import kotlin.math.pow

/**
 * Calculator logic for Fixed Deposit (FD).
 * FD is a financial instrument provided by banks which provides investors a higher rate of interest than a regular savings account, until the given maturity date.
 */
object FdCalculator {
    /**
     * Calculates the maturity amount and interest earned for a Fixed Deposit.
     *
     * @param totalInvestment The principal amount deposited.
     * @param rateOfInterest The annual interest rate in percent.
     * @param timePeriodYears The duration of the deposit in years.
     * @param compoundingFrequency The number of times interest is compounded per year (default is 4 for quarterly).
     * @return A [Triple] containing:
     *  - First: Total Invested Amount
     *  - Second: Estimated Returns (Interest Earned)
     *  - Third: Total Value (Maturity Amount)
     */
    fun calculate(
        totalInvestment: Double,
        rateOfInterest: Double,
        timePeriodYears: Double,
        compoundingFrequency: Int = 4
    ): Triple<Double, Double, Double> {
        if (!totalInvestment.isFinite() || !rateOfInterest.isFinite() || !timePeriodYears.isFinite() ||
            totalInvestment <= 0.0 || rateOfInterest < 0.0 || timePeriodYears < 0.0
        ) {
            return Triple(0.0, 0.0, 0.0)
        }
        val r = rateOfInterest / 100.0
        val n = compoundingFrequency.coerceAtLeast(1)
        val t = timePeriodYears

        val totalValue = totalInvestment * (1.0 + r / n).pow(n * t)
        val estimatedReturns = totalValue - totalInvestment

        return Triple(totalInvestment, estimatedReturns, totalValue)
    }
}
