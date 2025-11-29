package com.hsb.calchub.domain.logic.other
object PostOfficeMisCalculator {
    fun calculate(investment: Double, interestRate: Double = 7.4): Triple<Double, Double, Double> {
        val monthlyIncome = (investment * interestRate / 100) / 12
        val yearlyIncome = monthlyIncome * 12
        val totalIncome5Years = yearlyIncome * 5
        return Triple(investment, monthlyIncome, totalIncome5Years)
    }
}
