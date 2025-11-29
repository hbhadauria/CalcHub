package com.hsb.calchub.domain.model

enum class CalculatorCategory {
    POPULAR, // Virtual category for UI logic if needed, but we'll use isPopular flag
    INVESTMENT,
    LOAN,
    TAX,
    RETIREMENT,
    TRADING,
    OTHER
}

data class Calculator(
    val name: String,
    val route: String,
    val category: CalculatorCategory,
    val isPopular: Boolean = false,
    val iconRes: Int? = null
)

val allCalculators = listOf(
    // Investment
    Calculator("SIP", "sip", CalculatorCategory.INVESTMENT, true),
    Calculator("Lumpsum", "lumpsum", CalculatorCategory.INVESTMENT),
    Calculator("FD", "fd", CalculatorCategory.INVESTMENT, true),
    Calculator("PPF", "ppf", CalculatorCategory.INVESTMENT, true),
    Calculator("RD", "rd", CalculatorCategory.INVESTMENT),
    Calculator("SSY", "ssy", CalculatorCategory.INVESTMENT),
    Calculator("EPF", "epf", CalculatorCategory.INVESTMENT),
    Calculator("NSC", "nsc", CalculatorCategory.INVESTMENT),
    Calculator("SWP", "swp", CalculatorCategory.INVESTMENT),
    Calculator("MF Returns", "mf_returns", CalculatorCategory.INVESTMENT),

    // Loan
    Calculator("EMI", "emi", CalculatorCategory.LOAN, true),
    Calculator("Home Loan", "home_loan_emi", CalculatorCategory.LOAN, true),
    Calculator("Car Loan", "car_loan_emi", CalculatorCategory.LOAN),
    Calculator("Flat vs Reducing", "flat_vs_reducing", CalculatorCategory.LOAN),

    // Tax
    Calculator("Income Tax", "income_tax", CalculatorCategory.TAX, true),
    Calculator("GST", "gst", CalculatorCategory.TAX, true),
    Calculator("HRA", "hra", CalculatorCategory.TAX),
    Calculator("TDS", "tds", CalculatorCategory.TAX),

    // Retirement
    Calculator("NPS", "nps", CalculatorCategory.RETIREMENT, true),
    Calculator("Retirement", "retirement", CalculatorCategory.RETIREMENT),
    Calculator("Gratuity", "gratuity", CalculatorCategory.RETIREMENT),
    Calculator("APY", "apy", CalculatorCategory.RETIREMENT),

    // Trading
    Calculator("Brokerage", "brokerage", CalculatorCategory.TRADING),
    Calculator("Stock Average", "stock_average", CalculatorCategory.TRADING),
    Calculator("CAGR", "cagr", CalculatorCategory.TRADING),
    Calculator("XIRR", "xirr", CalculatorCategory.TRADING),
    Calculator("Margin", "margin", CalculatorCategory.TRADING),

    // Other
    Calculator("Simple Interest", "simple_interest", CalculatorCategory.OTHER, true),
    Calculator("Compound Interest", "compound_interest", CalculatorCategory.OTHER),
    Calculator("Salary", "salary", CalculatorCategory.OTHER),
    Calculator("Inflation", "inflation", CalculatorCategory.OTHER),
    Calculator("Post Office MIS", "post_office_mis", CalculatorCategory.OTHER),
    Calculator("SCSS", "scss", CalculatorCategory.OTHER)
)
