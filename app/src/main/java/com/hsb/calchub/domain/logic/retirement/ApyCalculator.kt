package com.hsb.calchub.domain.logic.retirement
object ApyCalculator {
    fun calculate(currentAge: Double, pensionAmount: Double = 5000.0): Triple<Double, Double, Double> {
        val yearsToRetirement = 60 - currentAge
        val monthlyContribution = when {
            pensionAmount == 1000.0 -> when { currentAge <= 20 -> 42.0; currentAge <= 25 -> 76.0; currentAge <= 30 -> 116.0; currentAge <= 35 -> 181.0; else -> 291.0 }
            pensionAmount == 5000.0 -> when { currentAge <= 20 -> 210.0; currentAge <= 25 -> 380.0; currentAge <= 30 -> 577.0; currentAge <= 35 -> 902.0; else -> 1454.0 }
            else -> 210.0
        }
        val totalContribution = monthlyContribution * 12 * yearsToRetirement
        return Triple(monthlyContribution, totalContribution, pensionAmount)
    }
}
