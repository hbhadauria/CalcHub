package com.hsb.calchub.domain.logic.tax

/**
 * Calculator logic for Goods and Services Tax (GST).
 * Calculates GST amount and total amount inclusive or exclusive of GST.
 */
object GstCalculator {
    /**
     * Calculates GST.
     *
     * @param amount The base amount or total amount depending on `isInclusive`.
     * @param gstRate The GST rate in percent.
     * @param isInclusive True if the `amount` already includes GST, False otherwise.
     * @return A [Triple] containing:
     *  - First: Original/Base Amount
     *  - Second: GST Amount
     *  - Third: Total Amount (Base + GST)
     */
    fun calculate(amount: Double, gstRate: Double = 18.0, isInclusive: Boolean = false): Triple<Double, Double, Double> {
        if (!amount.isFinite() || !gstRate.isFinite() || amount <= 0.0 || gstRate < 0.0) {
            return Triple(0.0, 0.0, 0.0)
        }
        val divisor = 1.0 + gstRate / 100.0
        if (divisor <= 0.0) {
            return Triple(0.0, 0.0, 0.0)
        }
        return if (isInclusive) {
            val baseAmount = amount / divisor
            val gstAmount = amount - baseAmount
            Triple(baseAmount, gstAmount, amount)
        } else {
            val gstAmount = amount * (gstRate / 100.0)
            val totalAmount = amount + gstAmount
            Triple(amount, gstAmount, totalAmount)
        }
    }
}
