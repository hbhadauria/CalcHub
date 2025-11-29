package com.hsb.calchub.domain.logic.investment

import kotlin.math.pow

object EpfCalculator {
    fun calculate(
        monthlyContribution: Double,
        interestRate: Double = 8.25,
        timePeriodYears: Double
    ): Triple<Double, Double, Double> {
        val i = interestRate / 100
        val n = timePeriodYears
        val yearlyContribution = monthlyContribution * 12
        
        val totalValue = yearlyContribution * ((1 + i).pow(n) - 1) / i * (1 + i)
        val investedAmount = yearlyContribution * n
        val estimatedReturns = totalValue - investedAmount
        
        return Triple(investedAmount, estimatedReturns, totalValue)
    }
}
