package com.bcasekuritas.mybest.ext.formatter;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class PercentFormatterChart implements IValueFormatter {
    public DecimalFormat mFormat;

    public PercentFormatterChart() {
        mFormat = new DecimalFormat("###.##", DecimalFormatSymbols.getInstance(Locale.US));
        mFormat.setRoundingMode(RoundingMode.CEILING);    }


    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        return mFormat.format((double) value) + " %";
    }
}
