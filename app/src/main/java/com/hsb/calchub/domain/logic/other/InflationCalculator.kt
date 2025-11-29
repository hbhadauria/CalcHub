package com.hsb.calchub.domain.logic.other
import kotlin.math.pow
object InflationCalculator {
    fun calculate(currentPrice: Double, inflationRate: Double, years: Double): Triple<Double, Double, Double> {
        val futurePrice = currentPrice * (1 + inflationRate / 100).pow(years)
        val increase = futurePrice - currentPrice
        return Triple(currentPrice, increase, futurePrice)
    }
}
