package com.hsb.calchub.domain.logic.tax

import com.hsb.calchub.domain.logic.CalculatorLogic
import org.junit.Assert.assertEquals
import org.junit.Test

class IncomeTaxCalculatorTest {

    @Test
    fun `calculate Old Regime Income Tax includes Sec 87A rebate and 4 percent cess`() {
        // Above 5L taxable -> slab tax 172,500 + 4% cess = 179,400
        val high = IncomeTaxCalculator.calculate(1200000.0, 0.0)
        assertEquals(1200000.0, high.first, 0.01)
        assertEquals(179400.0, high.second, 0.01)
        assertEquals(1020600.0, high.third, 0.01)

        // Sec 87A rebate at taxable <= 5L -> 0 tax
        val rebate = IncomeTaxCalculator.calculate(700000.0, 200000.0)
        assertEquals(0.0, rebate.second, 0.01)
        assertEquals(700000.0, rebate.third, 0.01)
    }

    @Test
    fun `calculate FY26 New Regime applies 75K std deduction, 87A rebate, marginal relief, and cess`() {
        // 12.75L gross -> 12L taxable -> 0 tax
        val exempt = CalculatorLogic.calculateIncomeTaxNewRegimeFy26(1_275_000.0)
        assertEquals(0.0, exempt.second, 0.01)
        assertEquals(1_275_000.0, exempt.third, 0.01)

        // 12.85L gross -> 12.10L taxable -> marginal relief caps slab tax at 10,000 + 4% cess = 10,400
        val marginal = CalculatorLogic.calculateIncomeTaxNewRegimeFy26(1_285_000.0)
        assertEquals(10_400.0, marginal.second, 0.01)

        // 18L gross -> 17.25L taxable -> slab tax 145,000 + 4% cess = 150,800
        val sample18L = CalculatorLogic.calculateIncomeTaxNewRegimeFy26(1_800_000.0)
        assertEquals(150_800.0, sample18L.second, 0.01)
        assertEquals(1_649_200.0, sample18L.third, 0.01)
    }

    @Test
    fun `calculate Income Tax guards zero negative and NaN inputs`() {
        assertEquals(0.0, IncomeTaxCalculator.calculate(0.0, 0.0).second, 0.01)
        assertEquals(0.0, IncomeTaxCalculator.calculate(-500000.0, 0.0).first, 0.01)
        assertEquals(0.0, IncomeTaxCalculator.calculateFy26NewRegime(-1000.0).second, 0.01)
        assertEquals(0.0, IncomeTaxCalculator.calculateFy26NewRegime(Double.NaN).second, 0.01)
    }
}
