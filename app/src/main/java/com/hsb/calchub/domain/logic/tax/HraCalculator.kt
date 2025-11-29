package com.hsb.calchub.domain.logic.tax
object HraCalculator {
    fun calculate(basicSalary: Double, hraReceived: Double, rentPaid: Double, isMetroCity: Boolean): Triple<Double, Double, Double> {
        val metroPercent = if (isMetroCity) 0.5 else 0.4
        val exemption1 = hraReceived
        val exemption2 = rentPaid - (0.1 * basicSalary)
        val exemption3 = metroPercent * basicSalary
        val exemption = minOf(exemption1, exemption2.coerceAtLeast(0.0), exemption3)
        val taxableHRA = hraReceived - exemption
        return Triple(hraReceived, exemption, taxableHRA)
    }
}
