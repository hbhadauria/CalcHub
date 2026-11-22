package com.hsb.calchub.domain.logic.retirement

/**
 * Calculator logic for Gratuity.
 * Gratuity is a lump sum amount paid by an employer to an employee for services rendered.
 */
object GratuityCalculator {
    /**
     * Calculates the gratuity amount and taxable/tax-free components.
     *
     * @param lastSalary Last drawn salary (Basic + DA).
     * @param yearsOfService Number of years of service.
     * @return A [Triple] containing:
     *  - First: Total Gratuity Amount
     *  - Second: Tax Exempt Gratuity
     *  - Third: Taxable Gratuity
     */
    fun calculate(lastSalary: Double, yearsOfService: Double): Triple<Double, Double, Double> {
        if (!lastSalary.isFinite() || !yearsOfService.isFinite() ||
            lastSalary <= 0.0 || yearsOfService <= 0.0
        ) {
            return Triple(0.0, 0.0, 0.0)
        }
        val gratuity = (lastSalary * yearsOfService * 15.0) / 26.0
        val taxFreeLimit = 2_000_000.0
        val taxableAmount = (gratuity - taxFreeLimit).coerceAtLeast(0.0)
        return Triple(gratuity, taxFreeLimit.coerceAtMost(gratuity), taxableAmount)
    }
}
