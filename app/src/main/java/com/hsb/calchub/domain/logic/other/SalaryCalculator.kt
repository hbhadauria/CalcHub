package com.hsb.calchub.domain.logic.other

/**
 * Calculator logic for Salary (Net Pay).
 * Calculates the in-hand salary after standard deductions.
 */
object SalaryCalculator {
    /**
     * Calculates Gross, Deductions, and Net Salary.
     *
     * @param basicSalary Basic Pay component of salary.
     * @param hra House Rent Allowance.
     * @param otherAllowances Special and other allowances.
     * @param pf Provident Fund deduction.
     * @param professionalTax Professional Tax deduction (default 200).
     * @return A [Triple] containing:
     *  - First: Gross Salary (Basic + HRA + Allowances)
     *  - Second: Total Deductions (PF + PT)
     *  - Third: Net Salary (In-hand)
     */
    fun calculate(basicSalary: Double, hra: Double, otherAllowances: Double, pf: Double, professionalTax: Double = 200.0): Triple<Double, Double, Double> {
        if (!basicSalary.isFinite() || !hra.isFinite() || !otherAllowances.isFinite() ||
            !pf.isFinite() || !professionalTax.isFinite()
        ) {
            return Triple(0.0, 0.0, 0.0)
        }
        val safeBasic = basicSalary.coerceAtLeast(0.0)
        val safeHra = hra.coerceAtLeast(0.0)
        val safeOther = otherAllowances.coerceAtLeast(0.0)
        val safePf = pf.coerceAtLeast(0.0)
        val safePt = professionalTax.coerceAtLeast(0.0)
        val grossSalary = safeBasic + safeHra + safeOther
        if (grossSalary <= 0.0) {
            return Triple(0.0, 0.0, 0.0)
        }
        val deductions = safePf + safePt
        val netSalary = (grossSalary - deductions).coerceAtLeast(0.0)
        return Triple(grossSalary, deductions, netSalary)
    }
}
