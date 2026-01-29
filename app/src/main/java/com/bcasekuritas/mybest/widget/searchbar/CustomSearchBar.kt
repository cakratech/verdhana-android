package com.bcasekuritas.mybest.widget.searchbar

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.bcasekuritas.mybest.databinding.CustomSearchbarBinding

class CustomSearchBar(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    private val binding = CustomSearchbarBinding.inflate(LayoutInflater.from(context), this, true)

    init {

        binding.edtSearchbar.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                binding.edtSearchbar.text?.let { VISIBLE } ?: INVISIBLE
            }
        })

        binding.btnClose.setOnClickListener {
            binding.btnClose.text = null
        }
    }

    fun setTextWatcher(textWatcher: TextWatcher) {
        binding.edtSearchbar.addTextChangedListener(textWatcher)
    }

    fun getText(): String {
        return binding.edtSearchbar.text.toString()
    }
    fun setText(text: String) {
        binding.edtSearchbar.setText(text)
    }

    fun setHint(hint: String){
        binding.edtSearchbar.hint = hint
    }
}