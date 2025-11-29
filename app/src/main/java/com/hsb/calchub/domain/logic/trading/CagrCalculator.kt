package com.hsb.calchub.domain.logic.trading
import kotlin.math.pow
object CagrCalculator {
    fun calculate(initialValue: Double, finalValue: Double, years: Double): Triple<Double, Double, Double> {
        val cagr = ((finalValue / initialValue).pow(1 / years) - 1) * 100
        val totalReturn = ((finalValue - initialValue) / initialValue) * 100
        return Triple(cagr, totalReturn, finalValue - initialValue)
    }
}
