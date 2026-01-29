package com.bcasekuritas.mybest.ext.listener

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bcasekuritas.mybest.app.base.BaseViewHolder

abstract class InfiniteScrollTenListener : RecyclerView.OnScrollListener {

    private var visibleThreshold = 2
    private var currentPage = 0
    private var previousTotalItemCount = 0
    private var loading = true
    private val startingPageIndex = 0
    private var mLayoutManager: RecyclerView.LayoutManager
    private var mAdapter: RecyclerView.Adapter<BaseViewHolder>

    constructor(layoutManager: LinearLayoutManager, adapter: RecyclerView.Adapter<BaseViewHolder>) {
        mLayoutManager = layoutManager
        mAdapter = adapter
    }

    constructor(layoutManager: GridLayoutManager, adapter: RecyclerView.Adapter<BaseViewHolder>) {
        mLayoutManager = layoutManager
        mAdapter = adapter
        visibleThreshold *= layoutManager.spanCount
    }

    constructor(layoutManager: StaggeredGridLayoutManager, adapter: RecyclerView.Adapter<BaseViewHolder>) {
        mLayoutManager = layoutManager
        mAdapter = adapter
        visibleThreshold *= layoutManager.spanCount
    }

    private fun getLastVisibleItem(lastVisibleItemPositions: IntArray): Int {
        var maxSize = 0
        for (i in lastVisibleItemPositions.indices) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i]
            } else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i]
            }
        }
        return maxSize
    }

    override fun onScrolled(view: RecyclerView, dx: Int, dy: Int) {
        var lastVisibleItemPosition = 0
        var totalItemCount = mLayoutManager.itemCount
        var totalNotItem = 0

        when (mLayoutManager) {
            is StaggeredGridLayoutManager -> {
                val lastVisibleItemPositions =
                    (mLayoutManager as StaggeredGridLayoutManager).findLastVisibleItemPositions(null)
                lastVisibleItemPosition = getLastVisibleItem(lastVisibleItemPositions)
            }
            is GridLayoutManager -> {
                lastVisibleItemPosition =
                    (mLayoutManager as GridLayoutManager).findLastVisibleItemPosition()
            }
            is LinearLayoutManager -> {
                lastVisibleItemPosition =
                    (mLayoutManager as LinearLayoutManager).findLastVisibleItemPosition()
            }
        }

        if (totalItemCount < previousTotalItemCount) {
            currentPage = startingPageIndex
            previousTotalItemCount = totalItemCount
            if (totalItemCount == 0) {
                loading = true
            }
        }
        if (loading && totalItemCount > previousTotalItemCount) {
            loading = false
            previousTotalItemCount = totalItemCount
        }
        if (!loading && lastVisibleItemPosition + visibleThreshold > totalItemCount) {
            if (totalItemCount % 10 == 0) {
                currentPage++
                onLoadMore(currentPage, totalItemCount, view)
                loading = true
            }

//            if (mAdapter is ConsulHistoryAdapter) {
//                totalNotItem = (mAdapter as ConsulHistoryAdapter).getDataNotItem()
//
//                totalItemCount -= totalNotItem
//                if (totalItemCount % 10 == 0) {
//                    currentPage++
//                    onLoadMore(currentPage, totalItemCount, view)
//                    loading = true
//                }
//            }
        }
    }

    fun resetState() {
        currentPage = startingPageIndex
        previousTotalItemCount = 0
        loading = true
    }

    abstract fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?)
}