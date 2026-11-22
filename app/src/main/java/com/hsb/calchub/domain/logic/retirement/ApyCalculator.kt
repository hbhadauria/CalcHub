package com.hsb.calchub.domain.logic.retirement

import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Calculator logic for Atal Pension Yojana (APY).
 * Uses the official PFRDA age 18–40 contribution chart across all 5 pension slabs (₹1,000 to ₹5,000).
 */
object ApyCalculator {
    private val PENSION_SLABS = intArrayOf(1000, 2000, 3000, 4000, 5000)

    // Official PFRDA monthly contribution table indexed by entry age (18..40) -> [1K, 2K, 3K, 4K, 5K]
    private val APY_CHART: Map<Int, IntArray> = mapOf(
        18 to intArrayOf(42, 84, 126, 168, 210),
        19 to intArrayOf(46, 92, 138, 183, 228),
        20 to intArrayOf(50, 100, 150, 198, 248),
        21 to intArrayOf(54, 108, 162, 215, 269),
        22 to intArrayOf(59, 117, 177, 234, 292),
        23 to intArrayOf(64, 127, 192, 254, 318),
        24 to intArrayOf(70, 139, 208, 277, 346),
        25 to intArrayOf(76, 151, 226, 301, 376),
        26 to intArrayOf(82, 164, 246, 327, 409),
        27 to intArrayOf(90, 178, 268, 356, 446),
        28 to intArrayOf(97, 194, 292, 388, 485),
        29 to intArrayOf(106, 212, 318, 423, 529),
        30 to intArrayOf(116, 231, 347, 462, 577),
        31 to intArrayOf(126, 252, 379, 504, 630),
        32 to intArrayOf(138, 276, 414, 551, 689),
        33 to intArrayOf(151, 302, 453, 602, 752),
        34 to intArrayOf(165, 330, 495, 659, 824),
        35 to intArrayOf(181, 362, 543, 722, 902),
        36 to intArrayOf(198, 396, 594, 792, 990),
        37 to intArrayOf(218, 436, 654, 870, 1087),
        38 to intArrayOf(240, 480, 720, 960, 1196),
        39 to intArrayOf(264, 528, 792, 1054, 1318),
        40 to intArrayOf(291, 582, 873, 1164, 1454)
    )

    /**
     * Calculates monthly contribution and total contribution for APY.
     *
     * @param currentAge The current age of the subscriber (clamped to 18..40 for lookup).
     * @param pensionAmount Desired monthly pension amount (snapped to nearest ₹1K..₹5K slab; default 5000).
     * @return A [Triple] containing:
     *  - First: Monthly Contribution Amount
     *  - Second: Total Contribution over the years until age 60
     *  - Third: Snapped Pension Amount
     */
    fun calculate(currentAge: Double, pensionAmount: Double = 5000.0): Triple<Double, Double, Double> {
        if (!currentAge.isFinite() || !pensionAmount.isFinite() ||
            currentAge <= 0.0 || currentAge >= 60.0 || pensionAmount <= 0.0
        ) {
            return Triple(0.0, 0.0, 0.0)
        }
        val age = currentAge.roundToInt().coerceIn(18, 40)
        val slabIndex = PENSION_SLABS.indices.minByOrNull { abs(PENSION_SLABS[it] - pensionAmount) } ?: 4
        val snappedPension = PENSION_SLABS[slabIndex].toDouble()
        val monthlyContribution = APY_CHART.getValue(age)[slabIndex].toDouble()
        val yearsToRetirement = (60 - age).coerceAtLeast(0)
        val totalContribution = monthlyContribution * 12.0 * yearsToRetirement
        return Triple(monthlyContribution, totalContribution, snappedPension)
    }
}
