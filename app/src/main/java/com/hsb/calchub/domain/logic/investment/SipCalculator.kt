package com.hsb.calchub.domain.logic.investment

import kotlin.math.pow

/**
 * Calculator logic for Systematic Investment Plan (SIP).
 * SIP is an investment route offered by Mutual Funds wherein one can invest a fixed amount in a Mutual Fund scheme at regular intervals.
 */
object SipCalculator {
    /**
     * Calculates SIP Returns.
     *
     * @param monthlyInvestment Amount invested per month.
     * @param expectedReturnRate Annual expected return rate (in percentage).
     * @param timePeriodYears Time period of investment in years.
     * @return A [Triple] containing:
     *  - First: Total Invested Amount
     *  - Second: Estimated Returns (Profit)
     *  - Third: Total Value (Maturity Amount)
     */
    fun calculate(
        monthlyInvestment: Double,
        expectedReturnRate: Double,
        timePeriodYears: Double
    ): Triple<Double, Double, Double> {
        if (!monthlyInvestment.isFinite() || !expectedReturnRate.isFinite() || !timePeriodYears.isFinite() ||
            monthlyInvestment <= 0.0 || timePeriodYears <= 0.0 || expectedReturnRate < 0.0
        ) {
            return Triple(0.0, 0.0, 0.0)
        }
        val n = timePeriodYears * 12.0
        val investedAmount = monthlyInvestment * n
        val totalValue = if (expectedReturnRate == 0.0) {
            investedAmount
        } else {
            val i = expectedReturnRate / 12.0 / 100.0
            monthlyInvestment * ((1.0 + i).pow(n) - 1.0) / i * (1.0 + i)
        }
        val estimatedReturns = totalValue - investedAmount
        return Triple(investedAmount, estimatedReturns, totalValue)
    }
}
