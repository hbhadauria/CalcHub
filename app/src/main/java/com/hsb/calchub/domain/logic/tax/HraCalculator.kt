package com.hsb.calchub.domain.logic.tax

/**
 * Calculator logic for House Rent Allowance (HRA) Exemption.
 * Calculates the HRA exemption amount based on salary, rent paid, and city type.
 */
object HraCalculator {
    /**
     * Calculates HRA Exemption.
     *
     * @param basicSalary Basic Pay component.
     * @param hraReceived Actual HRA received from employer.
     * @param rentPaid Actual rent paid by the employee.
     * @param isMetroCity True if the residence is in a metro city (50% rule), False otherwise (40% rule).
     * @return A [Triple] containing:
     *  - First: Actual HRA Received
     *  - Second: HRA Exemption Amount
     *  - Third: Taxable HRA Amount
     */
    fun calculate(basicSalary: Double, hraReceived: Double, rentPaid: Double, isMetroCity: Boolean): Triple<Double, Double, Double> {
        if (!basicSalary.isFinite() || !hraReceived.isFinite() || !rentPaid.isFinite()) {
            return Triple(0.0, 0.0, 0.0)
        }
        val safeBasic = basicSalary.coerceAtLeast(0.0)
        val safeHra = hraReceived.coerceAtLeast(0.0)
        val safeRent = rentPaid.coerceAtLeast(0.0)
        val metroPercent = if (isMetroCity) 0.5 else 0.4
        val exemption1 = safeHra
        val exemption2 = (safeRent - (0.1 * safeBasic)).coerceAtLeast(0.0)
        val exemption3 = metroPercent * safeBasic
        val exemption = minOf(exemption1, exemption2, exemption3).coerceAtLeast(0.0)
        val taxableHRA = (safeHra - exemption).coerceAtLeast(0.0)
        return Triple(safeHra, exemption, taxableHRA)
    }
}
