package com.hsb.calchub.domain.logic.investment

import kotlin.math.pow

/**
 * Calculator logic for Lumpsum Mutual Fund Investment.
 * Calculates the future value of a one-time investment based on an expected annual return rate.
 */
object LumpsumCalculator {
    /**
     * Calculates Lumpsum Investment Returns using the compound interest formula.
     *
     * @param totalInvestment Total amount invested as a lumpsum.
     * @param expectedReturnRate Annual expected return rate (CAGR) in percent.
     * @param timePeriodYears Time period of investment in years.
     * @return A [Triple] containing:
     *  - First: Total Invested Amount
     *  - Second: Estimated Returns (Profit)
     *  - Third: Total Value (Maturity Amount)
     */
    fun calculate(
        totalInvestment: Double,
        expectedReturnRate: Double,
        timePeriodYears: Double
    ): Triple<Double, Double, Double> {
        if (!totalInvestment.isFinite() || !expectedReturnRate.isFinite() || !timePeriodYears.isFinite() ||
            totalInvestment <= 0.0 || expectedReturnRate < 0.0 || timePeriodYears < 0.0
        ) {
            return Triple(0.0, 0.0, 0.0)
        }
        val totalValue = totalInvestment * (1.0 + expectedReturnRate / 100.0).pow(timePeriodYears)
        val estimatedReturns = totalValue - totalInvestment
        return Triple(totalInvestment, estimatedReturns, totalValue)
    }
}
