package com.hsb.calchub.domain.logic.tax

/**
 * Calculator logic for Freelance / Side-Hustle Tax under Section 44ADA presumptive taxation
 * paired with FY 2025-26 New Tax Regime slabs and post-tax hourly rate estimation.
 */
object FreelanceTaxCalculator {
    private const val SECTION_44ADA_DIGITAL_LIMIT = 7_500_000.0
    private const val PRESUMPTIVE_PROFIT_SHARE = 0.50

    /**
     * Section 44ADA presumptive income tax breakdown and effective post-tax hourly rate.
     */
    data class FreelanceTaxResult(
        val grossReceipts: Double,
        val presumptiveTaxableIncome: Double,
        val estimatedTax: Double,
        val effectiveTaxRatePct: Double,
        val annualTakeHome: Double,
        val monthlyTakeHome: Double,
        val postTaxHourlyRate: Double,
        val taxSavedVsRegular: Double,
        val exceeds44AdaLimit: Boolean
    )

    /**
     * Estimates presumptive Section 44ADA tax (50% profit) and post-tax take-home/hourly rate.
     */
    fun calculate(
        annualGrossReceipts: Double,
        billableHoursPerWeek: Double = 30.0,
        workingWeeksPerYear: Int = 48
    ): FreelanceTaxResult {
        if (!annualGrossReceipts.isFinite() || !billableHoursPerWeek.isFinite() || annualGrossReceipts <= 0.0) {
            return FreelanceTaxResult(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, false)
        }

        val exceedsLimit = annualGrossReceipts > SECTION_44ADA_DIGITAL_LIMIT
        val presumptiveTaxableIncome = annualGrossReceipts * PRESUMPTIVE_PROFIT_SHARE
        val estimatedTax = IncomeTaxCalculator.calculateFy26NewRegimeBusiness(presumptiveTaxableIncome)
        val regularTax = IncomeTaxCalculator.calculateFy26NewRegimeBusiness(annualGrossReceipts)
        val taxSaved = (regularTax - estimatedTax).coerceAtLeast(0.0)

        val effectiveTaxRatePct = (estimatedTax / annualGrossReceipts) * 100.0
        val annualTakeHome = (annualGrossReceipts - estimatedTax).coerceAtLeast(0.0)
        val monthlyTakeHome = annualTakeHome / 12.0

        val totalBillableHours = billableHoursPerWeek.coerceAtLeast(0.0) * workingWeeksPerYear.coerceAtLeast(0)
        val postTaxHourlyRate = if (totalBillableHours > 0.0) annualTakeHome / totalBillableHours else 0.0

        return FreelanceTaxResult(
            grossReceipts = annualGrossReceipts,
            presumptiveTaxableIncome = presumptiveTaxableIncome,
            estimatedTax = estimatedTax,
            effectiveTaxRatePct = effectiveTaxRatePct,
            annualTakeHome = annualTakeHome,
            monthlyTakeHome = monthlyTakeHome,
            postTaxHourlyRate = postTaxHourlyRate,
            taxSavedVsRegular = taxSaved,
            exceeds44AdaLimit = exceedsLimit
        )
    }
}
