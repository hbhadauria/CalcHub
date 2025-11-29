package com.hsb.calchub.domain.logic.retirement
import kotlin.math.pow
object RetirementCalculator {
    fun calculate(currentAge: Double, retirementAge: Double, monthlyExpenses: Double, inflationRate: Double = 6.0, expectedReturn: Double = 12.0): Triple<Double, Double, Double> {
        val yearsToRetirement = retirementAge - currentAge
        val yearsInRetirement = 25.0
        val futureExpenses = monthlyExpenses * (1 + inflationRate / 100).pow(yearsToRetirement)
        val corpusNeeded = futureExpenses * 12 * yearsInRetirement
        val i = expectedReturn / 12 / 100
        val n = yearsToRetirement * 12
        val monthlySIP = corpusNeeded / (((1 + i).pow(n) - 1) / i * (1 + i))
        return Triple(monthlySIP, corpusNeeded, futureExpenses * 12)
    }
}
