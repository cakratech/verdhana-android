package com.bcasekuritas.mybest.app.data.layout

import android.text.Spanned
import com.google.gson.annotations.SerializedName

class UISnackModel {
    constructor(message: String?, color: String?) {
        this.message = message
        this.color = color
    }

    @SerializedName("message")
    var spanned: Spanned? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("color")
    var color: String? = null
}