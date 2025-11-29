package com.hsb.calchub.domain.logic.investment

object RdCalculator {
    fun calculate(
        monthlyDeposit: Double,
        interestRate: Double,
        timePeriodYears: Double
    ): Triple<Double, Double, Double> {
        val n = timePeriodYears * 12
        val totalValue = monthlyDeposit * n + (monthlyDeposit * n * (n + 1) / 24) * (interestRate / 100)
        val investedAmount = monthlyDeposit * n
        val estimatedReturns = totalValue - investedAmount
        
        return Triple(investedAmount, estimatedReturns, totalValue)
    }
}
