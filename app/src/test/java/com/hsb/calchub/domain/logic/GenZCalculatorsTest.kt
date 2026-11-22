package com.hsb.calchub.domain.logic

import com.hsb.calchub.domain.logic.investment.SpendVsInvestCalculator
import com.hsb.calchub.domain.logic.loan.BuyVsRentCalculator
import com.hsb.calchub.domain.logic.other.SalaryCalculator
import com.hsb.calchub.domain.logic.retirement.FireCalculator
import com.hsb.calchub.domain.logic.tax.FreelanceTaxCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GenZCalculatorsTest {

    @Test
    fun salaryFromCtc_computesTakeHomeWithFy26NewRegimeAndPfCap() {
        val noTaxCtc = CalculatorLogic.calculateSalaryFromCtc(
            annualCtc = 1_200_000.0,
            variableBonusPct = 0.0,
            basicPctOfFixed = 40.0,
            pfMonthlyCap = 1_800.0,
            monthlyProfTax = 200.0
        )
        assertEquals(98_200.0, noTaxCtc.monthlyGross, 0.01)
        assertEquals(1_800.0, noTaxCtc.monthlyPf, 0.01)
        assertEquals(0.0, noTaxCtc.monthlyIncomeTax, 0.01)
        assertEquals(96_200.0, noTaxCtc.monthlyInHand, 0.01)
        assertEquals(96_200.0 * 12.0, noTaxCtc.annualInHand, 0.01)

        val taxedCtc = SalaryCalculator.calculateFromCtc(
            annualCtc = 2_400_000.0,
            variableBonusPct = 10.0,
            basicPctOfFixed = 40.0,
            pfMonthlyCap = 0.0,
            monthlyProfTax = 200.0
        )
        assertEquals(0.0, taxedCtc.monthlyPf, 0.01)
        assertTrue(taxedCtc.monthlyIncomeTax > 10_000.0)
        assertTrue(taxedCtc.monthlyInHand < taxedCtc.monthlyGross)
    }

    @Test
    fun salaryFromCtc_guardsInvalidInputs() {
        val invalid = SalaryCalculator.calculateFromCtc(-100_000.0)
        assertEquals(0.0, invalid.monthlyInHand, 0.01)
        val nan = SalaryCalculator.calculateFromCtc(Double.NaN)
        assertEquals(0.0, nan.annualInHand, 0.01)
    }

    @Test
    fun fireCalculator_computesFireAndCoastFireTargets() {
        val result = CalculatorLogic.calculateFire(
            currentAge = 26,
            monthlyExpenses = 50_000.0,
            currentCorpus = 500_000.0,
            monthlySip = 25_000.0,
            expectedReturnPct = 12.0,
            inflationPct = 6.0,
            safeWithdrawalRatePct = 4.0,
            targetRetirementAge = 60
        )
        assertEquals(15_000_000.0, result.fireNumber, 1.0)
        assertTrue(result.yearsToFire in 15..35)
        assertEquals(26 + result.yearsToFire, result.fireAge)
        assertTrue(result.coastFireNumber in 1_000_000.0..5_000_000.0)
        assertFalse(result.isCoastFireReached)
        assertTrue(result.coastFireGap > 0.0)

        val coasted = FireCalculator.calculate(
            currentAge = 28,
            monthlyExpenses = 50_000.0,
            currentCorpus = 5_000_000.0,
            monthlySip = 0.0,
            expectedReturnPct = 12.0,
            inflationPct = 6.0
        )
        assertTrue(coasted.isCoastFireReached)
        assertEquals(0.0, coasted.coastFireGap, 0.01)

        val alreadyFire = FireCalculator.calculate(
            currentAge = 32,
            monthlyExpenses = 50_000.0,
            currentCorpus = 16_000_000.0,
            monthlySip = 0.0,
            expectedReturnPct = 12.0,
            inflationPct = 6.0
        )
        assertEquals(0, alreadyFire.yearsToFire)
        assertEquals(32, alreadyFire.fireAge)
    }

    @Test
    fun fireCalculator_guardsInvalidInputs() {
        val zeroExp = FireCalculator.calculate(25, 0.0, 100_000.0, 5_000.0, 12.0, 6.0)
        assertEquals(0.0, zeroExp.fireNumber, 0.01)
        val nanSwr = FireCalculator.calculate(25, 40_000.0, 100_000.0, 5_000.0, 12.0, 6.0, Double.NaN)
        assertEquals(0.0, nanSwr.coastFireNumber, 0.01)
    }

    @Test
    fun buyVsRent_comparesHomeEquityAgainstRentAndSip() {
        val rentFavored = CalculatorLogic.calculateBuyVsRent(
            propertyPrice = 10_000_000.0,
            downPaymentPct = 20.0,
            loanRatePct = 9.0,
            tenureYears = 20,
            monthlyRent = 22_000.0,
            rentEscalationPct = 5.0,
            propertyAppreciationPct = 4.0,
            sipReturnPct = 13.0
        )
        assertTrue(rentFavored.monthlyEmi > 70_000.0)
        assertTrue(rentFavored.rentInvestNetWealth > rentFavored.buyNetWealth)
        assertFalse(rentFavored.isBuyingBetter)

        val buyFavored = BuyVsRentCalculator.calculate(
            propertyPrice = 6_000_000.0,
            downPaymentPct = 20.0,
            loanRatePct = 8.0,
            tenureYears = 20,
            monthlyRent = 32_000.0,
            rentEscalationPct = 7.0,
            propertyAppreciationPct = 9.0,
            sipReturnPct = 9.0
        )
        assertTrue(buyFavored.buyNetWealth > buyFavored.rentInvestNetWealth)
        assertTrue(buyFavored.isBuyingBetter)
        assertTrue(buyFavored.wealthDifference > 0.0)
    }

    @Test
    fun buyVsRent_guardsZeroRateAndInvalidInputs() {
        val zeroRate = BuyVsRentCalculator.calculate(5_000_000.0, 100.0, 0.0, 10, 15_000.0, 0.0, 5.0, 10.0)
        assertEquals(0.0, zeroRate.monthlyEmi, 0.01)
        assertTrue(zeroRate.buyNetWealth > 0.0)

        val invalid = BuyVsRentCalculator.calculate(-100.0, 20.0, 8.5, 20, 20_000.0, 5.0, 6.0, 12.0)
        assertEquals(0.0, invalid.buyNetWealth, 0.01)
        val nan = BuyVsRentCalculator.calculate(5_000_000.0, Double.NaN, 8.5, 20, 20_000.0, 5.0, 6.0, 12.0)
        assertEquals(0.0, nan.rentInvestNetWealth, 0.01)
    }

    @Test
    fun spendVsInvest_matchesSipAndAdjustsForInflation() {
        val nominal = CalculatorLogic.calculateSpendVsInvest(
            monthlySpend = 3_000.0,
            expectedReturnPct = 12.0,
            years = 15.0,
            inflationPct = 0.0
        )
        val expectedSip = CalculatorLogic.calculateSIP(3_000.0, 12.0, 15.0)
        assertEquals(expectedSip.first, nominal.totalSpent, 0.01)
        assertEquals(expectedSip.second, nominal.opportunityCostGain, 0.01)
        assertEquals(expectedSip.third, nominal.futureValueIfInvested, 0.01)
        assertEquals(nominal.futureValueIfInvested, nominal.inflationAdjustedValue, 0.01)

        val real = SpendVsInvestCalculator.calculate(3_000.0, 12.0, 15.0, 6.0)
        assertTrue(real.inflationAdjustedValue < real.futureValueIfInvested)

        val invalid = SpendVsInvestCalculator.calculate(Double.POSITIVE_INFINITY, 12.0, 10.0)
        assertEquals(0.0, invalid.futureValueIfInvested, 0.01)
    }

    @Test
    fun freelanceTax_computes44AdaPresumptiveWithoutSalariedStdDeduction() {
        val withinRebate = CalculatorLogic.calculateFreelanceTax(annualGrossReceipts = 2_400_000.0)
        assertEquals(1_200_000.0, withinRebate.presumptiveTaxableIncome, 0.01)
        assertEquals(0.0, withinRebate.estimatedTax, 0.01)
        assertFalse(withinRebate.exceeds44AdaLimit)
        assertTrue(withinRebate.taxSavedVsRegular > 0.0)

        // At 25L gross, presumptive taxable income is 12.5L (> 12L Sec 87A limit).
        // Ensures salaried Rs 75K standard deduction is NOT applied here (marginal relief tax = 52,000).
        val edgeAboveRebate = FreelanceTaxCalculator.calculate(
            annualGrossReceipts = 2_500_000.0,
            billableHoursPerWeek = 25.0,
            workingWeeksPerYear = 50
        )
        assertEquals(1_250_000.0, edgeAboveRebate.presumptiveTaxableIncome, 0.01)
        assertEquals(52_000.0, edgeAboveRebate.estimatedTax, 0.01)
        assertEquals((2_500_000.0 - 52_000.0) / (25.0 * 50.0), edgeAboveRebate.postTaxHourlyRate, 0.01)

        val over44AdaLimit = FreelanceTaxCalculator.calculate(8_000_000.0)
        assertTrue(over44AdaLimit.exceeds44AdaLimit)

        val invalid = FreelanceTaxCalculator.calculate(Double.NaN)
        assertEquals(0.0, invalid.annualTakeHome, 0.01)
    }
}
