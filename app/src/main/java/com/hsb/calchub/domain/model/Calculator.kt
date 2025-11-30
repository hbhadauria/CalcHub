package com.hsb.calchub.domain.model

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*

enum class CalculatorCategory {
    POPULAR,
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
    val icon: ImageVector = Icons.Rounded.Calculate // Default icon
)

val allCalculators = listOf(
    // Investment
    Calculator("SIP", "sip", CalculatorCategory.INVESTMENT, true, Icons.Rounded.TrendingUp),
    Calculator("Lumpsum", "lumpsum", CalculatorCategory.INVESTMENT, false, Icons.Rounded.PieChart),
    Calculator("FD", "fd", CalculatorCategory.INVESTMENT, true, Icons.Rounded.Savings),
    Calculator("PPF", "ppf", CalculatorCategory.INVESTMENT, true, Icons.Rounded.AccountBalance),
    Calculator("RD", "rd", CalculatorCategory.INVESTMENT, false, Icons.Rounded.Restore),
    Calculator("SSY", "ssy", CalculatorCategory.INVESTMENT, false, Icons.Rounded.ChildCare),
    Calculator("EPF", "epf", CalculatorCategory.INVESTMENT, false, Icons.Rounded.Work),
    Calculator("NSC", "nsc", CalculatorCategory.INVESTMENT, false, Icons.Rounded.LocalPostOffice),
    Calculator("SWP", "swp", CalculatorCategory.INVESTMENT, false, Icons.Rounded.Output),
    Calculator("MF Returns", "mf_returns", CalculatorCategory.INVESTMENT, false, Icons.Rounded.ShowChart),

    // Loan
    Calculator("EMI", "emi", CalculatorCategory.LOAN, true, Icons.Rounded.Calculate),
    Calculator("Home Loan", "home_loan_emi", CalculatorCategory.LOAN, true, Icons.Rounded.Home),
    Calculator("Car Loan", "car_loan_emi", CalculatorCategory.LOAN, false, Icons.Rounded.DirectionsCar),
    Calculator("Flat vs Reducing", "flat_vs_reducing", CalculatorCategory.LOAN, false, Icons.Rounded.CompareArrows),

    // Tax
    Calculator("Income Tax", "income_tax", CalculatorCategory.TAX, true, Icons.Rounded.Description),
    Calculator("GST", "gst", CalculatorCategory.TAX, true, Icons.Rounded.ReceiptLong),
    Calculator("HRA", "hra", CalculatorCategory.TAX, false, Icons.Rounded.House),
    Calculator("TDS", "tds", CalculatorCategory.TAX, false, Icons.Rounded.ContentCut),

    // Retirement
    Calculator("NPS", "nps", CalculatorCategory.RETIREMENT, true, Icons.Rounded.Elderly),
    Calculator("Retirement", "retirement", CalculatorCategory.RETIREMENT, false, Icons.Rounded.BeachAccess),
    Calculator("Gratuity", "gratuity", CalculatorCategory.RETIREMENT, false, Icons.Rounded.CardGiftcard),
    Calculator("APY", "apy", CalculatorCategory.RETIREMENT, false, Icons.Rounded.Security),

    // Trading
    Calculator("Brokerage", "brokerage", CalculatorCategory.TRADING, false, Icons.Rounded.Percent),
    Calculator("Stock Average", "stock_average", CalculatorCategory.TRADING, false, Icons.Rounded.Functions),
    Calculator("CAGR", "cagr", CalculatorCategory.TRADING, false, Icons.Rounded.Timeline),
    Calculator("XIRR", "xirr", CalculatorCategory.TRADING, false, Icons.Rounded.AutoGraph),
    Calculator("Margin", "margin", CalculatorCategory.TRADING, false, Icons.Rounded.AttachMoney),

    // Other
    Calculator("Simple Interest", "simple_interest", CalculatorCategory.OTHER, true, Icons.Rounded.Percent),
    Calculator("Compound Interest", "compound_interest", CalculatorCategory.OTHER, false, Icons.Rounded.Loop),
    Calculator("Salary", "salary", CalculatorCategory.OTHER, false, Icons.Rounded.AttachMoney),
    Calculator("Inflation", "inflation", CalculatorCategory.OTHER, false, Icons.Rounded.TrendingDown),
    Calculator("Post Office MIS", "post_office_mis", CalculatorCategory.OTHER, false, Icons.Rounded.LocalPostOffice),
    Calculator("SCSS", "scss", CalculatorCategory.OTHER, false, Icons.Rounded.ElderlyWoman)
)
