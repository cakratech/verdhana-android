package com.bcasekuritas.mybest.widget.edittext

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.databinding.CustomWidgetEdtWithTitleBinding
import com.google.android.material.textfield.TextInputLayout


class CustomEditTextWithTitle(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    private val binding = CustomWidgetEdtWithTitleBinding.inflate(LayoutInflater.from(context), this, true)

    init {

        // Read custom attributes
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomEditTextWithTitle)
        val title = typedArray.getString(R.styleable.CustomEditTextWithTitle_title)
        val hint = typedArray.getString(R.styleable.CustomEditTextWithTitle_hint)
        val editTextFor = typedArray.getString(R.styleable.CustomEditTextWithTitle_edittextfor)
        val isNoteIcon = typedArray.getBoolean(R.styleable.CustomEditTextWithTitle_isNoteIcon, false)
        typedArray.recycle()

        // Set title and hint
        binding.titleTextView.text = title
        binding.editText.hint = hint
        binding.editText.isSingleLine = true

        if (isNoteIcon){
            binding.editText.isSingleLine = false
            binding.ivNotes.isGone = false
        } else {
            binding.ivNotes.isGone = true
        }

        // Add a TextWatcher to monitor EditText changes and clear errors
        binding.editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Hide error on text change
                s?.let {
                    val newText = it.toString().replace(" ", "")
                    if (it.toString() != newText) {
                        binding.editText.setText(newText)
                        binding.editText.setSelection(newText.length)
                    }
                }
                if (editTextFor != "pin") {
                    hideError()
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        when(editTextFor) {
            "password" -> {
                binding.editText.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.outline.endIconMode = TextInputLayout.END_ICON_PASSWORD_TOGGLE
                binding.outline.endIconDrawable = ContextCompat.getDrawable(context, R.drawable.selector_password_toggle)
                binding.editText.transformationMethod = PasswordTransformationMethod.getInstance()
            }
            "login" -> {
                val blockCharacterSet = "`~#^|$%&*!@()-_=+<>.,/?';:[{]}\"'\\ "
                val filter =
                    InputFilter { source, start, end, dest, dstart, dend ->
                        if (source != null && blockCharacterSet.contains("" + source)) {
                            ""
                        } else null
                    }
                binding.editText.filters = arrayOf(filter)
            }
            "pin" -> {
                binding.titleTextView.isGone = true
                binding.editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.editText.textAlignment = View.TEXT_ALIGNMENT_CENTER
                binding.errorTextView.textAlignment = View.TEXT_ALIGNMENT_CENTER
            }
            "watchlist" -> {
                val filters = arrayOf(
                    InputFilter { source, _, _, _, _, _ -> source.filter { it.isLetterOrDigit() } },
                    LengthFilter(20)
                )

                binding.editText.filters = filters
            }
            "otp" -> {
                binding.titleTextView.isGone = true
                binding.editText.inputType = InputType.TYPE_CLASS_NUMBER
                binding.editText.keyListener = DigitsKeyListener.getInstance("0123456789")
                binding.editText.textAlignment = View.TEXT_ALIGNMENT_CENTER
                binding.errorTextView.textAlignment = View.TEXT_ALIGNMENT_CENTER
                binding.editText.filters = arrayOf(LengthFilter(6))
            }
        }
    }

    fun setLetterSpacing(state: Boolean) {
        binding.editText.letterSpacing = if (state) 0.5f else 0f
    }

    fun changeIconPasswordToggle() {
        binding.outline.endIconDrawable = ContextCompat.getDrawable(context, R.drawable.custom_icon_password_toggle)
    }

    // Function to set the title text
    fun setTitle(title: String) {
        binding.titleTextView.text = title
    }

    // Function to get the entered text
    fun getText(): String {
        return binding.editText.text.toString()
    }
    fun setText(text: String) {
        binding.editText.setText(text)
    }

    // Function to show an error message
    fun showError(errorMessage: String) {
        binding.errorTextView.text = errorMessage
        if (errorMessage.isNotEmpty()) {
            binding.errorTextView.visibility = VISIBLE
        }
        binding.outline.boxStrokeColor = ContextCompat.getColor(context, R.color.redWarning)
        setErrorBox(true)
    }
    fun showErrorForPin(errorMessage: String) {
        binding.errorTextView.text = errorMessage
        binding.errorTextView.visibility = VISIBLE
        binding.outline.isEnabled = false
        binding.editText.setTextColor(ContextCompat.getColor(context, R.color.black))
    }

    // Function to hide the error message
    fun hideErrorForPin() {
        binding.errorTextView.visibility = GONE
        binding.outline.isEnabled = true
    }

    // Function to hide the error message
    fun hideError() {
        binding.errorTextView.visibility = GONE
        binding.outline.boxStrokeColor = ContextCompat.getColor(context, R.color.text_input_layout_stroke_color)
        setErrorBox(false)
    }

    fun setTextWatcher(textWatcher: TextWatcher) {
        binding.editText.addTextChangedListener(textWatcher)
    }

    fun setEdt(value: String){
        binding.editText.setText(value)
    }

    fun disable(){
        binding.editText.isEnabled = false
        binding.outline.background = ContextCompat.getDrawable(context, R.drawable.rounded_f2f2f2)
    }

    fun enable(){
        binding.editText.isEnabled = true
        binding.outline.background = ContextCompat.getDrawable(context, R.drawable.bg_edit_text)
    }

    fun cursorsPosition(length: Int){
        binding.editText.requestFocus()
        binding.editText.setSelection(length)
    }

    fun disableFocus(){
        binding.editText.isFocusable = false
    }

    fun hideKeyboard(){
        val inputMgr = (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?)
        inputMgr?.hideSoftInputFromWindow(windowToken, 0);
    }

    fun setErrorBox(state: Boolean) {
        val colorStateListError = ContextCompat.getColorStateList(context, R.color.text_input_layout_stroke_color_error)
        val colorStateList = ContextCompat.getColorStateList(context, R.color.text_input_layout_stroke_color)
        if (colorStateList != null && colorStateListError != null) {
            if (state) {
                binding.outline.setBoxStrokeColorStateList(colorStateListError)
                binding.titleTextView.setTextColor(ContextCompat.getColor(context, R.color.redWarning))
            } else {
                binding.outline.setBoxStrokeColorStateList(colorStateList)
                binding.titleTextView.setTextColor(ContextCompat.getColor(context, R.color.black))
            }
        }
    }
}