package com.bcasekuritas.mybest.widget.adjuster

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.databinding.CustomWidgetQuantityAdjusterBinding
import com.bcasekuritas.mybest.ext.common.checkFractionPrice
import com.bcasekuritas.mybest.ext.converter.formatPriceWithoutDecimal
import com.bcasekuritas.mybest.ext.converter.removeSeparator

class CustomQtyAdjuster(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    private val binding =
        CustomWidgetQuantityAdjusterBinding.inflate(LayoutInflater.from(context), this, true)
    private var isDisable = false
    private var isSpecialStock = false

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomQtyAdjuster)
        val isFraction = typedArray.getBoolean(R.styleable.CustomQtyAdjuster_isFraction, true)
        val adjustFor = typedArray.getString(R.styleable.CustomQtyAdjuster_adjusterFor)
        typedArray.recycle()

        binding.etQty.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                /*binding.etQty.text?.toString().let {text ->
                    text?.let {
                        if (it != "0" || it != "") {
                            if (it.first().toString() == "0") {
                                binding.etQty.setText(it.substring(1))
                            }
                        }
                    }
                }*/
            }
            override fun afterTextChanged(s: Editable?) {
                binding.etQty.removeTextChangedListener(this)

                if (!isDisable) {
                    binding.btnMin.isEnabled = binding.etQty.text?.toString() != "0"
                }

                val removeSeparator = s.toString().removeSeparator()
                val formattedText = removeSeparator.formatPriceWithoutDecimal()

                binding.etQty.setText(formattedText)
                binding.etQty.setSelection(formattedText.length)
                binding.etQty.addTextChangedListener(this)
            }
        })

        binding.btnMin.setOnClickListener {
            val value = binding.etQty.text?.toString()?.removeSeparator()
            value?.toLongOrNull()?.let { quantity ->
                val newQty = if (isFraction) {
                    getFractionPrice(quantity.toInt(), "-")
                } else {
                    when (adjustFor) {
                        "shares" -> quantity.minus(1).toString()
                        else -> quantity.minus(1).toString() // Ensure whole number output if not a fraction
                    }
                }
                binding.etQty.setText(newQty)
            } ?: run {
                // Handle empty input case using the appropriate value and formatting based on isFraction
                val initialValue = if (isFraction) {
                    getFractionPrice(0, "-")
                } else {
                    "0" // Initialize with 0 for whole numbers
                }
                binding.etQty.setText(initialValue)
            }

        }

        binding.btnPlus.setOnClickListener {
            val value = binding.etQty.text?.toString()?.removeSeparator()
            value?.toLongOrNull()?.let { quantity ->
                val newQty = if (isFraction) {
                    getFractionPrice(quantity.toInt(), "+")
                } else {
                    when (adjustFor) {
                        "shares" -> quantity.plus(1).toString()
                        else -> quantity.plus(1).toString() // Ensure whole number output if not a fraction
                    }
                }
                binding.etQty.setText(newQty)
            } ?: run {
                // Handle empty input case using the appropriate value and formatting based on isFraction
                val initialValue = if (isFraction) {
                    getFractionPrice(0, "+")
                } else {
                    "0" // Initialize with 0 for whole numbers
                }
                binding.etQty.setText(initialValue)
            }
        }

    }

    fun getDouble(): Double{
        val value = binding.etQty.text?.toString()
        val removeSeparator = value?.replace(",", "")
        return removeSeparator?.toDoubleOrNull() ?: 0.0
    }
    fun getString(): String{
        return binding.etQty.text.toString()
    }
    fun getDoubleTimes100(): Double{
        val value = binding.etQty.text?.toString()
        val removeSeparator = value?.replace(",", "")
        return removeSeparator?.toDoubleOrNull()?.times(100) ?: 0.0
    }
    fun getInt(): Int {
        val value = binding.etQty.text?.toString()
        val removeSeparator = value?.replace(",", "")
        return removeSeparator?.toIntOrNull() ?: 0
    }

    fun disable(){
        binding.etQty.setText("")
        binding.etQty.isEnabled = false
        binding.btnPlus.isEnabled = false
        binding.btnMin.isEnabled = false
        changeColor(R.color.txtBlackWhite)
        isDisable = true
    }
    fun enable(){
        binding.etQty.isEnabled = true
        binding.btnPlus.isEnabled = true
        binding.btnMin.isEnabled = true
        isDisable = false
    }

    fun disableIncreaseBtn(){
        binding.btnPlus.isEnabled = false
    }

    fun disableDecreaseBtn(){
        binding.btnMin.isEnabled = false
    }

    fun enableIncreaseBtn(){
        binding.btnPlus.isEnabled = true
    }

    fun enableDecreaseBtn(){
        binding.btnMin.isEnabled = true
    }

    fun setEdt(value: String){
        binding.etQty.setText(value)
    }

    fun setTextWatcher(textWatcher: TextWatcher) {
        binding.etQty.addTextChangedListener(textWatcher)
    }

    fun changeColor(colorId: Int) {
        binding.etQty.setTextColor(ContextCompat.getColor(context, colorId))
    }

    fun getFractionPrice(price: Int, mode: String): String {
        val modVal = when {
            isSpecialStock -> 1
            price < 200 -> 1
            price < 500 -> 2
            price < 2000 -> 5
            price < 5000 -> 10
            else -> 25
        }

        val previousModVal = when (price) {
            200 -> 1
            500 -> 2
            2000 -> 5
            5000 -> 10
            else -> modVal
        }
        val roundedPrice = (price + modVal - 1) / modVal * modVal

        val total = when (mode) {
            "-" -> if (roundedPrice > 0) roundedPrice - previousModVal else roundedPrice - modVal
            "+" -> if (checkFractionPrice(price, isSpecialStock)) roundedPrice + modVal else roundedPrice
            else -> roundedPrice // Default to no change
        }

        return total.toString()
    }

    fun setIsSpecialStock(state: Boolean) {
        isSpecialStock = state
    }
}