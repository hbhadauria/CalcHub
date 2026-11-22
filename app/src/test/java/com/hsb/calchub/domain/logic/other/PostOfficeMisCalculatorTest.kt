package com.hsb.calchub.domain.logic.other

import org.junit.Assert.assertEquals
import org.junit.Test

class PostOfficeMisCalculatorTest {

    @Test
    fun `calculate Post Office MIS returns correctly`() {
        val result = PostOfficeMisCalculator.calculate(100000.0, 7.4)
        assertEquals(100000.0, result.first, 0.01)
        assertEquals(616.67, result.second, 1.0)
        assertEquals(37000.0, result.third, 10.0)
    }

    @Test
    fun `calculate Post Office MIS guards negative and zero inputs`() {
        assertEquals(0.0, PostOfficeMisCalculator.calculate(0.0, 7.4).second, 0.01)
        assertEquals(0.0, PostOfficeMisCalculator.calculate(100000.0, -1.0).third, 0.01)
    }
}
