package com.hsb.calchub.domain.logic.investment

import kotlin.math.pow

object NscCalculator {
    fun calculate(
        investment: Double,
        interestRate: Double = 7.7
    ): Triple<Double, Double, Double> {
        val tenure = 5.0
        val totalValue = investment * (1 + interestRate / 100).pow(tenure)
        val interest = totalValue - investment
        return Triple(investment, interest, totalValue)
    }
}
