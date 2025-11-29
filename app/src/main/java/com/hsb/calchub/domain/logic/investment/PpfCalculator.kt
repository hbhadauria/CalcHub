package com.hsb.calchub.domain.logic.investment

import kotlin.math.pow

object PpfCalculator {
    fun calculate(
        yearlyInvestment: Double,
        interestRate: Double,
        timePeriodYears: Double = 15.0
    ): Triple<Double, Double, Double> {
        val i = interestRate / 100
        val n = timePeriodYears
        
        val totalValue = yearlyInvestment * ((1 + i).pow(n) - 1) / i * (1 + i)
        val investedAmount = yearlyInvestment * n
        val estimatedReturns = totalValue - investedAmount
        
        return Triple(investedAmount, estimatedReturns, totalValue)
    }
}
