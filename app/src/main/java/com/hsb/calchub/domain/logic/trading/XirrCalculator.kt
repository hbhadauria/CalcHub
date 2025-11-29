package com.hsb.calchub.domain.logic.trading
object XirrCalculator {
    fun calculate(investments: List<Double>, returns: Double): Triple<Double, Double, Double> {
        val totalInvested = investments.sum()
        val profit = returns - totalInvested
        val returnPercent = (profit / totalInvested) * 100
        return Triple(totalInvested, profit, returnPercent)
    }
}
