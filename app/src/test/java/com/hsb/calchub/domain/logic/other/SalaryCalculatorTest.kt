package com.hsb.calchub.domain.logic.other

import org.junit.Assert.assertEquals
import org.junit.Test

class SalaryCalculatorTest {

    @Test
    fun `calculate Salary returns correctly`() {
        val result = SalaryCalculator.calculate(50000.0, 20000.0, 10000.0, 1800.0, 200.0)
        assertEquals(80000.0, result.first, 0.01)
        assertEquals(2000.0, result.second, 0.01)
        assertEquals(78000.0, result.third, 0.01)
    }

    @Test
    fun `calculate Salary guards negative components and clamps net salary`() {
        val overDeducted = SalaryCalculator.calculate(1000.0, 0.0, 0.0, 2000.0, 200.0)
        assertEquals(1000.0, overDeducted.first, 0.01)
        assertEquals(2200.0, overDeducted.second, 0.01)
        assertEquals(0.0, overDeducted.third, 0.01)

        val zeroGross = SalaryCalculator.calculate(-500.0, 0.0, 0.0, 100.0, 200.0)
        assertEquals(0.0, zeroGross.first, 0.01)
        assertEquals(0.0, zeroGross.third, 0.01)
    }
}
