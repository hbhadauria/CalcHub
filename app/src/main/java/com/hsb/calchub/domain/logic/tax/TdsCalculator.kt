package com.hsb.calchub.domain.logic.tax

/**
 * Calculator logic for Tax Deducted at Source (TDS).
 * Calculates the amount deducted as tax from payments like salary, rent, etc.
 */
object TdsCalculator {
    /**
     * Calculates TDS Amount.
     *
     * @param amount The gross amount.
     * @param tdsRate The TDS rate in percent.
     * @return A [Triple] containing:
     *  - First: Gross Amount
     *  - Second: TDS Amount Deducted
     *  - Third: Net Amount Payable
     */
    fun calculate(amount: Double, tdsRate: Double = 10.0): Triple<Double, Double, Double> {
        if (!amount.isFinite() || !tdsRate.isFinite() || amount <= 0.0 || tdsRate < 0.0) {
            return Triple(0.0, 0.0, 0.0)
        }
        val clampedRate = tdsRate.coerceIn(0.0, 100.0)
        val tdsAmount = amount * (clampedRate / 100.0)
        val netAmount = (amount - tdsAmount).coerceAtLeast(0.0)
        return Triple(amount, tdsAmount, netAmount)
    }
}
