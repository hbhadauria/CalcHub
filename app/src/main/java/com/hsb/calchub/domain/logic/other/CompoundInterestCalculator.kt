package com.hsb.calchub.domain.logic.other
import kotlin.math.pow
object CompoundInterestCalculator {
    fun calculate(principal: Double, rate: Double, timeYears: Double, compoundingFrequency: Int = 1): Triple<Double, Double, Double> {
        val r = rate / 100
        val n = compoundingFrequency
        val t = timeYears
        val totalValue = principal * (1 + r / n).pow(n * t)
        val interest = totalValue - principal
        return Triple(principal, interest, totalValue)
    }
}
