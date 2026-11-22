package com.hsb.calchub.domain.logic.investment

import kotlin.math.roundToInt

/**
 * Calculator logic for Employee Provident Fund (EPF).
 * Models monthly EPF contributions with monthly interest accrual credited at the end of each 12-month year.
 */
object EpfCalculator {
    /**
     * Calculates the estimated returns and total value for EPF investment.
     *
     * @param monthlyContribution The amount contributed monthly by the employee/employer.
     * @param interestRate The annual interest rate in percent (default is 8.25%).
     * @param timePeriodYears The duration of the investment in years.
     * @return A [Triple] containing:
     *  - First: Total Invested Amount
     *  - Second: Estimated Returns (Interest Earned)
     *  - Third: Total Value (Maturity Amount)
     */
    fun calculate(
        monthlyContribution: Double,
        interestRate: Double = 8.25,
        timePeriodYears: Double
    ): Triple<Double, Double, Double> {
        if (!monthlyContribution.isFinite() || !interestRate.isFinite() || !timePeriodYears.isFinite() ||
            monthlyContribution <= 0.0 || timePeriodYears <= 0.0 || interestRate < 0.0
        ) {
            return Triple(0.0, 0.0, 0.0)
        }

        val totalMonths = (timePeriodYears * 12.0).roundToInt().coerceAtLeast(0)
        if (totalMonths == 0) {
            return Triple(0.0, 0.0, 0.0)
        }
        val investedAmount = monthlyContribution * totalMonths
        if (interestRate == 0.0) {
            return Triple(investedAmount, 0.0, investedAmount)
        }

        val monthlyRate = (interestRate / 100.0) / 12.0
        var balance = 0.0
        var accruedYearInterest = 0.0

        for (m in 1..totalMonths) {
            balance += monthlyContribution
            accruedYearInterest += balance * monthlyRate
            if (m % 12 == 0) {
                balance += accruedYearInterest
                accruedYearInterest = 0.0
            }
        }
        balance += accruedYearInterest

        val estimatedReturns = balance - investedAmount
        return Triple(investedAmount, estimatedReturns, balance)
    }
}
