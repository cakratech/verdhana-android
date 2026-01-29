package com.bcasekuritas.mybest.app.base

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var mCurrentPosition = 0
    var mCurrentObject: Any? = null

    open fun onBind(obj: Any) {
        mCurrentObject = obj
    }

    fun getCurrentPosition(): Int {
        return mCurrentPosition
    }

    fun getCurrentObject(): Any? {
        return mCurrentObject
    }
}