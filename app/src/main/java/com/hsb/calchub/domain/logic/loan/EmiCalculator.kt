package com.hsb.calchub.domain.logic.loan

import kotlin.math.pow

object EmiCalculator {
    /**
     * Calculate EMI (Equated Monthly Installment)
     * @param loanAmount: Total loan amount
     * @param interestRate: Annual interest rate (in %)
     * @param tenureYears: Loan tenure in years
     * @return Triple(Monthly EMI, Total Interest, Total Amount Payable)
     */
    fun calculate(
        loanAmount: Double,
        interestRate: Double,
        tenureYears: Double
    ): Triple<Double, Double, Double> {
        val r = interestRate / 12 / 100
        val n = tenureYears * 12
        
        val emi = if (interestRate == 0.0) {
            loanAmount / n
        } else {
            (loanAmount * r * (1 + r).pow(n)) / ((1 + r).pow(n) - 1)
        }
        
        val totalAmountPayable = emi * n
        val totalInterest = totalAmountPayable - loanAmount
        
        return Triple(emi, totalInterest, totalAmountPayable)
    }
}
