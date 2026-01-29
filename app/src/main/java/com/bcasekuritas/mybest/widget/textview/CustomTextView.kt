package com.bcasekuritas.mybest.widget.textview

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.bcasekuritas.mybest.R

class CustomTextView : AppCompatTextView {


    constructor(context: Context?) : super(context!!)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomTextView)
        val txtStyle = typedArray.getString(R.styleable.CustomTextView_txtStyle)
        val txtFor = typedArray.getString(R.styleable.CustomTextView_txtFor)
        val txtColor = typedArray.getString(R.styleable.CustomTextView_txtColor)
        typedArray.recycle()

        // font type
        val fontDefault = ResourcesCompat.getFont(context, R.font.figtree)
        val fontSoftBold = ResourcesCompat.getFont(context, R.font.figtree_sb)
        val fontBold = ResourcesCompat.getFont(context, R.font.figtree_b)
        val italic = ResourcesCompat.getFont(context, R.font.figtree)?.let { Typeface.create(it, Typeface.ITALIC) }

        // color list
        val red = ContextCompat.getColor(context, R.color.textDown)
        val green = ContextCompat.getColor(context, R.color.textUp)
        val lightGreen = ContextCompat.getColor(context, R.color.textUpHeader)
        val lightRed = ContextCompat.getColor(context, R.color.textDownHeader)
        val white = ContextCompat.getColor(context, R.color.textWhite)
        val chart = ContextCompat.getColor(context, R.color.textChart)
        val blue = ContextCompat.getColor(context, R.color.textSecondaryBluey)
        val blueWhite = ContextCompat.getColor(context, R.color.brandPrimaryBCABlue)
        val grey = ContextCompat.getColor(context, R.color.textSecondary)
        val black = ContextCompat.getColor(context, R.color.black)
        val darkOrange = ContextCompat.getColor(context, R.color.orange_stroke_info)
        val blueGreen = ContextCompat.getColor(context, R.color.calDividen)
        val midBlue = ContextCompat.getColor(context, R.color.calFinancialReport)
        val purple = ContextCompat.getColor(context, R.color.calEIPO)
        val yellow = ContextCompat.getColor(context, R.color.yellow)
        val secondaryGrey = ContextCompat.getColor(context, R.color.textSecondaryGrey)
        val noChanges = ContextCompat.getColor(context, R.color.noChanges)
        val alwaysBlack = ContextCompat.getColor(context, R.color.black)

        //Color List Calendar
        val dividend = ContextCompat.getColor(context, R.color.calDividen)
        val rups = ContextCompat.getColor(context, R.color.calRUPS)
        val eipo = ContextCompat.getColor(context, R.color.calEIPO)
        val pubExp = ContextCompat.getColor(context, R.color.calPubExp)
        val bonus = ContextCompat.getColor(context, R.color.calBonus)
        val stockSplit = ContextCompat.getColor(context, R.color.calStockSplit)
        val reverseSplit = ContextCompat.getColor(context, R.color.calReverseSplit)
        val rightIssue = ContextCompat.getColor(context, R.color.calRightIssue)
        val warrant = ContextCompat.getColor(context, R.color.calWarrant)

        // set default font and color
        typeface = fontDefault
        setTextColor(ContextCompat.getColor(context, R.color.txtBlackWhite))

        when(txtColor){
            "red" -> setTextColor(red)
            "green" -> setTextColor(green)
            "lightGreen" -> setTextColor(lightGreen)
            "lightRed" -> setTextColor(lightRed)
            "white" -> setTextColor(white)
            "blue" -> setTextColor(blue)
            "grey" -> setTextColor(grey)
            "black" -> setTextColor(black)
            "blueGreen" -> setTextColor(blueGreen)
            "midBlue" -> setTextColor(midBlue)
            "purple" -> setTextColor(purple)
            "yellow" -> setTextColor(yellow)
            "chart" -> setTextColor(chart)
            "blueWhite" -> setTextColor(blueWhite)
            "dividend" -> setTextColor(dividend)
            "rups" -> setTextColor(rups)
            "eipo" -> setTextColor(eipo)
            "pubExp" -> setTextColor(pubExp)
            "bonus" -> setTextColor(bonus)
            "stockSplit" -> setTextColor(stockSplit)
            "reverseSplit" -> setTextColor(reverseSplit)
            "rightIssue" -> setTextColor(rightIssue)
            "warrant" -> setTextColor(warrant)
            "noChanges" -> setTextColor(noChanges)
            "darkOrange" -> setTextColor(darkOrange)
            "secondaryGrey" -> setTextColor(secondaryGrey)
            "alwaysBlack" -> setTextColor(alwaysBlack)
        }

        when(txtStyle){
            "softBold" -> typeface = fontSoftBold
            "bold" -> typeface = fontBold
            "italic" -> typeface = italic
        }

        when(txtFor) {
            // New Start
            "h1" -> setTextSize(TypedValue.COMPLEX_UNIT_DIP, 36f)
            "h2" -> setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30f)
            "h3" -> setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24f)
            "h4" -> setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20f)
            "title" -> setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
            "body" -> setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14f)
            "caption" -> setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12f)
            // New End

            "big" -> {
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18f)
            }
            "bigBold" -> {
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18f)
            }
            "header" -> {
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
            }
            "title" -> {
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
            }
            "desc" -> {
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14f)
            }
            "normalBold" -> {
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14f)
            }
            "small" -> {
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12f)
            }
            "smallBold" -> {
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12f)
            }
            "tiny" -> {
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10f)
            }
            "tinyBold" -> {
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10f)
            }
            "dashboardTitle" -> {
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 36f)
            }
            "aboutTitle" -> {
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 32f)
            }
        }
    }

    fun setTextStyle(string: String){

        val fontDefault = ResourcesCompat.getFont(context, R.font.figtree)
        val fontSoftBold = ResourcesCompat.getFont(context, R.font.figtree_sb)
        val fontBold = ResourcesCompat.getFont(context, R.font.figtree_b)
        val italic = ResourcesCompat.getFont(context, R.font.figtree)?.let { Typeface.create(it, Typeface.ITALIC) }
        when(string){
            "softBold" -> typeface = fontSoftBold
            "bold" -> typeface = fontBold
            "italic" -> typeface = italic
            "default" -> typeface = fontDefault
        }
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {

    }

//    init {
//        // Custom Attributes
//        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomTextView)
//        val txtFor = typedArray.getString(R.styleable.CustomTextView_txtFor)
//        typedArray.recycle()
//
//        // custom font
//        val fontDefault = ResourcesCompat.getFont(context, R.font.figtree)
//        val fontSoftBold = ResourcesCompat.getFont(context, R.font.figtree_sb)
//        val fontBold = ResourcesCompat.getFont(context, R.font.figtree_b)
//
//
//        when(txtFor) {
//            "big" -> {
//                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18f)
//                typeface = fontDefault
//            }
//            "bigBold" -> {
//                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18f)
//                typeface = fontSoftBold
//            }
//            "header" -> {
//                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
//                typeface = fontBold
//            }
//            "title" -> {
//                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
//                typeface = fontSoftBold
//            }
//            "desc" -> {
//                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14f)
//                typeface = fontDefault
//            }
//            "normalBold" -> {
//                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14f)
//                typeface = fontSoftBold
//            }
//            "small" -> {
//                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12f)
//                typeface = fontDefault
//            }
//            "smallBold" -> {
//                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12f)
//                typeface = fontSoftBold
//            }
//            "tiny" -> {
//                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10f)
//                typeface = fontDefault
//            }
//            "tinyBold" -> {
//                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10f)
//                typeface = fontSoftBold
//            }
//            "dashboardTitle" -> {
//                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 36f)
//                typeface = fontDefault
//            }
//            "aboutTitle" -> {
//                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 32f)
//                typeface = fontSoftBold
//            }
//        }
//    }
}