package com.hsb.calchub.domain.logic.other
object ScssCalculator {
    fun calculate(investment: Double, interestRate: Double = 8.2): Triple<Double, Double, Double> {
        val tenure = 5.0
        val quarterlyInterest = (investment * interestRate / 100) / 4
        val yearlyInterest = quarterlyInterest * 4
        val totalInterest = yearlyInterest * tenure
        return Triple(investment, totalInterest, investment + totalInterest)
    }
}
