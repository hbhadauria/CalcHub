package com.hsb.calchub.domain.logic.investment

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.pow

class RdCalculatorTest {

    @Test
    fun `calculate RD uses quarterly compounding`() {
        val monthlyDeposit = 5000.0
        val interestRate = 6.5
        val timePeriodYears = 5.0

        val result = RdCalculator.calculate(monthlyDeposit, interestRate, timePeriodYears)

        assertEquals(300000.0, result.first, 0.01)
        val expected = (1..60).sumOf { k ->
            monthlyDeposit * (1.0 + interestRate / 100.0 / 4.0).pow(4.0 * k / 12.0)
        }
        assertEquals(expected, result.third, 0.01)
        assertEquals(result.third - result.first, result.second, 0.01)
        // Must exceed legacy simple-interest approximation (349562.5)
        assertTrue(result.third > 354000.0)
    }

    @Test
    fun `calculate RD handles zero rate and invalid inputs`() {
        val zeroRate = RdCalculator.calculate(5000.0, 0.0, 2.0)
        assertEquals(120000.0, zeroRate.first, 0.01)
        assertEquals(0.0, zeroRate.second, 0.01)
        assertEquals(120000.0, zeroRate.third, 0.01)

        val invalid = RdCalculator.calculate(-500.0, 6.5, 5.0)
        assertEquals(0.0, invalid.third, 0.01)
    }
}
