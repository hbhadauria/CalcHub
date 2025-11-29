package com.hsb.calchub.domain.logic.other
object SalaryCalculator {
    fun calculate(basicSalary: Double, hra: Double, otherAllowances: Double, pf: Double, professionalTax: Double = 200.0): Triple<Double, Double, Double> {
        val grossSalary = basicSalary + hra + otherAllowances
        val deductions = pf + professionalTax
        val netSalary = grossSalary - deductions
        return Triple(grossSalary, deductions, netSalary)
    }
}
