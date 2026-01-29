package com.bcasekuritas.mybest.widget.scrollview

interface ScrollViewListener {
    fun onScrollChanged(scrollView: ObservableVerticalScrollView?, x: Int, y: Int, oldx: Int, oldy: Int)
}