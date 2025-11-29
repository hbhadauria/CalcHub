package com.hsb.calchub.domain.logic.trading
object MarginCalculator {
    fun calculate(quantity: Double, price: Double, leverage: Double = 5.0): Triple<Double, Double, Double> {
        val totalValue = quantity * price
        val marginRequired = totalValue / leverage
        val exposure = totalValue
        return Triple(marginRequired, exposure, totalValue)
    }
}
