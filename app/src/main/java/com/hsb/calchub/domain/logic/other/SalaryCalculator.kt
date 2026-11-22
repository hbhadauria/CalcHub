package com.hsb.calchub.domain.logic.other

import com.hsb.calchub.domain.logic.tax.IncomeTaxCalculator

/**
 * Calculator logic for Salary (Net Pay), supporting both component-wise net pay
 * and CTC-to-in-hand estimation under the FY26 New Tax Regime.
 */
object SalaryCalculator {
    /**
     * Breakdown of monthly and annual take-home pay derived from Annual CTC.
     */
    data class CtcBreakdownResult(
        val annualCtc: Double,
        val monthlyGross: Double,
        val monthlyPf: Double,
        val monthlyProfTax: Double,
        val monthlyIncomeTax: Double,
        val monthlyInHand: Double,
        val annualInHand: Double
    )

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

    /**
     * Estimates monthly and annual take-home pay from Annual CTC using FY 2025-26 New Tax Regime slabs.
     */
    fun calculateFromCtc(
        annualCtc: Double,
        variableBonusPct: Double = 0.0,
        basicPctOfFixed: Double = 40.0,
        pfMonthlyCap: Double = 1800.0,
        monthlyProfTax: Double = 200.0
    ): CtcBreakdownResult {
        if (!annualCtc.isFinite() || !variableBonusPct.isFinite() || !basicPctOfFixed.isFinite() ||
            !pfMonthlyCap.isFinite() || !monthlyProfTax.isFinite() || annualCtc <= 0.0
        ) {
            return CtcBreakdownResult(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
        }

        val safeBonusPct = variableBonusPct.coerceIn(0.0, 50.0)
        val safeBasicPct = basicPctOfFixed.coerceIn(20.0, 70.0)
        val safePfCap = pfMonthlyCap.coerceAtLeast(0.0)
        val safeMonthlyPt = monthlyProfTax.coerceAtLeast(0.0)

        val fixedCtc = annualCtc * (1.0 - safeBonusPct / 100.0)
        val annualBasic = fixedCtc * (safeBasicPct / 100.0)
        val rawMonthlyPf = (annualBasic / 12.0) * 0.12
        val monthlyPf = if (safePfCap <= 0.0) 0.0 else minOf(rawMonthlyPf, safePfCap)
        val employerAnnualPf = monthlyPf * 12.0

        val taxableGross = (fixedCtc - employerAnnualPf).coerceAtLeast(0.0)
        val annualIncomeTax = IncomeTaxCalculator.calculateFy26NewRegime(taxableGross).second

        val monthlyGross = taxableGross / 12.0
        val monthlyIncomeTax = annualIncomeTax / 12.0
        val monthlyInHand = (monthlyGross - monthlyPf - safeMonthlyPt - monthlyIncomeTax).coerceAtLeast(0.0)

        return CtcBreakdownResult(
            annualCtc = annualCtc,
            monthlyGross = monthlyGross,
            monthlyPf = monthlyPf,
            monthlyProfTax = safeMonthlyPt,
            monthlyIncomeTax = monthlyIncomeTax,
            monthlyInHand = monthlyInHand,
            annualInHand = monthlyInHand * 12.0
        )
    }
}
