package com.hsb.calchub.domain.logic.trading

/**
 * Calculator logic for XIRR (Extended Internal Rate of Return).
 * Simplified implementation for return percentage calculation.
 */
object XirrCalculator {
    /**
     * Calculates simplified Return Percentage (since full XIRR requires dates).
     *
     * @param investments List of investment amounts.
     * @param returns The final value or returns amount.
     * @return A [Triple] containing:
     *  - First: Total Invested Amount
     *  - Second: Absolute Profit
     *  - Third: Return Percentage
     */
    fun calculate(investments: List<Double>, returns: Double): Triple<Double, Double, Double> {
        if (!returns.isFinite() || returns < 0.0 || investments.isEmpty() ||
            investments.any { !it.isFinite() || it < 0.0 }
        ) {
            return Triple(0.0, 0.0, 0.0)
        }
        val totalInvested = investments.sum()
        if (totalInvested <= 0.0) {
            return Triple(0.0, 0.0, 0.0)
        }
        val profit = returns - totalInvested
        val returnPercent = (profit / totalInvested) * 100.0
        return Triple(totalInvested, profit, returnPercent)
    }
}
