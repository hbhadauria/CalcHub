package com.hsb.calchub.domain.logic.tax

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CapitalGainsCalculatorTest {

    @Test
    fun `equity LTCG exempts 1_25L and taxes remainder at 12_5 percent plus cess`() {
        // Gain = 250,000, taxable = 125,000 -> tax = 125,000 * 12.5% * 1.04 = 16,250
        val res = CapitalGainsCalculator.calculate(300_000.0, 550_000.0, 18, isEquity = true)
        assertTrue(res.isLongTerm)
        assertEquals(250_000.0, res.capitalGain, 0.01)
        assertEquals(125_000.0, res.taxableGain, 0.01)
        assertEquals(16_250.0, res.totalTax, 0.01)
        assertEquals(533_750.0, res.netProceeds, 0.01)
    }

    @Test
    fun `equity STCG taxes full gain at 20 percent plus cess`() {
        val res = CapitalGainsCalculator.calculate(100_000.0, 200_000.0, 6, isEquity = true)
        assertFalse(res.isLongTerm)
        assertEquals(20.0, res.baseRatePercent, 0.01)
        assertEquals(20_800.0, res.totalTax, 0.01)
    }

    @Test
    fun `debt or property uses 24m LTCG cutoff and 30 percent STCG`() {
        val stcg = CapitalGainsCalculator.calculate(100_000.0, 200_000.0, 18, isEquity = false)
        assertFalse(stcg.isLongTerm)
        assertEquals(31_200.0, stcg.totalTax, 0.01)

        val ltcg = CapitalGainsCalculator.calculate(100_000.0, 200_000.0, 24, isEquity = false)
        assertTrue(ltcg.isLongTerm)
        assertEquals(13_000.0, ltcg.totalTax, 0.01)
    }

    @Test
    fun `capital loss and invalid inputs produce zero tax and finite results`() {
        val loss = CapitalGainsCalculator.calculate(200_000.0, 150_000.0, 24, isEquity = true)
        assertEquals(-50_000.0, loss.capitalGain, 0.01)
        assertEquals(0.0, loss.totalTax, 0.01)
        assertEquals(150_000.0, loss.netProceeds, 0.01)

        val invalid = CapitalGainsCalculator.calculate(-1000.0, 5000.0, -3, isEquity = true)
        assertEquals(0.0, invalid.totalTax, 0.01)
    }
}
