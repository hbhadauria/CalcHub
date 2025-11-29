package com.hsb.calchub.domain.logic.retirement
object GratuityCalculator {
    fun calculate(lastSalary: Double, yearsOfService: Double): Triple<Double, Double, Double> {
        val gratuity = (lastSalary * yearsOfService * 15) / 26
        val taxFreeLimit = 2000000.0
        val taxableAmount = (gratuity - taxFreeLimit).coerceAtLeast(0.0)
        return Triple(gratuity, taxFreeLimit.coerceAtMost(gratuity), taxableAmount)
    }
}
