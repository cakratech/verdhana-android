package com.bcasekuritas.mybest.widget.view

import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.domain.dto.response.DataChart
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import java.text.SimpleDateFormat

class CustomMarkerView(
    context: Context,
    layout: Int,
    private val dataToDisplay: MutableList<DataChart>
) : MarkerView(context, layout) {

    private var txtViewData: TextView? = null
    private var txtViewDate: TextView? = null

    init {
        txtViewData = findViewById(R.id.txtViewData)
        txtViewDate = findViewById(R.id.txtDate)

    }

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        try {
            val item = e?.x?.toInt() ?: 0

            val formatDate = if (dataToDisplay[item-1].isHoursFormat) SimpleDateFormat("dd MMM yyyy, HH:mm") else SimpleDateFormat("dd MMM yyyy")
            txtViewData?.text = dataToDisplay[item-1].price.formatPriceWithoutDecimal()
            txtViewDate?.text = dataToDisplay[item-1].date.let { formatDate.format(it) }
        } catch (_: IndexOutOfBoundsException) { }

        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF(-(width / 2f), -height.toFloat())
    }
}