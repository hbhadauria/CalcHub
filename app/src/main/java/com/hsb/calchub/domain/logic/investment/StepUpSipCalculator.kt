package com.hsb.calchub.domain.logic.investment

/**
 * Result breakdown for Step-Up SIP compounding projection.
 */
data class StepUpProjection(
    val investedAmount: Double,
    val estimatedReturns: Double,
    val totalValue: Double,
    val yearlyPoints: List<Pair<Double, Double>>,
    val finalMonthlySip: Double
)

/**
 * Calculator logic for Step-Up Systematic Investment Plan (SIP) with annual contribution top-ups.
 */
object StepUpSipCalculator {
    /**
     * Simulates a Step-Up SIP where monthly investments step up at the start of each subsequent year.
     *
     * @param initialMonthly Starting monthly SIP contribution.
     * @param annualStepUpPct Annual percentage increase applied after each year.
     * @param annualReturnPct Expected annual return rate in percent.
     * @param years Total investment duration in whole years.
     */
    fun calculate(
        initialMonthly: Double,
        annualStepUpPct: Double,
        annualReturnPct: Double,
        years: Int
    ): StepUpProjection {
        if (!initialMonthly.isFinite() || !annualStepUpPct.isFinite() || !annualReturnPct.isFinite() ||
            initialMonthly <= 0.0 || years <= 0
        ) {
            return StepUpProjection(
                investedAmount = 0.0,
                estimatedReturns = 0.0,
                totalValue = 0.0,
                yearlyPoints = listOf(0.0 to 0.0),
                finalMonthlySip = 0.0
            )
        }

        val monthlyRate = (annualReturnPct / 12.0 / 100.0).coerceAtLeast(0.0)
        val stepUpMultiplier = 1.0 + annualStepUpPct.coerceAtLeast(0.0) / 100.0
        var currentMonthly = initialMonthly
        var totalInvested = 0.0
        var corpus = 0.0
        val points = mutableListOf(0.0 to 0.0)

        for (yr in 1..years) {
            for (m in 1..12) {
                totalInvested += currentMonthly
                corpus = (corpus + currentMonthly) * (1.0 + monthlyRate)
            }
            points.add(totalInvested to corpus)
            if (yr < years) {
                currentMonthly *= stepUpMultiplier
            }
        }

        val gain = (corpus - totalInvested).coerceAtLeast(0.0)
        return StepUpProjection(
            investedAmount = totalInvested,
            estimatedReturns = gain,
            totalValue = corpus,
            yearlyPoints = points,
            finalMonthlySip = currentMonthly
        )
    }
}
