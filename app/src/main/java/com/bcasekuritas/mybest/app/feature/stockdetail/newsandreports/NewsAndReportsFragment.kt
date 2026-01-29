package com.bcasekuritas.mybest.app.feature.stockdetail.newsandreports

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.domain.dto.response.source.StockDetReportsRes
import com.bcasekuritas.mybest.app.feature.activity.middle.MiddleActivity
import com.bcasekuritas.mybest.app.feature.stockdetail.StockDetailSharedViewModel
import com.bcasekuritas.mybest.app.feature.stockdetail.adapter.StockDetailNewsAdapter
import com.bcasekuritas.mybest.app.feature.stockdetail.adapter.StockDetailReportsAdapter
import com.bcasekuritas.mybest.databinding.FragmentNewsAndReportsBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.constant.ConstKeys
import com.bcasekuritas.mybest.ext.constant.NavKeys
import com.bcasekuritas.mybest.ext.delegate.ShowDropDown
import com.bcasekuritas.mybest.ext.delegate.ShowDropDownImpl
import com.bcasekuritas.mybest.ext.listener.OnClickAnyStr
import com.bcasekuritas.mybest.ext.listener.OnClickStr
import com.bcasekuritas.mybest.ext.listener.OnClickStrInt
import java.net.URLEncoder

@FragmentScoped
@AndroidEntryPoint
class NewsAndReportsFragment : BaseFragment<FragmentNewsAndReportsBinding, NewsAndReportsViewModel>(), ShowDropDown by ShowDropDownImpl(), OnClickStrInt, OnClickAnyStr {

    @FragmentScoped
    override val bindingVariable: Int = BR.vmKeyStats
    override val viewModel: NewsAndReportsViewModel by viewModels()
    override val binding: FragmentNewsAndReportsBinding by autoCleaned {
        FragmentNewsAndReportsBinding.inflate(
            layoutInflater
        )
    }

    lateinit var sharedViewModel: StockDetailSharedViewModel
    private val newsAdapter: StockDetailNewsAdapter by autoCleaned { StockDetailNewsAdapter(this) }
    private val reportsAdapter: StockDetailReportsAdapter by autoCleaned { StockDetailReportsAdapter(this) }

    private lateinit var linearLayoutManagerNews: LinearLayoutManager
    private lateinit var linearLayoutManagerReports: LinearLayoutManager

    private val webView = CustomTabsIntent.Builder().build()

    private var stockCode = ""
    private var isPageLoading = false

    private var userId = ""
    private var sessionId = ""
    private var pageNews = 0
    private var pageReports = 0
    private var size = 10

