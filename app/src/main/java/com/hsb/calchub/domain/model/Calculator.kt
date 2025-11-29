package com.hsb.calchub.domain.model

data class Calculator(
    val name: String,
    val route: String,
    val iconRes: Int? = null // Placeholder for now
)

val allCalculators = listOf(
    Calculator("SIP", "sip"),
    Calculator("Lumpsum", "lumpsum"),
    Calculator("SWP", "swp"),
    Calculator("MF Returns", "mf_returns"),
    Calculator("SSY", "ssy"),
    Calculator("PPF", "ppf"),
    Calculator("EPF", "epf"),
    Calculator("FD", "fd"),
    Calculator("RD", "rd"),
    Calculator("NPS", "nps"),
    Calculator("HRA", "hra"),
    Calculator("Retirement", "retirement"),
    Calculator("EMI", "emi"),
    Calculator("Car Loan EMI", "car_loan_emi"),
    Calculator("Home Loan EMI", "home_loan_emi"),
    Calculator("Simple Interest", "simple_interest"),
    Calculator("Compound Interest", "compound_interest"),
    Calculator("NSC", "nsc"),
    Calculator("Step Up SIP", "step_up_sip"),
    Calculator("Income Tax", "income_tax"),
    Calculator("Gratuity", "gratuity"),
    Calculator("APY", "apy"),
    Calculator("CAGR", "cagr"),
    Calculator("GST", "gst"),
    Calculator("Flat vs Reducing", "flat_vs_reducing"),
    Calculator("Brokerage", "brokerage"),
    Calculator("Margin", "margin"),
    Calculator("TDS", "tds"),
    Calculator("Salary", "salary"),
    Calculator("Inflation", "inflation"),
    Calculator("Post Office MIS", "post_office_mis"),
    Calculator("SCSS", "scss"),
    Calculator("Stock Average", "stock_average"),
    Calculator("XIRR", "xirr")
)
