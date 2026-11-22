package com.hsb.calchub.domain.logic.other

import kotlin.math.pow

/**
 * Calculator logic for Compound Interest.
 * Compound interest is the interest on a loan or deposit calculated based on both the initial principal and the accumulated interest from previous periods.
 */
object CompoundInterestCalculator {
    /**
     * Calculates Compound Interest.
     *
     * @param principal The initial principal amount.
     * @param rate The annual interest rate in percent.
     * @param timeYears The time period in years.
     * @param compoundingFrequency The number of times interest is compounded per year (default is 1 for annually).
     * @return A [Triple] containing:
     *  - First: Principal Amount
     *  - Second: Total Interest Earned
     *  - Third: Total Amount (Principal + Interest)
     */
    fun calculate(principal: Double, rate: Double, timeYears: Double, compoundingFrequency: Int = 1): Triple<Double, Double, Double> {
        if (!principal.isFinite() || !rate.isFinite() || !timeYears.isFinite() ||
            principal <= 0.0 || rate < 0.0 || timeYears < 0.0
        ) {
            return Triple(0.0, 0.0, 0.0)
        }
        val r = rate / 100.0
        val n = compoundingFrequency.coerceAtLeast(1)
        val t = timeYears
        val totalValue = principal * (1.0 + r / n).pow(n * t)
        val interest = totalValue - principal
        return Triple(principal, interest, totalValue)
    }
}
