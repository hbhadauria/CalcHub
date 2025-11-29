package com.hsb.calchub.domain.logic.tax
object GstCalculator {
    fun calculate(amount: Double, gstRate: Double = 18.0, isInclusive: Boolean = false): Triple<Double, Double, Double> {
        val gstAmount = if (isInclusive) amount - (amount / (1 + gstRate / 100)) else amount * (gstRate / 100)
        val totalAmount = if (isInclusive) amount else amount + gstAmount
        return Triple(amount, gstAmount, totalAmount)
    }
}
