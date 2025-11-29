package com.hsb.calchub.domain.logic.investment

import kotlin.math.pow

object LumpsumCalculator {
    /**
     * Calculate Lumpsum Investment Returns
     * @param totalInvestment: Total amount invested
     * @param expectedReturnRate: Annual expected return rate (in %)
     * @param timePeriodYears: Time period in years
     * @return Triple(Invested Amount, Estimated Returns, Total Value)
     */
    fun calculate(
        totalInvestment: Double,
        expectedReturnRate: Double,
        timePeriodYears: Double
    ): Triple<Double, Double, Double> {
        val totalValue = totalInvestment * (1 + expectedReturnRate / 100).pow(timePeriodYears)
        val estimatedReturns = totalValue - totalInvestment
        
        return Triple(totalInvestment, estimatedReturns, totalValue)
    }
}
