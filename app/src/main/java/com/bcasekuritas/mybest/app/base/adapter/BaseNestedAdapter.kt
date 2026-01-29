package com.bcasekuritas.mybest.app.base.adapter

import androidx.recyclerview.widget.RecyclerView

abstract class BaseNestedAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var menuPositionList : ArrayList<Pair<String,Int>>     = arrayListOf()
    var realMenuPositionList : ArrayList<Pair<String,Int>> = arrayListOf()
        set(value) {
            field.apply {
                clear()
                addAll(value)
            }
            menuPositionList.clear()
            menuPositionList.addAll(value)
        }


    override fun getItemCount(): Int {
        return menuPositionList.size
    }
}