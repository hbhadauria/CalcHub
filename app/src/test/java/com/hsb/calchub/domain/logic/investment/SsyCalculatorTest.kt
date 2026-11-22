package com.hsb.calchub.domain.logic.investment

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.pow

class SsyCalculatorTest {

    @Test
    fun `calculate SSY caps deposits at 15 years and compounds through maturity`() {
        val yearlyDeposit = 100000.0
        val interestRate = 8.2

        val at15 = SsyCalculator.calculate(yearlyDeposit, interestRate, 15.0)
        assertEquals(1500000.0, at15.first, 0.01)
        assertTrue(at15.third > at15.first)
        assertEquals(at15.third - at15.first, at15.second, 0.01)

        val at21 = SsyCalculator.calculate(yearlyDeposit, interestRate, 21.0)
        // Deposits stop at 15 years
        assertEquals(1500000.0, at21.first, 0.01)
        // Corpus compounds for the remaining 6 years
        val expectedMaturity21 = at15.third * (1.0 + interestRate / 100.0).pow(6.0)
        assertEquals(expectedMaturity21, at21.third, 0.01)
        assertEquals(at21.third - at21.first, at21.second, 0.01)
    }

    @Test
    fun `calculate SSY handles zero rate and invalid inputs`() {
        val zeroRate = SsyCalculator.calculate(50000.0, 0.0, 21.0)
        assertEquals(750000.0, zeroRate.first, 0.01)
        assertEquals(0.0, zeroRate.second, 0.01)
        assertEquals(750000.0, zeroRate.third, 0.01)

        assertEquals(0.0, SsyCalculator.calculate(0.0, 8.2, 15.0).third, 0.01)
        assertEquals(0.0, SsyCalculator.calculate(50000.0, -1.0, 15.0).third, 0.01)
    }
}
