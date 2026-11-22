package com.hsb.calchub.domain.logic.loan

/**
 * Calculator logic for comparing Flat Rate vs Reducing Balance Rate loans.
 * Compares the monthly EMI of both methods at the same quoted annual interest rate.
 */
object FlatVsReducingCalculator {
    /**
     * Calculates and compares EMI for Flat Rate and Reducing Balance Rate at the same annual rate.
     *
     * @param loanAmount The principal loan amount.
     * @param flatRate The quoted annual interest rate in percent.
     * @param tenureYears The loan tenure in years.
     * @return A [Triple] containing:
     *  - First: EMI as per Flat Rate method
     *  - Second: EMI as per Reducing Balance method at the same annual rate
     *  - Third: Difference in EMI (Flat - Reducing)
     */
    fun calculate(loanAmount: Double, flatRate: Double, tenureYears: Double): Triple<Double, Double, Double> {
        if (!loanAmount.isFinite() || !flatRate.isFinite() || !tenureYears.isFinite() ||
            loanAmount <= 0.0 || tenureYears <= 0.0 || flatRate < 0.0
        ) {
            return Triple(0.0, 0.0, 0.0)
        }
        val n = tenureYears * 12.0
        val totalInterestFlat = loanAmount * (flatRate / 100.0) * tenureYears
        val emiFlat = (loanAmount + totalInterestFlat) / n
        val emiReducing = EmiCalculator.calculate(loanAmount, flatRate, tenureYears).first
        return Triple(emiFlat, emiReducing, emiFlat - emiReducing)
    }
}
