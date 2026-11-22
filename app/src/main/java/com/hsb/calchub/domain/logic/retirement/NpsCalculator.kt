package com.hsb.calchub.domain.logic.retirement

import kotlin.math.pow

/**
 * Calculator logic for National Pension System (NPS).
 * NPS is a voluntary, long-term retirement savings scheme designed to enable systematic savings.
 */
object NpsCalculator {
    /**
     * Calculates the estimated corpus and returns for NPS.
     *
     * @param monthlyContribution The monthly contribution amount.
     * @param expectedReturnRate The annual expected return rate in percent.
     * @param currentAge The current age of the subscriber.
     * @param retirementAge The age of retirement (default 60).
     * @return A [Triple] containing:
     *  - First: Total Invested Amount
     *  - Second: Estimated Returns
     *  - Third: Total Maturity Value
     */
    fun calculate(
        monthlyContribution: Double,
        expectedReturnRate: Double,
        currentAge: Double,
        retirementAge: Double = 60.0
    ): Triple<Double, Double, Double> {
        if (!monthlyContribution.isFinite() || !expectedReturnRate.isFinite() ||
            !currentAge.isFinite() || !retirementAge.isFinite() ||
            monthlyContribution <= 0.0 || retirementAge <= currentAge || expectedReturnRate < 0.0
        ) {
            return Triple(0.0, 0.0, 0.0)
        }
        val timePeriodYears = retirementAge - currentAge
        val n = timePeriodYears * 12.0
        val investedAmount = monthlyContribution * n
        val totalValue = if (expectedReturnRate == 0.0) {
            investedAmount
        } else {
            val i = expectedReturnRate / 12.0 / 100.0
            monthlyContribution * ((1.0 + i).pow(n) - 1.0) / i * (1.0 + i)
        }
        val estimatedReturns = totalValue - investedAmount
        return Triple(investedAmount, estimatedReturns, totalValue)
    }
}
