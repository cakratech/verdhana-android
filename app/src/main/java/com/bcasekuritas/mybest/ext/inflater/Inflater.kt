package com.bcasekuritas.mybest.ext.inflater

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup

fun getLayoutInflater(parent: ViewGroup): LayoutInflater {
    return LayoutInflater.from(parent.context)
}

fun Context.getInflater(): LayoutInflater {
    return getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
}