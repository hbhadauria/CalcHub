package com.hsb.calchub.domain.logic.trading

/**
 * Calculator logic for Stock Price Averaging.
 * Calculates the average price of a stock when bought at different prices.
 */
object StockAverageCalculator {
    /**
     * Calculates Average Stock Price.
     *
     * @param firstQuantity Quantity bought in first tranche.
     * @param firstPrice Price paid in first tranche.
     * @param secondQuantity Quantity bought in second tranche.
     * @param secondPrice Price paid in second tranche.
     * @return A [Triple] containing:
     *  - First: Total Quantity
     *  - Second: Average Price
     *  - Third: Total Investment Amount
     */
    fun calculate(firstQuantity: Double, firstPrice: Double, secondQuantity: Double, secondPrice: Double): Triple<Double, Double, Double> {
        if (!firstQuantity.isFinite() || !firstPrice.isFinite() ||
            !secondQuantity.isFinite() || !secondPrice.isFinite() ||
            firstQuantity < 0.0 || secondQuantity < 0.0 ||
            firstPrice < 0.0 || secondPrice < 0.0 ||
            firstQuantity + secondQuantity <= 0.0
        ) {
            return Triple(0.0, 0.0, 0.0)
        }
        val totalQuantity = firstQuantity + secondQuantity
        val totalInvestment = (firstQuantity * firstPrice) + (secondQuantity * secondPrice)
        val averagePrice = totalInvestment / totalQuantity
        return Triple(totalQuantity, averagePrice, totalInvestment)
    }
}
