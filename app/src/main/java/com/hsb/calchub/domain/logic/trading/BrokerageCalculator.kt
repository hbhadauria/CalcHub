package com.hsb.calchub.domain.logic.trading
object BrokerageCalculator {
    fun calculate(tradeValue: Double, brokerageRate: Double = 0.03, isIntraday: Boolean = false): Triple<Double, Double, Double> {
        val brokerage = tradeValue * (brokerageRate / 100)
        val stt = if (isIntraday) tradeValue * 0.00025 else tradeValue * 0.001
        val totalCharges = brokerage + stt
        return Triple(tradeValue, totalCharges, tradeValue - totalCharges)
    }
}
