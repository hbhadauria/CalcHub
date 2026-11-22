package com.hsb.calchub.domain.logic.retirement

import kotlin.math.pow

/**
 * Calculator logic for Retirement Planning.
 * Calculates the corpus required for retirement and the SIP needed to achieve it.
 */
object RetirementCalculator {
    /**
     * Calculates the monthly SIP required to build the target retirement corpus.
     *
     * @param currentAge Current age.
     * @param retirementAge Desired retirement age.
     * @param monthlyExpenses Current monthly expenses.
     * @param inflationRate Expected annual inflation rate (default 6%).
     * @param expectedReturn Expected annual return on investment (default 12%).
     * @return A [Triple] containing:
     *  - First: Monthly SIP Amount Required
     *  - Second: Total Corpus Needed at Retirement
     *  - Third: Future Annual Expenses at Retirement
     */
    fun calculate(
        currentAge: Double,
        retirementAge: Double,
        monthlyExpenses: Double,
        inflationRate: Double = 6.0,
        expectedReturn: Double = 12.0
    ): Triple<Double, Double, Double> {
        if (!currentAge.isFinite() || !retirementAge.isFinite() || !monthlyExpenses.isFinite() ||
            !inflationRate.isFinite() || !expectedReturn.isFinite() ||
            retirementAge <= currentAge || monthlyExpenses <= 0.0 ||
            inflationRate < 0.0 || expectedReturn < 0.0
        ) {
            return Triple(0.0, 0.0, 0.0)
        }
        val yearsToRetirement = retirementAge - currentAge
        val yearsInRetirement = 25.0
        val futureMonthlyExpenses = monthlyExpenses * (1.0 + inflationRate / 100.0).pow(yearsToRetirement)
        val futureAnnualExpenses = futureMonthlyExpenses * 12.0
        val corpusNeeded = futureAnnualExpenses * yearsInRetirement
        val n = yearsToRetirement * 12.0
        val monthlySIP = if (expectedReturn == 0.0) {
            corpusNeeded / n
        } else {
            val i = expectedReturn / 12.0 / 100.0
            corpusNeeded / (((1.0 + i).pow(n) - 1.0) / i * (1.0 + i))
        }
        return Triple(monthlySIP, corpusNeeded, futureAnnualExpenses)
    }
}
