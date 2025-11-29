package com.hsb.calchub.domain.logic

import com.hsb.calchub.domain.logic.investment.*
import com.hsb.calchub.domain.logic.loan.*
import com.hsb.calchub.domain.logic.other.CompoundInterestCalculator
import com.hsb.calchub.domain.logic.other.InflationCalculator
import com.hsb.calchub.domain.logic.other.PostOfficeMisCalculator
import com.hsb.calchub.domain.logic.other.SalaryCalculator
import com.hsb.calchub.domain.logic.other.ScssCalculator
import com.hsb.calchub.domain.logic.other.SimpleInterestCalculator
import com.hsb.calchub.domain.logic.tax.*
import com.hsb.calchub.domain.logic.retirement.*
import com.hsb.calchub.domain.logic.trading.BrokerageCalculator
import com.hsb.calchub.domain.logic.trading.CagrCalculator
import com.hsb.calchub.domain.logic.trading.MarginCalculator
import com.hsb.calchub.domain.logic.trading.StockAverageCalculator
import com.hsb.calchub.domain.logic.trading.XirrCalculator

/**
 * Main Calculator Logic - Delegates to specialized calculator objects
 * This maintains backward compatibility while organizing code better
 */
object CalculatorLogic {

    // Investment Calculators
    fun calculateSIP(monthlyInvestment: Double, expectedReturnRate: Double, timePeriodYears: Double) =
        SipCalculator.calculate(monthlyInvestment, expectedReturnRate, timePeriodYears)

    fun calculateLumpsum(totalInvestment: Double, expectedReturnRate: Double, timePeriodYears: Double) =
        LumpsumCalculator.calculate(totalInvestment, expectedReturnRate, timePeriodYears)

    fun calculateFD(totalInvestment: Double, rateOfInterest: Double, timePeriodYears: Double, compoundingFrequency: Int = 4) =
        FdCalculator.calculate(totalInvestment, rateOfInterest, timePeriodYears, compoundingFrequency)

    fun calculatePPF(yearlyInvestment: Double, interestRate: Double, timePeriodYears: Double = 15.0) =
        PpfCalculator.calculate(yearlyInvestment, interestRate, timePeriodYears)

    fun calculateRD(monthlyDeposit: Double, interestRate: Double, timePeriodYears: Double) =
        RdCalculator.calculate(monthlyDeposit, interestRate, timePeriodYears)

    fun calculateNSC(investment: Double, interestRate: Double = 7.7) =
        NscCalculator.calculate(investment, interestRate)

    fun calculateEPF(monthlyContribution: Double, interestRate: Double = 8.25, timePeriodYears: Double) =
        EpfCalculator.calculate(monthlyContribution, interestRate, timePeriodYears)

    fun calculateSSY(yearlyDeposit: Double, interestRate: Double = 8.2, timePeriodYears: Double = 15.0) =
        SsyCalculator.calculate(yearlyDeposit, interestRate, timePeriodYears)

    fun calculateSWP(totalInvestment: Double, withdrawalPerMonth: Double, expectedReturnRate: Double, timePeriodYears: Double) =
        SwpCalculator.calculate(totalInvestment, withdrawalPerMonth, expectedReturnRate, timePeriodYears)

    // Loan Calculators
    fun calculateEMI(loanAmount: Double, interestRate: Double, tenureYears: Double) =
        EmiCalculator.calculate(loanAmount, interestRate, tenureYears)

    fun calculateHomeLoanEMI(loanAmount: Double, interestRate: Double, tenureYears: Double) =
        EmiCalculator.calculate(loanAmount, interestRate, tenureYears)

    fun calculateCarLoanEMI(loanAmount: Double, interestRate: Double, tenureYears: Double) =
        EmiCalculator.calculate(loanAmount, interestRate, tenureYears)

    fun calculateFlatVsReducing(loanAmount: Double, flatRate: Double, tenureYears: Double) =
        FlatVsReducingCalculator.calculate(loanAmount, flatRate, tenureYears)

    // Retirement Calculators
    fun calculateNPS(monthlyContribution: Double, expectedReturnRate: Double, currentAge: Double, retirementAge: Double = 60.0) =
        NpsCalculator.calculate(monthlyContribution, expectedReturnRate, currentAge, retirementAge)

    fun calculateRetirement(currentAge: Double, retirementAge: Double, monthlyExpenses: Double, inflationRate: Double = 6.0, expectedReturn: Double = 12.0) =
        RetirementCalculator.calculate(currentAge, retirementAge, monthlyExpenses, inflationRate, expectedReturn)

    fun calculateAPY(currentAge: Double, pensionAmount: Double = 5000.0) =
        ApyCalculator.calculate(currentAge, pensionAmount)

    fun calculateGratuity(lastSalary: Double, yearsOfService: Double) =
        GratuityCalculator.calculate(lastSalary, yearsOfService)

    // Tax Calculators
    fun calculateIncomeTax(annualIncome: Double, deductions: Double = 0.0) =
        IncomeTaxCalculator.calculate(annualIncome, deductions)

    fun calculateHRA(basicSalary: Double, hraReceived: Double, rentPaid: Double, isMetroCity: Boolean) =
        HraCalculator.calculate(basicSalary, hraReceived, rentPaid, isMetroCity)

    fun calculateGST(amount: Double, gstRate: Double = 18.0, isInclusive: Boolean = false) =
        GstCalculator.calculate(amount, gstRate, isInclusive)

    fun calculateTDS(amount: Double, tdsRate: Double = 10.0) =
        TdsCalculator.calculate(amount, tdsRate)

    // Trading Calculators
    fun calculateCAGR(initialValue: Double, finalValue: Double, years: Double) =
        CagrCalculator.calculate(initialValue, finalValue, years)

    fun calculateBrokerage(tradeValue: Double, brokerageRate: Double = 0.03, isIntraday: Boolean = false) =
        BrokerageCalculator.calculate(tradeValue, brokerageRate, isIntraday)

    fun calculateMargin(quantity: Double, price: Double, leverage: Double = 5.0) =
        MarginCalculator.calculate(quantity, price, leverage)

    fun calculateStockAverage(firstQuantity: Double, firstPrice: Double, secondQuantity: Double, secondPrice: Double) =
        StockAverageCalculator.calculate(firstQuantity, firstPrice, secondQuantity, secondPrice)

    fun calculateXIRR(investments: List<Double>, returns: Double) =
        XirrCalculator.calculate(investments, returns)

    // Other Calculators
    fun calculateSimpleInterest(principal: Double, rate: Double, timeYears: Double) =
        SimpleInterestCalculator.calculate(principal, rate, timeYears)

    fun calculateCompoundInterest(principal: Double, rate: Double, timeYears: Double, compoundingFrequency: Int = 1) =
        CompoundInterestCalculator.calculate(principal, rate, timeYears, compoundingFrequency)

    fun calculateSalary(basicSalary: Double, hra: Double, otherAllowances: Double, pf: Double, professionalTax: Double = 200.0) =
        SalaryCalculator.calculate(basicSalary, hra, otherAllowances, pf, professionalTax)

    fun calculateInflation(currentPrice: Double, inflationRate: Double, years: Double) =
        InflationCalculator.calculate(currentPrice, inflationRate, years)

    fun calculatePostOfficeMIS(investment: Double, interestRate: Double = 7.4) =
        PostOfficeMisCalculator.calculate(investment, interestRate)

    fun calculateSCSS(investment: Double, interestRate: Double = 8.2) =
        ScssCalculator.calculate(investment, interestRate)
}
