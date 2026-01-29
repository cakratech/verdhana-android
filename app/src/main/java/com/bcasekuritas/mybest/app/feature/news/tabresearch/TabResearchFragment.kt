package com.bcasekuritas.mybest.app.feature.news.tabresearch

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.feature.news.NewsShareViewModel
import com.bcasekuritas.mybest.app.feature.news.tabresearch.adapter.TabResearchAdapter
import com.bcasekuritas.mybest.databinding.FragmentTabResearchBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.listener.OnClickStrInt
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import java.net.URLEncoder

@FragmentScoped
@AndroidEntryPoint
class TabResearchFragment: BaseFragment<FragmentTabResearchBinding, TabResearchViewModel>(), OnClickStrInt {

    override val viewModel: TabResearchViewModel by viewModels()
    override val binding: FragmentTabResearchBinding by autoCleaned { (FragmentTabResearchBinding.inflate(layoutInflater)) }
    override val bindingVariable: Int = BR.vmNewsTabResearch

    private val sharedViewModel: NewsShareViewModel by lazy {
        ViewModelProvider(requireActivity()).get(NewsShareViewModel::class.java)
    }

    private val mAdapter: TabResearchAdapter by autoCleaned{ TabResearchAdapter(this) }
    private lateinit var linearLayoutManager: LinearLayoutManager

    private val webView = CustomTabsIntent.Builder().build()

    private var page = 0
    private var size = 10
    private var query = ""

    private var userId = ""
    private var sessionId = ""

    private var isPageLoading = false

    override fun setupAdapter() {
        super.setupAdapter()
        linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rcvResearch.adapter = mAdapter
        binding.rcvResearch.layoutManager = linearLayoutManager
    }

    override fun initAPI() {
        super.initAPI()
        userId = prefManager.userId
        sessionId = prefManager.sessionId

    }

    override fun setupListener() {
        super.setupListener()
        binding.rcvResearch.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val visibleItemCount = linearLayoutManager.childCount
                val pastVisibleItem = linearLayoutManager.findFirstVisibleItemPosition()
                val total  = mAdapter.itemCount

                if (!isPageLoading) {
                    if ((visibleItemCount + pastVisibleItem) >= total) {
                        page += 1
                        getResearchData(query)
                        isPageLoading = true
                    }
                }

                super.onScrolled(recyclerView, dx, dy)
            }
        })

    }

    override fun setupObserver() {
        super.setupObserver()

        viewModel.getResearchNewsResult.observe(viewLifecycleOwner){data ->
            if (!data.isNullOrEmpty()) {
                val sortedList = data.sortedByDescending { it.publishDate }
                if (page > 0) {
                    mAdapter.addData(sortedList)
                } else {
                    mAdapter.setData(sortedList)
                }
            } else {
                if (page == 0) {
                    mAdapter.clearData()
                }
            }
            isPageLoading = false
        }

        sharedViewModel.getQuerySearch.observe(viewLifecycleOwner) {keySearch ->
            if (isVisible) {
                page = 0
                query = keySearch
                getResearchData(query)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        query = ""
        page = 0
        getResearchData(query)
    }

    private fun getResearchData(query: String) {
        Log.d("newsss", "research hit page $page")
        viewModel.getResearchNewsSearch(userId, sessionId, page, size, query)
    }

    override fun onClickStrInt(valueStr: String?, valueInt: Int?) {
        if (!valueStr.isNullOrEmpty()) {
            val encodedUrl = URLEncoder.encode(valueStr, "UTF-8")
            val url = if (valueInt == 0) "https://docs.google.com/gview?embedded=true&url=$encodedUrl" else valueStr
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