package com.hsb.calchub.domain.logic.retirement

import kotlin.math.ceil
import kotlin.math.pow

/**
 * Calculator logic for FIRE (Financial Independence, Retire Early) and Coast-FIRE.
 */
object FireCalculator {
    private const val MAX_SIMULATION_YEARS = 60

    /**
     * Input parameters for FIRE & Coast-FIRE calculations.
     */
    data class FireInput(
        val currentAge: Int,
        val monthlyExpenses: Double,
        val currentCorpus: Double,
        val monthlySip: Double,
        val expectedReturnPct: Double,
        val inflationPct: Double,
        val safeWithdrawalRatePct: Double = 4.0,
        val targetRetirementAge: Int = 60
    )

    /**
     * FIRE & Coast-FIRE projection expressed in today's purchasing power.
     */
    data class FireResult(
        val fireNumber: Double,
        val yearsToFire: Int,
        val fireAge: Int,
        val coastFireNumber: Double,
        val isCoastFireReached: Boolean,
        val coastFireGap: Double,
        val projectedCorpusAtFire: Double
    )

    /**
     * Computes inflation-adjusted FIRE corpus target, Coast-FIRE threshold, and timeline to FIRE.
     */
    fun calculate(input: FireInput): FireResult {
        val (
            currentAge,
            monthlyExpenses,
            currentCorpus,
            monthlySip,
            expectedReturnPct,
            inflationPct,
            safeWithdrawalRatePct,
            targetRetirementAge
        ) = input
        val safeAge = currentAge.coerceIn(18, 80)
        if (!monthlyExpenses.isFinite() || !currentCorpus.isFinite() || !monthlySip.isFinite() ||
            !expectedReturnPct.isFinite() || !inflationPct.isFinite() || !safeWithdrawalRatePct.isFinite() ||
            monthlyExpenses <= 0.0 || safeWithdrawalRatePct <= 0.0 || expectedReturnPct < 0.0 || inflationPct < 0.0
        ) {
            return FireResult(0.0, 0, safeAge, 0.0, false, 0.0, 0.0)
        }

        val safeCorpus = currentCorpus.coerceAtLeast(0.0)
        val safeSip = monthlySip.coerceAtLeast(0.0)
        val safeTargetAge = targetRetirementAge.coerceAtLeast(safeAge + 1)

        val realAnnualRate = ((1.0 + expectedReturnPct / 100.0) / (1.0 + inflationPct / 100.0)) - 1.0
        val fireNumber = (monthlyExpenses * 12.0) / (safeWithdrawalRatePct / 100.0)
        val yearsToCoast = (safeTargetAge - safeAge).coerceAtLeast(1)
        val coastFireNumber = if (realAnnualRate > 0.0) {
            fireNumber / (1.0 + realAnnualRate).pow(yearsToCoast)
        } else {
            fireNumber
        }
        val isCoastFireReached = safeCorpus >= coastFireNumber
        val coastFireGap = (coastFireNumber - safeCorpus).coerceAtLeast(0.0)

        if (safeCorpus >= fireNumber) {
            return FireResult(
                fireNumber = fireNumber,
                yearsToFire = 0,
                fireAge = safeAge,
                coastFireNumber = coastFireNumber,
                isCoastFireReached = isCoastFireReached,
                coastFireGap = coastFireGap,
                projectedCorpusAtFire = safeCorpus
            )
        }

        val realMonthlyRate = if (1.0 + realAnnualRate > 0.0) {
            (1.0 + realAnnualRate).pow(1.0 / 12.0) - 1.0
        } else {
            0.0
        }

        var corpus = safeCorpus
        var monthsNeeded = MAX_SIMULATION_YEARS * 12
        for (month in 1..(MAX_SIMULATION_YEARS * 12)) {
            corpus = (corpus * (1.0 + realMonthlyRate) + safeSip).coerceAtLeast(0.0)
            if (corpus >= fireNumber) {
                monthsNeeded = month
                break
            }
        }
        val yearsToFire = ceil(monthsNeeded / 12.0).toInt().coerceIn(0, MAX_SIMULATION_YEARS)

        return FireResult(
            fireNumber = fireNumber,
            yearsToFire = yearsToFire,
            fireAge = safeAge + yearsToFire,
            coastFireNumber = coastFireNumber,
            isCoastFireReached = isCoastFireReached,
            coastFireGap = coastFireGap,
            projectedCorpusAtFire = corpus
        )
    }
}
