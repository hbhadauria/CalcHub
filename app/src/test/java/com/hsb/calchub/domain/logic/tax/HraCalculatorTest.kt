package com.hsb.calchub.domain.logic.tax

import org.junit.Assert.assertEquals
import org.junit.Test

class HraCalculatorTest {

    @Test
    fun `calculate HRA returns correctly`() {
        val result = HraCalculator.calculate(500000.0, 200000.0, 150000.0, true)
        assertEquals(200000.0, result.first, 0.01)
        assertEquals(100000.0, result.second, 0.01)
        assertEquals(100000.0, result.third, 0.01)

        val nonMetro = HraCalculator.calculate(500000.0, 300000.0, 400000.0, false)
        assertEquals(200000.0, nonMetro.second, 0.01)
        assertEquals(100000.0, nonMetro.third, 0.01)
    }

    @Test
    fun `calculate HRA guards negative and NaN inputs`() {
        val negative = HraCalculator.calculate(-1000.0, -500.0, -200.0, true)
        assertEquals(0.0, negative.first, 0.01)
        assertEquals(0.0, negative.second, 0.01)
        assertEquals(0.0, negative.third, 0.01)

        assertEquals(0.0, HraCalculator.calculate(Double.NaN, 1000.0, 1000.0, true).second, 0.01)
    }
}
