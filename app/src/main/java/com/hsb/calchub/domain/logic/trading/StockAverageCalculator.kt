package com.hsb.calchub.domain.logic.trading
object StockAverageCalculator {
    fun calculate(firstQuantity: Double, firstPrice: Double, secondQuantity: Double, secondPrice: Double): Triple<Double, Double, Double> {
        val totalQuantity = firstQuantity + secondQuantity
        val totalInvestment = (firstQuantity * firstPrice) + (secondQuantity * secondPrice)
        val averagePrice = totalInvestment / totalQuantity
        return Triple(totalQuantity, averagePrice, totalInvestment)
    }
}
