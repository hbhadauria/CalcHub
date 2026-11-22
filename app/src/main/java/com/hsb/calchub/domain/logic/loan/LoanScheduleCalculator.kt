package com.hsb.calchub.domain.logic.loan

/**
 * Yearly amortization summary used by the standard EMI chart.
 */
data class AmortizationSummary(
    val yearlyBreakdown: List<Pair<Double, Double>>,
    val crossoverYear: Int
)

/**
 * Loan repayment schedule summary supporting optional annual 1-extra-EMI prepayment.
 */
data class HomeLoanSchedule(
    val yearlyBreakdown: List<Pair<Double, Double>>,
    val crossoverYear: Int,
    val totalInterest: Double,
    val totalMonths: Int
)

/**
 * Loan amortization and prepayment schedule helper functions.
 */
object LoanScheduleCalculator {
    /**
     * Computes yearly Principal vs. Interest payments and the year when principal exceeds interest.
     */
    fun computeAmortizationSummary(
        principal: Double,
        annualInterestRate: Double,
        tenureYears: Int,
        monthlyEmi: Double
    ): AmortizationSummary {
        if (!principal.isFinite() || !annualInterestRate.isFinite() || !monthlyEmi.isFinite() ||
            principal <= 0.0 || tenureYears <= 0 || monthlyEmi <= 0.0
        ) {
            return AmortizationSummary(yearlyBreakdown = emptyList(), crossoverYear = 0)
        }
        val r = (annualInterestRate / 12.0 / 100.0).coerceAtLeast(0.0)
        var balance = principal
        val yearly = mutableListOf<Pair<Double, Double>>()
        var crossover = tenureYears

        for (yr in 1..tenureYears) {
            var yrPrin = 0.0
            var yrIntr = 0.0
            for (m in 1..12) {
                if (balance <= 0.0) break
                val intr = balance * r
                val prin = (monthlyEmi - intr).coerceIn(0.0, balance)
                yrPrin += prin
                yrIntr += intr
                balance -= prin
            }
            yearly.add(yrPrin to yrIntr)
            if (crossover == tenureYears && yrPrin > yrIntr) {
                crossover = yr
            }
        }

        return AmortizationSummary(yearlyBreakdown = yearly, crossoverYear = crossover)
    }

    /**
     * Simulates a home loan repayment schedule with an optional extra EMI paid every 12th month.
     */
    fun simulateHomeLoanSchedule(
        principal: Double,
        annualRate: Double,
        tenureYears: Int,
        monthlyEmi: Double,
        extraEmiEachYear: Boolean
    ): HomeLoanSchedule {
        if (!principal.isFinite() || !annualRate.isFinite() || !monthlyEmi.isFinite() ||
            principal <= 0.0 || tenureYears <= 0 || monthlyEmi <= 0.0
        ) {
            return HomeLoanSchedule(
                yearlyBreakdown = emptyList(),
                crossoverYear = 0,
                totalInterest = 0.0,
                totalMonths = 0
            )
        }
        val maxMonths = tenureYears * 12
        val r = (annualRate / 12.0 / 100.0).coerceAtLeast(0.0)
        var balance = principal
        var cumInterest = 0.0
        var monthsUsed = 0
        val yearly = mutableListOf<Pair<Double, Double>>()
        var crossover = tenureYears

        var yrPrin = 0.0
        var yrIntr = 0.0

        for (m in 1..maxMonths) {
            if (balance <= 0.01) break
            monthsUsed = m
            val intr = balance * r
            val baseDue = (monthlyEmi - intr).coerceAtLeast(0.0)
            val extra = if (extraEmiEachYear && m % 12 == 0) monthlyEmi else 0.0
            val prinPaid = (baseDue + extra).coerceIn(0.0, balance)
            balance -= prinPaid
            cumInterest += intr
            yrPrin += prinPaid
            yrIntr += intr

            if (m % 12 == 0 || balance <= 0.01 || m == maxMonths) {
                yearly.add(yrPrin to yrIntr)
                val yrIdx = yearly.size
                if (crossover == tenureYears && yrPrin > yrIntr) {
                    crossover = yrIdx
                }
                yrPrin = 0.0
                yrIntr = 0.0
            }
        }

        return HomeLoanSchedule(
            yearlyBreakdown = yearly,
            crossoverYear = crossover,
            totalInterest = cumInterest,
            totalMonths = monthsUsed.coerceAtLeast(1)
        )
    }
}
