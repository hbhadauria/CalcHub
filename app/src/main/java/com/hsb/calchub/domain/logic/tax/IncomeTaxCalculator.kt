package com.hsb.calchub.domain.logic.tax

/**
 * Calculator logic for Income Tax covering Old Regime (with Sec 87A rebate + 4% cess)
 * and FY 2025-26 New Regime (with ₹75,000 standard deduction, Sec 87A rebate up to ₹12L taxable
 * with marginal relief, and 4% cess).
 */
object IncomeTaxCalculator {
    private const val OLD_REGIME_87A_LIMIT = 500_000.0
    private const val NEW_REGIME_STD_DEDUCTION = 75_000.0
    private const val NEW_REGIME_87A_LIMIT = 1_200_000.0
    private const val NEW_REGIME_RELIEF_CUTOFF = 1_275_000.0
    private const val HEALTH_EDUCATION_CESS = 1.04

    /**
     * Calculates Old Regime Income Tax including Section 87A rebate (taxable <= ₹5L) and 4% cess.
     *
     * @param annualIncome Total annual income.
     * @param deductions Total deductions (e.g., 80C, 80D, HRA).
     * @return A [Triple] containing:
     *  - First: Annual Income
     *  - Second: Estimated Tax (including 4% cess)
     *  - Third: Net Income (Post Tax)
     */
    fun calculate(annualIncome: Double, deductions: Double = 0.0): Triple<Double, Double, Double> {
        if (!annualIncome.isFinite() || !deductions.isFinite() || annualIncome <= 0.0) {
            return Triple(0.0, 0.0, 0.0)
        }
        val safeDeductions = deductions.coerceAtLeast(0.0)
        val taxableIncome = (annualIncome - safeDeductions).coerceAtLeast(0.0)
        val slabTax = when {
            taxableIncome <= OLD_REGIME_87A_LIMIT -> 0.0
            taxableIncome <= 1_000_000.0 -> 12_500.0 + (taxableIncome - 500_000.0) * 0.20
            else -> 12_500.0 + 100_000.0 + (taxableIncome - 1_000_000.0) * 0.30
        }
        val totalTax = slabTax * HEALTH_EDUCATION_CESS
        return Triple(annualIncome, totalTax, (annualIncome - totalTax).coerceAtLeast(0.0))
    }

    /**
     * Calculates simplified FY 2025-26 New Tax Regime estimation with ₹75,000 standard deduction,
     * Budget 2025 slabs, Section 87A rebate up to ₹12L taxable income (with marginal relief),
     * and 4% Health & Education Cess.
     *
     * @param annualIncome Total annual income.
     * @return A [Triple] containing:
     *  - First: Annual Income
     *  - Second: Estimated Tax (including 4% cess)
     *  - Third: Net Income (Post Tax)
     */
    fun calculateFy26NewRegime(annualIncome: Double): Triple<Double, Double, Double> {
        if (!annualIncome.isFinite() || annualIncome <= 0.0) {
            return Triple(0.0, 0.0, 0.0)
        }
        val taxable = (annualIncome - NEW_REGIME_STD_DEDUCTION).coerceAtLeast(0.0)
        val totalTax = calculateFy26NewRegimeBusiness(taxable)
        return Triple(annualIncome, totalTax, (annualIncome - totalTax).coerceAtLeast(0.0))
    }

    /**
     * Calculates FY 2025-26 New Tax Regime tax directly from taxable income without the salaried
     * ₹75,000 standard deduction (used for non-salaried / Section 44ADA presumptive income).
     */
    fun calculateFy26NewRegimeBusiness(taxableIncome: Double): Double {
        if (!taxableIncome.isFinite() || taxableIncome <= 0.0) {
            return 0.0
        }
        val taxable = taxableIncome.coerceAtLeast(0.0)
        if (taxable <= NEW_REGIME_87A_LIMIT) {
            return 0.0
        }

        var slabTax = 0.0
        val bands = listOf(
            400_000.0 to 0.0,
            400_000.0 to 0.05,
            400_000.0 to 0.10,
            400_000.0 to 0.15,
            400_000.0 to 0.20,
            400_000.0 to 0.25
        )
        var remaining = taxable
        for ((bandWidth, rate) in bands) {
            val slice = remaining.coerceAtMost(bandWidth)
            slabTax += slice * rate
            remaining -= slice
            if (remaining <= 0.0) break
        }
        if (remaining > 0.0) {
            slabTax += remaining * 0.30
        }

        val marginalCap = (taxable - NEW_REGIME_87A_LIMIT).coerceAtLeast(0.0)
        val taxAfterRelief = if (taxable <= NEW_REGIME_RELIEF_CUTOFF) minOf(slabTax, marginalCap) else slabTax
        return taxAfterRelief * HEALTH_EDUCATION_CESS
    }
}
