package com.hsb.calchub.domain.logic.investment

import kotlin.math.pow

/**
 * Result breakdown for Reverse / Goal-Based SIP planning.
 */
data class GoalSipResult(
    val requiredMonthlySip: Double,
    val inflationAdjustedTarget: Double,
    val totalInvested: Double,
    val wealthGain: Double
)

/**
 * Calculator logic for Goal-Based SIP: determines monthly SIP required to achieve a target corpus.
 */
object GoalSipCalculator {
    /**
     * Computes required monthly SIP to accumulate [targetCorpus] (optionally adjusted for [inflationRate])
     * over [timePeriodYears] at [expectedReturnRate].
     */
    fun calculate(
        targetCorpus: Double,
        expectedReturnRate: Double,
        timePeriodYears: Double,
        inflationRate: Double = 0.0
    ): GoalSipResult {
        if (!targetCorpus.isFinite() || !expectedReturnRate.isFinite() ||
            !timePeriodYears.isFinite() || !inflationRate.isFinite() ||
            targetCorpus <= 0.0 || timePeriodYears <= 0.0 ||
            expectedReturnRate < 0.0 || inflationRate < 0.0
        ) {
            return GoalSipResult(0.0, 0.0, 0.0, 0.0)
        }

        val adjustedTarget = if (inflationRate > 0.0) {
            targetCorpus * (1.0 + inflationRate / 100.0).pow(timePeriodYears)
        } else {
            targetCorpus
        }

        val n = timePeriodYears * 12.0
        val requiredSip = if (expectedReturnRate == 0.0) {
            adjustedTarget / n
        } else {
            val i = expectedReturnRate / 12.0 / 100.0
            adjustedTarget / (((1.0 + i).pow(n) - 1.0) / i * (1.0 + i))
        }

        val totalInvested = requiredSip * n
        val wealthGain = (adjustedTarget - totalInvested).coerceAtLeast(0.0)

        return GoalSipResult(
            requiredMonthlySip = requiredSip,
            inflationAdjustedTarget = adjustedTarget,
            totalInvested = totalInvested,
            wealthGain = wealthGain
        )
    }
}
