package com.bcasekuritas.mybest.app.feature.news.tabnews

import android.net.Uri
import android.util.Log
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
import com.bcasekuritas.mybest.app.feature.activity.middle.MiddleActivity
import com.bcasekuritas.mybest.app.feature.news.NewsShareViewModel
import com.bcasekuritas.mybest.app.feature.news.tabnews.adapter.TabNewsAdapter
import com.bcasekuritas.mybest.databinding.FragmentTabNewsBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.constant.NavKeys
import com.bcasekuritas.mybest.ext.listener.OnClickStrInt

@FragmentScoped
@AndroidEntryPoint
class TabNewsFragment : BaseFragment<FragmentTabNewsBinding, TabNewsViewModel>(), OnClickStrInt {

    override val viewModel: TabNewsViewModel by viewModels()
    override val binding: FragmentTabNewsBinding by autoCleaned { (FragmentTabNewsBinding.inflate(layoutInflater)) }
    override val bindingVariable: Int = BR.vmNewsTab

    private val sharedViewModel: NewsShareViewModel by lazy {
        ViewModelProvider(requireActivity()).get(NewsShareViewModel::class.java)
    }

    private val mAdapter: TabNewsAdapter by autoCleaned{ TabNewsAdapter(this) }
    private lateinit var linearLayoutManager: LinearLayoutManager

    private val webView = CustomTabsIntent.Builder().build()

    private var isPageLoading = false

    private var userId = ""
    private var sessionId = ""
    private var query = ""
    private var page = 0
    private var size = 10

    override fun setupAdapter() {
        super.setupAdapter()
        linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rcvNews.apply {
            adapter = mAdapter
            layoutManager = linearLayoutManager
        }
    }

    override fun setupListener() {
        super.setupListener()
        binding.rcvNews.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val visibleItemCount = linearLayoutManager.childCount
                val pastVisibleItem = linearLayoutManager.findFirstVisibleItemPosition()
                val total  = mAdapter.itemCount

                if (!isPageLoading) {
                    if ((visibleItemCount + pastVisibleItem) >= total) {
                        page += 1
                        getNewsData(query)
                        isPageLoading = true
                    }
                }


                super.onScrolled(recyclerView, dx, dy)
            }
        })
    }

    override fun initAPI() {
        super.initAPI()
        userId = prefManager.userId
        sessionId = prefManager.sessionId

    }

    override fun setupObserver() {
        super.setupObserver()

        viewModel.getNewsFeedResult.observe(viewLifecycleOwner) {listItem ->
            if (!listItem.isNullOrEmpty()) {
                if (page > 0) {
                    mAdapter.addData(listItem)
                } else {
                    mAdapter.setData(listItem)
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
                getNewsData(query)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        query = ""
        page = 0
        getNewsData(query)
    }

    private fun getNewsData(query: String) {
        Log.d("newsss", "news hit page $page")
        viewModel.getNewsFeedSearch(userId, sessionId, page, size, query)
    }

    // on click item
    override fun onClickStrInt(valueStr: String?, valueInt: Int?) {
        if (!valueStr.isNullOrEmpty() && valueInt != null) {
            // value int 0 = news, 1 = tag
            if (valueInt == 0) {
                if (valueStr.isBlank() || !(valueStr.startsWith("http://") || valueStr.startsWith("https://"))) {
                    Toast.makeText(context, "Unavailable url file", Toast.LENGTH_SHORT).show()
                    return
                }
                try {
                    webView.launchUrl(requireContext(), Uri.parse(valueStr))
                } catch (ignore: Exception) {}
            } else {
                MiddleActivity.startIntentParam(
                    requireActivity(),
                    NavKeys.KEY_FM_STOCK_DETAIL,
                    valueStr.toString(),
                    ""
                )
            }
        }
    }
}