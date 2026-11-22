package com.hsb.calchub.domain.logic.other

import kotlin.math.pow

/**
 * Calculator logic for Inflation.
 * Calculates future cost escalation as well as present-day purchasing power of future investment values.
 */
object InflationCalculator {
    /**
     * Calculates the future cost due to inflation.
     *
     * @param currentPrice The current price or cost.
     * @param inflationRate The annual inflation rate in percent.
     * @param years The number of years in the future.
     * @return A [Triple] containing:
     *  - First: Current Price
     *  - Second: Increase in Price (Inflation Effect)
     *  - Third: Future Price
     */
    fun calculate(currentPrice: Double, inflationRate: Double, years: Double): Triple<Double, Double, Double> {
        if (!currentPrice.isFinite() || !inflationRate.isFinite() || !years.isFinite() ||
            currentPrice <= 0.0 || inflationRate < 0.0 || years < 0.0
        ) {
            return Triple(0.0, 0.0, 0.0)
        }
        val futurePrice = currentPrice * (1.0 + inflationRate / 100.0).pow(years)
        val increase = futurePrice - currentPrice
        return Triple(currentPrice, increase, futurePrice)
    }

    /**
     * Discounts a future nominal value to today's purchasing power using the annual inflation rate.
     *
     * @param nominalValue Future nominal maturity amount.
     * @param inflationRate Annual inflation rate in percent.
     * @param years Investment duration in years.
     * @return Inflation-adjusted present value (purchasing power in today's terms).
     */
    fun adjustForInflation(nominalValue: Double, inflationRate: Double, years: Double): Double {
        if (!nominalValue.isFinite() || nominalValue <= 0.0) {
            return 0.0
        }
        if (!inflationRate.isFinite() || !years.isFinite() || inflationRate <= 0.0 || years <= 0.0) {
            return nominalValue
        }
        return nominalValue / (1.0 + inflationRate / 100.0).pow(years)
    }
}
