package com.bcasekuritas.mybest.widget.edittext

import android.text.InputFilter
import android.widget.EditText

object EditTextUtils {
    fun disallowFirstZero(editText: EditText) { // TODO RK CORRECT THE LOGIC
        val inputFilter = InputFilter { source, _, _, _, _, _ ->
            if (source[0] == '0') {
                source.toString().replaceFirst("0", "")
            } else {
                null
            }
        }

        val filters = editText.filters.toMutableList()
        filters.add(inputFilter)

        editText.filters = filters.toTypedArray()
    }

    fun disallowSpace(editText: EditText) {
        editText.text?.let {
            val newText = it.toString().replace(" ", "")
            if (it.toString() != newText) {
                editText.setText(newText)
                editText.setSelection(newText.length)
            }
        }
    }
}