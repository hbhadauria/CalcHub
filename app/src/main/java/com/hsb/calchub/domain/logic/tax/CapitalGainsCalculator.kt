package com.hsb.calchub.domain.logic.tax

/**
 * FY 2025-26 Indian Capital Gains tax breakdown for Equity and Debt/Other assets.
 */
data class CapitalGainsResult(
    val capitalGain: Double,
    val taxableGain: Double,
    val totalTax: Double,
    val netProceeds: Double,
    val isLongTerm: Boolean,
    val baseRatePercent: Double
)

/**
 * Calculator logic for Short-Term and Long-Term Capital Gains Tax (FY26 rules + 4% cess).
 */
object CapitalGainsCalculator {
    private const val EQUITY_LTCG_EXEMPTION = 125_000.0
    private const val HEALTH_EDUCATION_CESS = 1.04

    /**
     * Calculates Capital Gains tax on realized asset sale.
     *
     * @param purchaseValue Cost of acquisition.
     * @param saleValue Realized sale consideration.
     * @param holdingMonths Holding period in months (>=12m LTCG for equity; >=24m LTCG for other assets).
     * @param isEquity True for listed equity / equity mutual funds; False for debt / other capital assets.
     */
    fun calculate(
        purchaseValue: Double,
        saleValue: Double,
        holdingMonths: Int,
        isEquity: Boolean = true
    ): CapitalGainsResult {
        if (!purchaseValue.isFinite() || !saleValue.isFinite() ||
            purchaseValue < 0.0 || saleValue < 0.0 || holdingMonths < 0
        ) {
            return CapitalGainsResult(0.0, 0.0, 0.0, 0.0, isLongTerm = false, baseRatePercent = 0.0)
        }

        val gain = saleValue - purchaseValue
        val isLongTerm = if (isEquity) holdingMonths >= 12 else holdingMonths >= 24

        if (gain <= 0.0) {
            return CapitalGainsResult(
                capitalGain = gain,
                taxableGain = 0.0,
                totalTax = 0.0,
                netProceeds = saleValue,
                isLongTerm = isLongTerm,
                baseRatePercent = 0.0
            )
        }

        val baseRatePercent: Double
        val taxableGain: Double
        when {
            isEquity && isLongTerm -> {
                baseRatePercent = 12.5
                taxableGain = (gain - EQUITY_LTCG_EXEMPTION).coerceAtLeast(0.0)
            }
            isEquity -> {
                baseRatePercent = 20.0
                taxableGain = gain
            }
            isLongTerm -> {
                baseRatePercent = 12.5
                taxableGain = gain
            }
            else -> {
                baseRatePercent = 30.0
                taxableGain = gain
            }
        }

        val totalTax = taxableGain * (baseRatePercent / 100.0) * HEALTH_EDUCATION_CESS
        val netProceeds = (saleValue - totalTax).coerceAtLeast(0.0)

        return CapitalGainsResult(
            capitalGain = gain,
            taxableGain = taxableGain,
            totalTax = totalTax,
            netProceeds = netProceeds,
            isLongTerm = isLongTerm,
            baseRatePercent = baseRatePercent
        )
    }
}
