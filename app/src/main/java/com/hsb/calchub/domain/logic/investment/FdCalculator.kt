package com.hsb.calchub.domain.logic.investment

import kotlin.math.pow

object FdCalculator {
    fun calculate(
        totalInvestment: Double,
        rateOfInterest: Double,
        timePeriodYears: Double,
        compoundingFrequency: Int = 4
    ): Triple<Double, Double, Double> {
        val r = rateOfInterest / 100
        val n = compoundingFrequency
        val t = timePeriodYears
        
        val totalValue = totalInvestment * (1 + r / n).pow(n * t)
        val estimatedReturns = totalValue - totalInvestment
        
        return Triple(totalInvestment, estimatedReturns, totalValue)
    }
}
