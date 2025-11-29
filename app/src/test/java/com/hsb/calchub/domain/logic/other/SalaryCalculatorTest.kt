package com.hsb.calchub.domain.logic.other

import org.junit.Assert.assertEquals
import org.junit.Test

class SalaryCalculatorTest {

    @Test
    fun `calculate Salary returns correctly`() {
        val basicSalary = 50000.0
        val hra = 20000.0
        val otherAllowances = 10000.0
        val pf = 1800.0
        val professionalTax = 200.0
        
        val result = SalaryCalculator.calculate(basicSalary, hra, otherAllowances, pf, professionalTax)
        
        // Gross = 80000
        // Deductions = 2000
        // Net = 78000
        
        assertEquals(80000.0, result.first, 0.01)
        assertEquals(2000.0, result.second, 0.01)
        assertEquals(78000.0, result.third, 0.01)
    }
}
