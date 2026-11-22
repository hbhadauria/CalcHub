package com.hsb.calchub.domain.logic.investment

import com.hsb.calchub.domain.logic.other.InflationCalculator

/**
 * Calculator logic for Spend vs. Invest opportunity cost of recurring discretionary expenses.
 */
object SpendVsInvestCalculator {
    /**
     * Future wealth forfeited when recurring monthly spending is not invested.
     */
    data class SpendVsInvestResult(
        val totalSpent: Double,
        val futureValueIfInvested: Double,
        val opportunityCostGain: Double,
        val inflationAdjustedValue: Double
    )

    /**
     * Computes what a recurring monthly spend could compound to in a SIP over [years],
     * optionally discounted by annual inflation.
     */
    fun calculate(
        monthlySpend: Double,
        expectedReturnPct: Double,
        years: Double,
        inflationPct: Double = 0.0
    ): SpendVsInvestResult {
        if (!monthlySpend.isFinite() || !expectedReturnPct.isFinite() || !years.isFinite() ||
            !inflationPct.isFinite() || monthlySpend <= 0.0 || years <= 0.0 ||
            expectedReturnPct < 0.0 || inflationPct < 0.0
        ) {
            return SpendVsInvestResult(0.0, 0.0, 0.0, 0.0)
        }

        val (totalSpent, gain, futureValue) = SipCalculator.calculate(monthlySpend, expectedReturnPct, years)
        val realValue = if (inflationPct > 0.0) {
            InflationCalculator.adjustForInflation(futureValue, inflationPct, years)
        } else {
            futureValue
        }

        return SpendVsInvestResult(
            totalSpent = totalSpent,
            futureValueIfInvested = futureValue,
            opportunityCostGain = gain,
            inflationAdjustedValue = realValue
        )
    }
}
