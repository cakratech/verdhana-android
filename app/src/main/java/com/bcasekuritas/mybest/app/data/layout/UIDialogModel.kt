package com.bcasekuritas.mybest.app.data.layout

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.annotation.StyleRes

class UIDialogModel(
    @DrawableRes
    var icon: Int? = null,

    @StringRes
    var title: Int? = null,

    var titleStr: String? = null,

    @StringRes
    var description: Int? = null,

    @StringRes
    var descriptionTwo: Int? = null,

    var descriptionStr: String? = null,

    var descriptionStrTwo: String? = null,

    @StringRes
    var specialWarning: Int? = null,

    @StyleRes
    var bgPositive: Int? = null,

    @StyleRes
    var bgNegative: Int? = null,

    @StringRes
    var btnPositive: Int? = null,


    var btnPositiveStr: String? = null,

    @StringRes
    var btnNegative: Int? = null,


    var btnNegativeStr: String? = null
)
