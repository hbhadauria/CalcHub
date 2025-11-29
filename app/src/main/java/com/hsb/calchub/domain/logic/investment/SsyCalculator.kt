package com.hsb.calchub.domain.logic.investment

import kotlin.math.pow

object SsyCalculator {
    fun calculate(
        yearlyDeposit: Double,
        interestRate: Double = 8.2,
        timePeriodYears: Double = 15.0
    ): Triple<Double, Double, Double> {
        val i = interestRate / 100
        val n = timePeriodYears
        
        val totalValue = yearlyDeposit * ((1 + i).pow(n) - 1) / i * (1 + i)
        val investedAmount = yearlyDeposit * n
        val estimatedReturns = totalValue - investedAmount
        
        return Triple(investedAmount, estimatedReturns, totalValue)
    }
}
