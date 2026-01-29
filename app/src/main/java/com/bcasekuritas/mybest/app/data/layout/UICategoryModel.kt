package com.bcasekuritas.mybest.app.data.layout

import com.google.gson.annotations.SerializedName

class UICategoryModel {
    @SerializedName("name")
    var name: String? = null

    @SerializedName("icon")
    var icon = 0

    @SerializedName("is_enabled")
    var isEnabled: Boolean? = null

    constructor(name: String?, icon: Int) {
        this.name = name
        this.icon = icon
    }

    constructor() {}
}