    private var isPageNews = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity()).get(StockDetailSharedViewModel::class.java)
    }

    override fun setupAdapter() {
        super.setupAdapter()
        linearLayoutManagerNews = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        linearLayoutManagerReports = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        // News
        binding.rcvSdNews.apply {
            adapter = newsAdapter
            layoutManager = linearLayoutManagerNews
        }

        // Reports
        binding.rcvSdReports.adapter = reportsAdapter
        binding.rcvSdReports.layoutManager = linearLayoutManagerReports
    }

    override fun setupListener() {
        super.setupListener()
        binding.rcvSdNews.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val visibleItemCount = linearLayoutManagerNews.childCount
                val pastVisibleItem = linearLayoutManagerNews.findFirstVisibleItemPosition()
                val total  = newsAdapter.itemCount


                if (!isPageLoading) {
                    if ((visibleItemCount + pastVisibleItem) >= total) {
                        pageNews += 1

                        viewModel.getNewsFeed(userId, sessionId, stockCode, pageNews, size)
                        isPageLoading = true
                    }
                }


                super.onScrolled(recyclerView, dx, dy)
            }
        })

        binding.rcvSdReports.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val visibleItemCount = linearLayoutManagerReports.childCount
                val pastVisibleItem = linearLayoutManagerReports.findFirstVisibleItemPosition()
                val total  = reportsAdapter.itemCount

                Log.d("newsss", "on scroll: item ${(visibleItemCount + pastVisibleItem)} total $total")
                Log.d("newsss", "is loading: $isPageLoading")
                if (!isPageLoading) {
                    if ((visibleItemCount + pastVisibleItem) >= total) {
                        pageReports += 1
                        Log.d("newsss", "page $pageReports")
                        viewModel.getResearchNewsSearch(userId, sessionId, pageReports, size, stockCode)
                        isPageLoading = true
                    }
                }


                super.onScrolled(recyclerView, dx, dy)
            }
        })
    }

    override fun initOnClick() {
        super.initOnClick()

        binding.chipGroupNewsReports.setOnCheckedStateChangeListener { _, _ ->
            pageReports = 0
            pageNews = 0
            isPageNews = binding.chipNews.isChecked
            if (isPageNews){
                binding.rcvSdNews.visibility = View.VISIBLE
                binding.rcvSdReports.visibility = View.GONE
            }else{
                binding.rcvSdNews.visibility = View.GONE
                binding.rcvSdReports.visibility = View.VISIBLE
            }
            newsAdapter.clearData()
            reportsAdapter.clearData()
            initAPI()
        }
    }

    override fun initAPI() {
        super.initAPI()
        userId = prefManager.userId
        sessionId = prefManager.sessionId
        stockCode = prefManager.stockDetailCode

        if (isPageNews) {
            viewModel.getNewsFeed(userId, sessionId, stockCode, pageNews, size)
        } else {
            viewModel.getResearchNewsSearch(userId, sessionId, pageReports, size, stockCode)
        }
    }

    override fun setupObserver() {
        super.setupObserver()
        sharedViewModel.getStockCodeChangeResult.observe(viewLifecycleOwner){
            if (it == true){
                pageNews = 0
                pageReports = 0
                newsAdapter.clearData()
                reportsAdapter.clearData()
                initAPI()
            }
        }

        viewModel.getNewsFeedByStockResult.observe(viewLifecycleOwner) {listNews ->
            if (!listNews.isNullOrEmpty()) {
                if (pageNews >= 1) {
                    newsAdapter.addData(listNews)
                } else {
                    newsAdapter.setData(listNews)
                }
            }
            isPageLoading = false
        }

        viewModel.getResearchNewsResult.observe(viewLifecycleOwner){data ->
            if (!data.isNullOrEmpty()) {
                if (pageReports >= 1) {
                    Log.d("newsss", "add ------")
                    reportsAdapter.addData(data)
                } else {
                    Log.d("newsss", "set ------")
                    reportsAdapter.setData(data, stockCode)
                }
            }
            isPageLoading = false
        }
    }

    // news click
    override fun onClickStrInt(valueStr: String?, valueInt: Int?) {
        if (!valueStr.isNullOrEmpty() && valueInt != null) {
            if (valueInt == 0) {
                if (valueStr.isBlank() || !(valueStr.startsWith("http://") || valueStr.startsWith("https://"))) {
                    Toast.makeText(context, "Unavailable url file", Toast.LENGTH_SHORT).show()
                    return
                }
                try {
                    webView.launchUrl(requireContext(), Uri.parse(valueStr))
                } catch (ignore: Exception) {}
            } else {
                // chip news click
                if (valueStr != stockCode) {
                    newsAdapter.clearData()
                    reportsAdapter.clearData()
                    pageNews = 0
                    sharedViewModel.setChipNewsOnClick(valueStr)
                }
            }
        }
    }

    // reports click
    override fun onClickAnyStr(valueAny: Any?, valueString: String) {
        if (!valueString.isNullOrEmpty()) {
            val encodedUrl = URLEncoder.encode(valueString, "UTF-8")
            val url = if (valueAny as Boolean) "https://docs.google.com/gview?embedded=true&url=$encodedUrl" else valueString
            if (url.isBlank() || !(url.startsWith("http://") || url.startsWith("https://"))) {
                Toast.makeText(context, "Unavailable url file", Toast.LENGTH_SHORT).show()
                return
            }
            try {
                webView.launchUrl(requireContext(), Uri.parse(url))
            } catch (ignore: Exception) {}
        }
    }
}