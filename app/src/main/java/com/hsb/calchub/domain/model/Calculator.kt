package com.hsb.calchub.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.CompareArrows
import androidx.compose.material.icons.automirrored.rounded.ReceiptLong
import androidx.compose.material.icons.automirrored.rounded.ShowChart
import androidx.compose.material.icons.automirrored.rounded.TrendingDown
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Assessment
import androidx.compose.material.icons.rounded.AutoGraph
import androidx.compose.material.icons.rounded.Balance
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.BeachAccess
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.ContentCut
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.CreditScore
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.Elderly
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.FastForward
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.Functions
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material.icons.rounded.LocalPostOffice
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Loop
import androidx.compose.material.icons.rounded.Output
import androidx.compose.material.icons.rounded.Percent
import androidx.compose.material.icons.rounded.PieChart
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.Savings
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.ShoppingBasket
import androidx.compose.material.icons.rounded.SwapVert
import androidx.compose.material.icons.rounded.Timeline
import androidx.compose.material.icons.rounded.Whatshot
import androidx.compose.material.icons.rounded.Work
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.ui.graphics.vector.ImageVector

enum class CalculatorCategory {
    POPULAR,
    INVESTMENT,
    LOAN,
    TAX,
    RETIREMENT,
    TRADING,
    OTHER
}

/**
 * Unified Calculator Registry entry driving Home discovery, Navigation 3 routing,
 * screen-scoped category accents, and Formulas Lab across all 42 calculators.
 */
data class Calculator(
    val name: String,
    val route: String,
    val category: CalculatorCategory,
    val isPopular: Boolean = false,
    val icon: ImageVector = Icons.Rounded.Calculate,
    val subtitle: String = "",
    val sampleValue: String = "",
    val formula: String = ""
)

