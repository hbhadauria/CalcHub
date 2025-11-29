package com.hsb.calchub.domain.logic.investment

import kotlin.math.pow

object SipCalculator {
    /**
     * Calculate SIP (Systematic Investment Plan) Returns
     * @param monthlyInvestment: Amount invested per month
     * @param expectedReturnRate: Annual expected return rate (in %)
     * @param timePeriodYears: Time period in years
     * @return Triple(Invested Amount, Estimated Returns, Total Value)
     */
    fun calculate(
        monthlyInvestment: Double,
        expectedReturnRate: Double,
        timePeriodYears: Double
    ): Triple<Double, Double, Double> {
        val i = expectedReturnRate / 12 / 100
        val n = timePeriodYears * 12
        
        val totalValue = monthlyInvestment * ((1 + i).pow(n) - 1) / i * (1 + i)
        val investedAmount = monthlyInvestment * n
        val estimatedReturns = totalValue - investedAmount
        
        return Triple(investedAmount, estimatedReturns, totalValue)
    }
}
