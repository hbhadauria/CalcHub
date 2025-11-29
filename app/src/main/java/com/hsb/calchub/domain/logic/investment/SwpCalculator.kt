package com.hsb.calchub.domain.logic.investment

object SwpCalculator {
    fun calculate(
        totalInvestment: Double,
        withdrawalPerMonth: Double,
        expectedReturnRate: Double,
        timePeriodYears: Double
    ): Triple<Double, Double, Double> {
        val r = expectedReturnRate / 12 / 100
        val n = timePeriodYears * 12
        
        var balance = totalInvestment
        var totalWithdrawn = 0.0
        
        for (month in 1..n.toInt()) {
            balance = balance * (1 + r) - withdrawalPerMonth
            totalWithdrawn += withdrawalPerMonth
            if (balance < 0) balance = 0.0
        }
        
        return Triple(totalInvestment, totalWithdrawn, balance)
    }
}
