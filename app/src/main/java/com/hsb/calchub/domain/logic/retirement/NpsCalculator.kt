package com.hsb.calchub.domain.logic.retirement
import kotlin.math.pow
object NpsCalculator {
    fun calculate(monthlyContribution: Double, expectedReturnRate: Double, currentAge: Double, retirementAge: Double = 60.0): Triple<Double, Double, Double> {
        val timePeriodYears = retirementAge - currentAge
        val i = expectedReturnRate / 12 / 100
        val n = timePeriodYears * 12
        val totalValue = monthlyContribution * ((1 + i).pow(n) - 1) / i * (1 + i)
        val investedAmount = monthlyContribution * n
        val estimatedReturns = totalValue - investedAmount
        return Triple(investedAmount, estimatedReturns, totalValue)
    }
}
