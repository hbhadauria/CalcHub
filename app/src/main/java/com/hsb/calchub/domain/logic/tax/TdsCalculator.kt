package com.hsb.calchub.domain.logic.tax
object TdsCalculator {
    fun calculate(amount: Double, tdsRate: Double = 10.0): Triple<Double, Double, Double> {
        val tdsAmount = amount * (tdsRate / 100)
        val netAmount = amount - tdsAmount
        return Triple(amount, tdsAmount, netAmount)
    }
}
