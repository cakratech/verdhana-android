package com.bcasekuritas.mybest.app.feature.sectors.detail

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.domain.dto.response.TradeSummary
import com.bcasekuritas.mybest.app.feature.activity.middle.MiddleActivity
import com.bcasekuritas.mybest.app.feature.index.detail.adapter.IndexDetailAdapter
import com.bcasekuritas.mybest.databinding.FragmentSectorDetailBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.constant.Args
import com.bcasekuritas.mybest.ext.constant.NavKeys
import com.bcasekuritas.mybest.ext.listener.OnClickStr
import com.bcasekuritas.mybest.ext.other.PagingManager
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@AndroidEntryPoint
class SectorDetailFragment: BaseFragment<FragmentSectorDetailBinding, SectorDetailViewModel>(), OnClickStr {

    override val bindingVariable: Int = BR.vmSectorDetail
    override val viewModel: SectorDetailViewModel by viewModels()
    override val binding: FragmentSectorDetailBinding by autoCleaned { (FragmentSectorDetailBinding.inflate(layoutInflater)) }

    private val mAdapter: IndexDetailAdapter by autoCleaned {
        IndexDetailAdapter(
            prefManager.urlIcon,
            this
        )
    }

    private var indexCode = ""
    private var indexId = 0
    private var userId = ""
    private var sessionId = ""

    private var sortState = 3
    private var isPageLoading = false

    override fun setupArguments() {
        super.setupArguments()

        arguments?.let {
            indexCode = it.getString(Args.EXTRA_PARAM_STR_ONE).toString()
            indexId = it.getInt(Args.EXTRA_PARAM_INT_ONE)
        }
    }

    override fun setupAdapter() {
        super.setupAdapter()
        binding.rcvStocks.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }
    }

    override fun setupComponent() {
        super.setupComponent()
        binding.apply {
            tvIndex.text = indexCode

            val searchTextWatcher = object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                    // Do something before text changes
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // Do something when text changes
                }

                override fun afterTextChanged(s: Editable?) {
                    mAdapter.clearData()
                    viewModel.setSortOrSearch(sortState , s.toString())
                }
            }

            binding.searchBar.setTextWatcher(searchTextWatcher)
        }
    }

    override fun initOnClick() {
        super.initOnClick()
        binding.apply {
            btnBack.setOnClickListener {
                onBackPressed()
            }
            btnSort.setOnClickListener {
                showDialogSortSectorBottom(sortState, parentFragmentManager)
            }
        }
    }

    override fun setupListener() {
        super.setupListener()
        parentFragmentManager.setFragmentResultListener(
            NavKeys.KEY_FM_SECTOR,
            viewLifecycleOwner
        ) { _, result ->
            val confirmResult = result.getString(NavKeys.CONST_RES_SECTOR)

            if (confirmResult == "RESULT_OK") {
                val sortResult = result.getInt("sort")
                sortState = sortResult
                mAdapter.clearData()
                viewModel.setSortOrSearch(sortResult, binding.searchBar.getText())
            }
        }

        binding.rcvStocks.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                (recyclerView.layoutManager as? LinearLayoutManager)?.let { layoutManager ->
                    val visibleItemCount = layoutManager.childCount
                    val pastVisibleItem = layoutManager.findFirstVisibleItemPosition()
                    val total  = mAdapter.itemCount

                    val isSearch = binding.searchBar.getText().toString() == ""
                    if (!isPageLoading && isSearch) {
                        if (visibleItemCount + pastVisibleItem>= total){
                            viewModel.loadNextPage()
                            isPageLoading = true
                        }
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

        viewModel.getStockIndex(userId, sessionId, indexId.toLong())
    }

    override fun onResume() {
        super.onResume()
        viewModel.setListenerTradeSummary()
        viewModel.resumeSubscribe()
    }

    override fun onPause() {
        super.onPause()
        viewModel.unSubscribeTradeSummary()
    }

    override fun setupObserver() {
        super.setupObserver()

        viewModel.getListStockIndex.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                mAdapter.setDataPaging(it.filterNotNull())
            }
            isPageLoading = false
        }

        viewModel.getSubscribeStockSector.observe(viewLifecycleOwner) { item ->
            if (item != null) {
                val itemIndex = mAdapter.getDataList().indexOfFirst { it.secCode == item.secCode }

                if (itemIndex != -1) {
                    if (!isPageLoading) {
                        mAdapter.updateData(itemIndex, item)
                    }
                }
            }
        }

        viewModel.getStockCountSectorResult.observe(viewLifecycleOwner) {totalStock ->
            binding.tvStockCount.text = "$totalStock Stocks"
        }
    }

    override fun onClickStr(value: String?) {
        MiddleActivity.startIntentParam(requireActivity(), NavKeys.KEY_FM_STOCK_DETAIL, value!!, "")
    }
}