val allCalculators: List<Calculator> = listOf(
    // Investment (12)
    Calculator(
        name = "SIP",
        route = "sip",
        category = CalculatorCategory.INVESTMENT,
        isPopular = true,
        icon = Icons.AutoMirrored.Rounded.TrendingUp,
        subtitle = "Monthly wealth builder",
        sampleValue = "₹50.46 L",
        formula = "FV = P × ((1+i)^n − 1)/i × (1+i)"
    ),
    Calculator(
        name = "Step-up SIP",
        route = "step_up_sip",
        category = CalculatorCategory.INVESTMENT,
        isPopular = true,
        icon = Icons.Rounded.BarChart,
        subtitle = "Annual top-up growth",
        sampleValue = "₹84.90 L",
        formula = "Annual +10% SIP compounding"
    ),
    Calculator(
        name = "Lumpsum",
        route = "lumpsum",
        category = CalculatorCategory.INVESTMENT,
        isPopular = false,
        icon = Icons.Rounded.Savings,
        subtitle = "One-time mutual fund",
        sampleValue = "₹5.47 L",
        formula = "FV = P × (1 + r)^t"
    ),
    Calculator(
        name = "FD",
        route = "fd",
        category = CalculatorCategory.INVESTMENT,
        isPopular = true,
        icon = Icons.Rounded.Lock,
        subtitle = "Fixed deposit maturity",
        sampleValue = "₹1.41 L",
        formula = "A = P × (1 + r/4)^(4t)"
    ),
    Calculator(
        name = "PPF",
        route = "ppf",
        category = CalculatorCategory.INVESTMENT,
        isPopular = true,
        icon = Icons.Rounded.Shield,
        subtitle = "15-yr tax-free lock",
        sampleValue = "₹40.68 L",
        formula = "Annual compounding @ 7.1%"
    ),
    Calculator(
        name = "RD",
        route = "rd",
        category = CalculatorCategory.INVESTMENT,
        isPopular = false,
        icon = Icons.Rounded.Repeat,
        subtitle = "Recurring bank deposit",
        sampleValue = "₹7.15 L",
        formula = "Quarterly compounded monthly RD"
    ),
    Calculator(
        name = "SSY",
        route = "ssy",
        category = CalculatorCategory.INVESTMENT,
        isPopular = false,
        icon = Icons.Rounded.School,
        subtitle = "Sukanya Samriddhi",
        sampleValue = "₹69.27 L",
        formula = "15y deposit · 21y @ 8.2%"
    ),
    Calculator(
        name = "EPF",
        route = "epf",
        category = CalculatorCategory.INVESTMENT,
        isPopular = false,
        icon = Icons.Rounded.Work,
        subtitle = "Provident fund corpus",
        sampleValue = "₹1.82 Cr",
        formula = "Employee + Employer @ 8.25%"
    ),
    Calculator(
        name = "NSC",
        route = "nsc",
        category = CalculatorCategory.INVESTMENT,
        isPopular = false,
        icon = Icons.Rounded.WorkspacePremium,
        subtitle = "5-yr savings cert",
        sampleValue = "₹1.45 L",
        formula = "A = P × (1 + 7.7%)^5"
    ),
    Calculator(
        name = "SWP",
        route = "swp",
        category = CalculatorCategory.INVESTMENT,
        isPopular = false,
        icon = Icons.Rounded.Output,
        subtitle = "Systematic withdrawal",
        sampleValue = "₹25k/mo",
        formula = "Remaining balance after monthly payout"
    ),
    Calculator(
        name = "MF Returns",
        route = "mf_returns",
        category = CalculatorCategory.INVESTMENT,
        isPopular = false,
        icon = Icons.Rounded.PieChart,
        subtitle = "Absolute & CAGR check",
        sampleValue = "+14.8%",
        formula = "Total return & annualized CAGR"
    ),
    Calculator(
        name = "Goal SIP",
        route = "goal_sip",
        category = CalculatorCategory.INVESTMENT,
        isPopular = true,
        icon = Icons.Rounded.Flag,
        subtitle = "Target corpus planner",
        sampleValue = "₹20k/mo",
        formula = "SIP = Goal / (((1+i)^n − 1)/i × (1+i))"
    ),
    Calculator(
        name = "Spend vs. Invest",
        route = "spend_vs_invest",
        category = CalculatorCategory.INVESTMENT,
        isPopular = true,
        icon = Icons.Rounded.ShoppingBasket,
        subtitle = "Daily spend opportunity cost",
        sampleValue = "₹14.9 L",
        formula = "Future SIP value of recurring monthly spend"
    ),

    // Loan (7)
    Calculator(
        name = "EMI",
        route = "emi",
        category = CalculatorCategory.LOAN,
        isPopular = true,
        icon = Icons.Rounded.CreditCard,
        subtitle = "Monthly loan schedule",
        sampleValue = "₹21,247",
        formula = "EMI = P×r×(1+r)^n / ((1+r)^n − 1)"
    ),
    Calculator(
        name = "Home Loan",
        route = "home_loan_emi",
        category = CalculatorCategory.LOAN,
        isPopular = true,
        icon = Icons.Rounded.Home,
        subtitle = "20-yr housing planner",
        sampleValue = "₹43,391",
        formula = "Reducing EMI + prepay savings"
    ),
    Calculator(
        name = "Car Loan",
        route = "car_loan_emi",
        category = CalculatorCategory.LOAN,
        isPopular = false,
        icon = Icons.Rounded.DirectionsCar,
        subtitle = "Auto EMI & interest",
        sampleValue = "₹16,679",
        formula = "Amortized monthly auto EMI"
    ),
    Calculator(
        name = "Flat vs Reducing",
        route = "flat_vs_reducing",
        category = CalculatorCategory.LOAN,
        isPopular = false,
        icon = Icons.Rounded.Balance,
        subtitle = "Compare true APR",
        sampleValue = "Save ₹64k",
        formula = "True effective reducing interest rate"
    ),
    Calculator(
        name = "Loan Prepayment",
        route = "loan_prepayment",
        category = CalculatorCategory.LOAN,
        isPopular = false,
        icon = Icons.Rounded.FastForward,
        subtitle = "Extra EMI & lump-sum savings",
        sampleValue = "Save ₹8.4 L",
        formula = "Prepay principal to cut tenure & interest"
    ),
    Calculator(
        name = "Credit Card EMI",
        route = "credit_card_emi",
        category = CalculatorCategory.LOAN,
        isPopular = false,
        icon = Icons.Rounded.CreditScore,
        subtitle = "Fixed vs 5% min-due trap",
        sampleValue = "12 mo",
        formula = "Monthly APR interest vs min-due payoff"
    ),
    Calculator(
        name = "Buy vs. Rent",
        route = "buy_vs_rent",
        category = CalculatorCategory.LOAN,
        isPopular = true,
        icon = Icons.Rounded.Balance,
        subtitle = "Home equity vs rent SIP",
        sampleValue = "20-yr split",
        formula = "Home equity vs Down payment + surplus SIP"
    ),

    // Tax (6)
    Calculator(
        name = "Income Tax",
        route = "income_tax",
        category = CalculatorCategory.TAX,
        isPopular = true,
        icon = Icons.Rounded.Description,
        subtitle = "Old vs New Regime",
        sampleValue = "₹1.51 L",
        formula = "FY26 tax slabs + 4% health cess"
    ),
    Calculator(
        name = "GST",
        route = "gst",
        category = CalculatorCategory.TAX,
        isPopular = true,
        icon = Icons.AutoMirrored.Rounded.ReceiptLong,
        subtitle = "Inclusive / Exclusive",
        sampleValue = "18% Slab",
        formula = "CGST + SGST price breakdown"
    ),
    Calculator(
        name = "HRA",
        route = "hra",
        category = CalculatorCategory.TAX,
        isPopular = false,
        icon = Icons.Rounded.Key,
        subtitle = "Rent exemption calc",
        sampleValue = "₹2.40 L",
        formula = "Min(HRA, Rent−10% Basic, 50% Basic)"
    ),
    Calculator(
        name = "TDS",
        route = "tds",
        category = CalculatorCategory.TAX,
        isPopular = false,
        icon = Icons.Rounded.ContentCut,
        subtitle = "Deduction at source",
        sampleValue = "10% Sec",
        formula = "TDS = Gross Payment × Rate %"
    ),
    Calculator(
        name = "Capital Gains",
        route = "capital_gains",
        category = CalculatorCategory.TAX,
        isPopular = false,
        icon = Icons.Rounded.Assessment,
        subtitle = "FY26 LTCG & STCG tax",
        sampleValue = "12.5% LTCG",
        formula = "LTCG (₹1.25L exempt) / STCG + 4% cess"
    ),
    Calculator(
        name = "Freelance Tax",
        route = "freelance_tax",
        category = CalculatorCategory.TAX,
        isPopular = false,
        icon = Icons.Rounded.Work,
        subtitle = "Sec 44ADA & hourly rate",
        sampleValue = "50% Profit",
        formula = "Taxable = 50% Receipts (Sec 44ADA)"
    ),

    // Retirement (5)
    Calculator(
        name = "NPS",
        route = "nps",
        category = CalculatorCategory.RETIREMENT,
        isPopular = true,
        icon = Icons.Rounded.Security,
        subtitle = "Pension + annuity split",
        sampleValue = "₹1.48 Cr",
        formula = "60% lump sum + 40% annuity corpus"
    ),
    Calculator(
        name = "Retirement",
        route = "retirement",
        category = CalculatorCategory.RETIREMENT,
        isPopular = false,
        icon = Icons.Rounded.BeachAccess,
        subtitle = "FIRE & life timeline",
        sampleValue = "₹7.73 Cr",
        formula = "25× inflation-adjusted annual expense"
    ),
    Calculator(
        name = "Gratuity",
        route = "gratuity",
        category = CalculatorCategory.RETIREMENT,
        isPopular = false,
        icon = Icons.Rounded.EmojiEvents,
        subtitle = "15/26 tenure formula",
        sampleValue = "₹8.65 L",
        formula = "(15 × Basic+DA × Years) / 26"
    ),
    Calculator(
        name = "APY",
        route = "apy",
        category = CalculatorCategory.RETIREMENT,
        isPopular = false,
        icon = Icons.Rounded.AccountBalance,
        subtitle = "Atal Pension Yojana",
        sampleValue = "₹5,000/mo",
        formula = "Guaranteed pension from age 60"
    ),
    Calculator(
        name = "FIRE / Coast-FIRE",
        route = "fire",
        category = CalculatorCategory.RETIREMENT,
        isPopular = true,
        icon = Icons.Rounded.Whatshot,
        subtitle = "Independence & Coast age",
        sampleValue = "25× Exp",
        formula = "FIRE = (12×Expense)/SWR; Coast = FIRE/(1+r)^t"
    ),

    // Trading (5)
    Calculator(
        name = "Brokerage",
        route = "brokerage",
        category = CalculatorCategory.TRADING,
        isPopular = false,
        icon = Icons.Rounded.SwapVert,
        subtitle = "STT, exchange & net P&L",
        sampleValue = "₹42.60",
        formula = "Net = P&L − (Brokerage + STT + GST)"
    ),
    Calculator(
        name = "Stock Average",
        route = "stock_average",
        category = CalculatorCategory.TRADING,
        isPopular = false,
        icon = Icons.Rounded.Functions,
        subtitle = "Average down cost",
        sampleValue = "₹412.50",
        formula = "Avg = (Q1×P1 + Q2×P2) / (Q1 + Q2)"
    ),
    Calculator(
        name = "CAGR",
        route = "cagr",
        category = CalculatorCategory.TRADING,
        isPopular = false,
        icon = Icons.Rounded.Timeline,
        subtitle = "Annualized return %",
        sampleValue = "16.4%",
        formula = "CAGR = (End / Start)^(1/t) − 1"
    ),
    Calculator(
        name = "XIRR",
        route = "xirr",
        category = CalculatorCategory.TRADING,
        isPopular = false,
        icon = Icons.Rounded.AutoGraph,
        subtitle = "Irregular cashflow IRR",
        sampleValue = "15.2%",
        formula = "NPV(cashflows, dates, rate) = 0"
    ),
    Calculator(
        name = "Margin",
        route = "margin",
        category = CalculatorCategory.TRADING,
        isPopular = false,
        icon = Icons.Rounded.Bolt,
        subtitle = "Leverage & buying power",
        sampleValue = "5× Lever",
        formula = "Exposure = Capital × Multiplier"
    ),

    // Other (6)
    Calculator(
        name = "Simple Interest",
        route = "simple_interest",
        category = CalculatorCategory.OTHER,
        isPopular = false,
        icon = Icons.Rounded.Percent,
        subtitle = "Linear principal gain",
        sampleValue = "₹1.30 L",
        formula = "SI = (P × R × T) / 100"
    ),
    Calculator(
        name = "Compound Interest",
        route = "compound_interest",
        category = CalculatorCategory.OTHER,
        isPopular = false,
        icon = Icons.Rounded.Loop,
        subtitle = "Exponential growth",
        sampleValue = "₹1.61 L",
        formula = "A = P × (1 + r/n)^(n×t)"
    ),
    Calculator(
        name = "Salary",
        route = "salary",
        category = CalculatorCategory.OTHER,
        isPopular = true,
        icon = Icons.Rounded.AccountBalanceWallet,
        subtitle = "CTC to monthly in-hand",
        sampleValue = "₹1.14 L",
        formula = "CTC − PF − Prof Tax − Income Tax"
    ),
    Calculator(
        name = "Inflation",
        route = "inflation",
        category = CalculatorCategory.OTHER,
        isPopular = false,
        icon = Icons.AutoMirrored.Rounded.TrendingDown,
        subtitle = "Purchasing power loss",
        sampleValue = "₹1.79 L",
        formula = "Future Cost = Present × (1 + inf)^t"
    ),
    Calculator(
        name = "Post Office MIS",
        route = "post_office_mis",
        category = CalculatorCategory.OTHER,
        isPopular = false,
        icon = Icons.Rounded.LocalPostOffice,
        subtitle = "Monthly govt payout",
        sampleValue = "₹5,550/mo",
        formula = "Monthly = (Deposit × 7.4%) / 12"
    ),
    Calculator(
        name = "SCSS",
        route = "scss",
        category = CalculatorCategory.OTHER,
        isPopular = false,
        icon = Icons.Rounded.Favorite,
        subtitle = "Senior citizen 8.2%",
        sampleValue = "₹61,500/q",
        formula = "Quarterly = (Deposit × 8.2%) / 4"
    )
)

fun findCalculatorByRoute(route: String): Calculator =
    allCalculators.firstOrNull { it.route == route } ?: allCalculators.first()

fun categoryForRoute(route: String): CalculatorCategory =
    findCalculatorByRoute(route).category
