package com.bcasekuritas.mybest.widget.adjuster

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.databinding.CustomQtyAdjusterBorderBinding
import com.bcasekuritas.mybest.ext.common.checkFractionPrice
import com.bcasekuritas.mybest.ext.converter.formatPriceWithoutDecimal
import com.bcasekuritas.mybest.ext.converter.removeSeparator

class CustomQtyAdjusterBorder (context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    private val binding =
        CustomQtyAdjusterBorderBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomQtyAdjuster)
        val isFraction = typedArray.getBoolean(R.styleable.CustomQtyAdjuster_isFraction, true)
        val adjusterFor = typedArray.getString(R.styleable.CustomQtyAdjuster_adjusterFor)
        val sizeWidth = typedArray.getString(R.styleable.CustomQtyAdjuster_sizeWidth)
        typedArray.recycle()

//        EditTextUtils.disallowFirstZero(binding.edtValue)

        binding.edtValue.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.edtValue.removeTextChangedListener(this)

                binding.btnMin.isEnabled = binding.edtValue.text?.toString() != "0"

                val removeSeparator = s.toString().removeSeparator()
                val formattedText = removeSeparator.formatPriceWithoutDecimal()
                binding.edtValue.setText(formattedText)
                binding.edtValue.setSelection(minOf(formattedText.length, binding.edtValue.text?.length ?: 0))
                binding.edtValue.addTextChangedListener(this)
            }
        })
        
        binding.btnMin.setOnClickListener {
            val value = binding.edtValue.text?.toString()?.removeSeparator()
            value?.toIntOrNull()?.let { quantity ->
                val newQty = if (isFraction) {
                    getFractionPrice(quantity, "-")
                } else {
                    quantity.minus(1).toString()
                }
                binding.edtValue.setText(newQty)
            } ?: run {
                val initialValue = if (isFraction) {
                    getFractionPrice(0, "-")
                } else {
                    "0"
                }
                binding.edtValue.setText(initialValue)
            }
        }
        
        binding.btnPlus.setOnClickListener {

            val value = binding.edtValue.text?.toString()?.removeSeparator()
            value?.toIntOrNull()?.let { quantity ->
                val newQty = if (isFraction) {
                    getFractionPrice(quantity, "+")
                } else {
                    quantity.plus(1).toString()
                }
                binding.edtValue.setText(newQty)
            } ?: run {
                val initialValue = if (isFraction) {
                    getFractionPrice(0, "+")
                } else {
                    "0"
                }
                binding.edtValue.setText(initialValue)
            }
        }

        when(adjusterFor){
            "customKey" -> {
                binding.edtValue.inputType =  InputType.TYPE_NULL
            }
            "priceAlert" -> {

            }
            else -> {

            }
        }

        when(sizeWidth){
            "matchParent" -> {
                binding .clAdjuster.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            }
        }
    }

    fun getDouble(): Double{
        val value = binding.edtValue.text?.toString()
        val removeSeparator = value?.replace(",", "")
        return removeSeparator?.toDoubleOrNull() ?: 0.0
    }
    fun getInt(): Int{
        return binding.edtValue.text.toString().removeSeparator().toInt()
    }
    fun getString(): String{
        return binding.edtValue.text.toString()
    }

    fun setEdt(value: String){
        binding.edtValue.setText(value)
    }

    fun changeColor(colorId: Int) {
        binding.edtValue.setTextColor(ContextCompat.getColor(context, colorId))
    }

    private fun getFractionPrice(price: Int, mode: String): String {
        val modVal = when {
            price < 200 -> 1
            price < 500 -> 2
            price < 2000 -> 5
            price < 5000 -> 10
            else -> 25
        }
        val roundedPrice = (price + modVal - 1) / modVal * modVal

        val total = when (mode) {
            "-" -> if (roundedPrice > 0) roundedPrice - modVal else 0
            "+" -> if (checkFractionPrice(price, false)) roundedPrice + modVal else roundedPrice
            else -> roundedPrice // Default to no change
        }

        return total.toString()
    }



    fun setTextWatcher(textWatcher: TextWatcher) {
        binding.edtValue.addTextChangedListener(textWatcher)
    }

}