package com.hsb.calchub.domain.logic.investment

import kotlin.math.pow

/**
 * Calculator logic for Public Provident Fund (PPF).
 * PPF is a long-term investment scheme popular among individuals who want to earn high but stable returns.
 */
object PpfCalculator {
    /**
     * Calculates the maturity amount and interest earned for PPF.
     *
     * @param yearlyInvestment The amount invested yearly.
     * @param interestRate The annual interest rate in percent.
     * @param timePeriodYears The duration of the investment in years (default is 15 years).
     * @return A [Triple] containing:
     *  - First: Total Invested Amount
     *  - Second: Estimated Returns (Interest Earned)
     *  - Third: Total Value (Maturity Amount)
     */
    fun calculate(
        yearlyInvestment: Double,
        interestRate: Double,
        timePeriodYears: Double = 15.0
    ): Triple<Double, Double, Double> {
        if (!yearlyInvestment.isFinite() || !interestRate.isFinite() || !timePeriodYears.isFinite() ||
            yearlyInvestment <= 0.0 || timePeriodYears <= 0.0 || interestRate < 0.0
        ) {
            return Triple(0.0, 0.0, 0.0)
        }
        val n = timePeriodYears
        val investedAmount = yearlyInvestment * n
        val totalValue = if (interestRate == 0.0) {
            investedAmount
        } else {
            val i = interestRate / 100.0
            yearlyInvestment * ((1.0 + i).pow(n) - 1.0) / i * (1.0 + i)
        }
        val estimatedReturns = totalValue - investedAmount
        return Triple(investedAmount, estimatedReturns, totalValue)
    }
}
