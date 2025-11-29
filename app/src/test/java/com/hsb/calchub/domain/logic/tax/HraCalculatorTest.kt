package com.hsb.calchub.domain.logic.tax

import org.junit.Assert.assertEquals
import org.junit.Test

class HraCalculatorTest {

    @Test
    fun `calculate HRA returns correctly`() {
        val basicSalary = 500000.0
        val hraReceived = 200000.0
        val rentPaid = 150000.0
        val isMetroCity = true
        
        val result = HraCalculator.calculate(basicSalary, hraReceived, rentPaid, isMetroCity)
        
        // 1. Actual HRA = 2,00,000
        // 2. Rent - 10% Basic = 1,50,000 - 50,000 = 1,00,000
        // 3. 50% Basic (Metro) = 2,50,000
        
        // Exemption = min(2L, 1L, 2.5L) = 1,00,000
        // Taxable HRA = 2,00,000 - 1,00,000 = 1,00,000
        
        assertEquals(200000.0, result.first, 0.01) // HRA Received
        assertEquals(100000.0, result.second, 0.01) // Exemption
        assertEquals(100000.0, result.third, 0.01) // Taxable
    }
}
