package com.hsb.calchub.domain.logic.other
object SimpleInterestCalculator {
    fun calculate(principal: Double, rate: Double, timeYears: Double): Triple<Double, Double, Double> {
        val interest = (principal * rate * timeYears) / 100
        val totalAmount = principal + interest
        return Triple(principal, interest, totalAmount)
    }
}
