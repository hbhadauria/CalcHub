package com.hsb.calchub.domain.logic.loan
import kotlin.math.pow
object FlatVsReducingCalculator {
    fun calculate(loanAmount: Double, flatRate: Double, tenureYears: Double): Triple<Double, Double, Double> {
        val totalInterestFlat = loanAmount * (flatRate / 100) * tenureYears
        val totalAmountFlat = loanAmount + totalInterestFlat
        val emiFlat = totalAmountFlat / (tenureYears * 12)
        val reducingRate = flatRate * 1.8
        val r = reducingRate / 12 / 100
        val n = tenureYears * 12
        val emiReducing = (loanAmount * r * (1 + r).pow(n)) / ((1 + r).pow(n) - 1)
        return Triple(emiFlat, emiReducing, emiFlat - emiReducing)
    }
}
