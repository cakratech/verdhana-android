package com.bcasekuritas.mybest.ext.formatter

import com.github.mikephil.charting.formatter.ValueFormatter
import java.math.RoundingMode
import java.text.DecimalFormat

class PercentValueFormatter: ValueFormatter() {

    fun roundOffDecimal(number: Double): Double? {
        val df = DecimalFormat("###.##")
        df.roundingMode = RoundingMode.CEILING

        return df.format(number).toDouble()
    }
}