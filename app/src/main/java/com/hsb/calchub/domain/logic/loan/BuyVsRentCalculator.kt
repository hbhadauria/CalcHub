package com.hsb.calchub.domain.logic.loan

import kotlin.math.abs
import kotlin.math.pow

/**
 * Calculator logic comparing buying a home with a loan versus renting and investing the surplus.
 */
object BuyVsRentCalculator {
    /**
     * Input parameters comparing home ownership against renting and investing.
     */
    data class BuyVsRentInput(
        val propertyPrice: Double,
        val downPaymentPct: Double,
        val loanRatePct: Double,
        val tenureYears: Int,
        val monthlyRent: Double,
        val rentEscalationPct: Double,
        val propertyAppreciationPct: Double,
        val sipReturnPct: Double
    )

    /**
     * Net wealth comparison at the end of the home loan / rental horizon.
     */
    data class BuyVsRentResult(
        val buyNetWealth: Double,
        val rentInvestNetWealth: Double,
        val monthlyEmi: Double,
        val futurePropertyValue: Double,
        val totalRentPaid: Double,
        val wealthDifference: Double,
        val isBuyingBetter: Boolean
    )

    /**
     * Compares projected home equity (buy) against investing down payment + monthly EMI-vs-rent surplus (rent).
     */
    fun calculate(input: BuyVsRentInput): BuyVsRentResult {
        val (
            propertyPrice,
            downPaymentPct,
            loanRatePct,
            tenureYears,
            monthlyRent,
            rentEscalationPct,
            propertyAppreciationPct,
            sipReturnPct
        ) = input
        if (!propertyPrice.isFinite() || !downPaymentPct.isFinite() || !loanRatePct.isFinite() ||
            !monthlyRent.isFinite() || !rentEscalationPct.isFinite() || !propertyAppreciationPct.isFinite() ||
            !sipReturnPct.isFinite() || propertyPrice <= 0.0 || tenureYears <= 0 ||
            loanRatePct < 0.0 || monthlyRent < 0.0 || rentEscalationPct < 0.0 ||
            propertyAppreciationPct < 0.0 || sipReturnPct < 0.0
        ) {
            return BuyVsRentResult(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, false)
        }

        val safeTenure = tenureYears.coerceIn(1, 40)
        val safeDownPct = downPaymentPct.coerceIn(0.0, 100.0)
        val downPayment = propertyPrice * safeDownPct / 100.0
        val loanAmount = (propertyPrice - downPayment).coerceAtLeast(0.0)
        val monthlyEmi = if (loanAmount > 0.0) {
            EmiCalculator.calculate(loanAmount, loanRatePct, safeTenure.toDouble()).first
        } else {
            0.0
        }

        val futurePropertyValue = propertyPrice * (1.0 + propertyAppreciationPct / 100.0).pow(safeTenure)
        val buyNetWealth = futurePropertyValue

        val monthlySipRate = sipReturnPct / 1200.0
        var investPortfolio = downPayment
        var totalRentPaid = 0.0
        val totalMonths = safeTenure * 12

        for (month in 0 until totalMonths) {
            val yearIdx = month / 12
            val currentRent = monthlyRent * (1.0 + rentEscalationPct / 100.0).pow(yearIdx)
            totalRentPaid += currentRent
            val monthlySurplus = monthlyEmi - currentRent
            investPortfolio = (investPortfolio * (1.0 + monthlySipRate) + monthlySurplus).coerceAtLeast(0.0)
        }

        return BuyVsRentResult(
            buyNetWealth = buyNetWealth,
            rentInvestNetWealth = investPortfolio,
            monthlyEmi = monthlyEmi,
            futurePropertyValue = futurePropertyValue,
            totalRentPaid = totalRentPaid,
            wealthDifference = abs(buyNetWealth - investPortfolio),
            isBuyingBetter = buyNetWealth >= investPortfolio
        )
    }
}
