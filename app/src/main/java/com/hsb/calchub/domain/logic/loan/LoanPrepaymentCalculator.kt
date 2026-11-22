package com.hsb.calchub.domain.logic.loan

import kotlin.math.roundToInt

/**
 * Summary comparing standard loan repayment vs. accelerated repayment with monthly/lump-sum prepayments.
 */
data class LoanPrepaymentResult(
    val baseEmi: Double,
    val baseTotalInterest: Double,
    val newTotalInterest: Double,
    val interestSaved: Double,
    val baseMonths: Int,
    val newMonths: Int,
    val monthsSaved: Int
)

/**
 * Calculator logic for Loan Prepayment savings (extra monthly EMI + one-time lump-sum prepayment).
 */
object LoanPrepaymentCalculator {
    /**
     * Simulates standard amortization vs. accelerated repayment with [extraMonthlyPayment]
     * and optional [lumpSumPrepayment] applied at [lumpSumAtMonth].
     */
    fun calculate(
        loanAmount: Double,
        interestRate: Double,
        tenureYears: Double,
        extraMonthlyPayment: Double = 0.0,
        lumpSumPrepayment: Double = 0.0,
        lumpSumAtMonth: Int = 12
    ): LoanPrepaymentResult {
        if (!loanAmount.isFinite() || !interestRate.isFinite() || !tenureYears.isFinite() ||
            !extraMonthlyPayment.isFinite() || !lumpSumPrepayment.isFinite() ||
            loanAmount <= 0.0 || tenureYears <= 0.0 || interestRate < 0.0 ||
            extraMonthlyPayment < 0.0 || lumpSumPrepayment < 0.0
        ) {
            return LoanPrepaymentResult(0.0, 0.0, 0.0, 0.0, 0, 0, 0)
        }

        val (baseEmi, baseTotalInterest, _) = EmiCalculator.calculate(loanAmount, interestRate, tenureYears)
        val baseMonths = (tenureYears * 12.0).roundToInt().coerceAtLeast(1)
        val r = interestRate / 12.0 / 100.0
        val lumpMonth = lumpSumAtMonth.coerceAtLeast(1)

        var balance = loanAmount
        var newInterest = 0.0
        var monthsUsed = 0

        for (m in 1..baseMonths) {
            if (balance <= 0.01) break
            monthsUsed = m
            val intr = balance * r
            val scheduledPrin = (baseEmi - intr).coerceAtLeast(0.0)
            val lump = if (lumpSumPrepayment > 0.0 && m == lumpMonth) lumpSumPrepayment else 0.0
            val totalPrin = (scheduledPrin + extraMonthlyPayment + lump).coerceIn(0.0, balance)
            balance -= totalPrin
            newInterest += intr
        }

        val interestSaved = (baseTotalInterest - newInterest).coerceAtLeast(0.0)
        val monthsSaved = (baseMonths - monthsUsed).coerceAtLeast(0)

        return LoanPrepaymentResult(
            baseEmi = baseEmi,
            baseTotalInterest = baseTotalInterest,
            newTotalInterest = newInterest,
            interestSaved = interestSaved,
            baseMonths = baseMonths,
            newMonths = monthsUsed,
            monthsSaved = monthsSaved
        )
    }
}
