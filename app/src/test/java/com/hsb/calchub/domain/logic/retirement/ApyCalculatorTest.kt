package com.hsb.calchub.domain.logic.retirement

import org.junit.Assert.assertEquals
import org.junit.Test

class ApyCalculatorTest {

    @Test
    fun `calculate APY returns official PFRDA contributions across pension slabs`() {
        // Age 20, Pension 5000 -> ₹248/mo, 40 yrs -> ₹119,040 total
        val age20Slab5k = ApyCalculator.calculate(20.0, 5000.0)
        assertEquals(248.0, age20Slab5k.first, 0.01)
        assertEquals(119040.0, age20Slab5k.second, 0.01)
        assertEquals(5000.0, age20Slab5k.third, 0.01)

        // Age 30, Pension 3000 -> ₹347/mo, 30 yrs -> ₹124,920 total
        val age30Slab3k = ApyCalculator.calculate(30.0, 3000.0)
        assertEquals(347.0, age30Slab3k.first, 0.01)
        assertEquals(124920.0, age30Slab3k.second, 0.01)
        assertEquals(3000.0, age30Slab3k.third, 0.01)

        // Snaps intermediate slider pension (2400 -> 2000) and clamps age > 40 to 40
        val snapped = ApyCalculator.calculate(45.0, 2400.0)
        assertEquals(582.0, snapped.first, 0.01)
        assertEquals(2000.0, snapped.third, 0.01)
    }

    @Test
    fun `calculate APY guards against invalid ages and zero pension`() {
        listOf(
            ApyCalculator.calculate(60.0, 5000.0),
            ApyCalculator.calculate(-5.0, 5000.0),
            ApyCalculator.calculate(25.0, 0.0),
            ApyCalculator.calculate(Double.NaN, 5000.0)
        ).forEach {
            assertEquals(0.0, it.first, 0.01)
            assertEquals(0.0, it.second, 0.01)
            assertEquals(0.0, it.third, 0.01)
        }
    }
}
