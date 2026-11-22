package com.hsb.calchub.domain.logic.loan

import kotlin.math.pow

/**
 * Calculator logic for EMI (Equated Monthly Installment).
 * Calculates the monthly loan repayment amount based on loan details.
 */
object EmiCalculator {
    /**
     * Calculates EMI using the standard formula: E = P * r * (1 + r)^n / ((1 + r)^n - 1)
     *
     * @param loanAmount The principal loan amount.
     * @param interestRate The annual interest rate in percent.
     * @param tenureYears The loan tenure in years.
     * @return A [Triple] containing:
     *  - First: Monthly EMI Amount
     *  - Second: Total Interest Payable
     *  - Third: Total Amount Payable (Principal + Interest)
     */
    fun calculate(
        loanAmount: Double,
        interestRate: Double,
        tenureYears: Double
    ): Triple<Double, Double, Double> {
        if (!loanAmount.isFinite() || !interestRate.isFinite() || !tenureYears.isFinite() ||
            loanAmount <= 0.0 || tenureYears <= 0.0 || interestRate < 0.0
        ) {
            return Triple(0.0, 0.0, 0.0)
        }
        val n = tenureYears * 12.0
        val emi = if (interestRate == 0.0) {
            loanAmount / n
        } else {
            val r = interestRate / 12.0 / 100.0
            (loanAmount * r * (1.0 + r).pow(n)) / ((1.0 + r).pow(n) - 1.0)
        }
        val totalAmountPayable = emi * n
        val totalInterest = totalAmountPayable - loanAmount
        return Triple(emi, totalInterest, totalAmountPayable)
    }
}
