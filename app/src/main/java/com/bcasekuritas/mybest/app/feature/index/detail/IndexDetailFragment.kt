package com.bcasekuritas.mybest.app.feature.index.detail

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
import com.bcasekuritas.mybest.databinding.FragmentIndexDetailBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.constant.Args
import com.bcasekuritas.mybest.ext.constant.NavKeys
import com.bcasekuritas.mybest.ext.listener.OnClickStr
import com.bcasekuritas.mybest.ext.other.PagingManager
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@AndroidEntryPoint
class IndexDetailFragment : BaseFragment<FragmentIndexDetailBinding, IndexDetailViewModel>(),
    OnClickStr {

    @FragmentScoped
    override val bindingVariable: Int = BR.vmIndexDetail
    override val viewModel: IndexDetailViewModel by viewModels()
    override val binding: FragmentIndexDetailBinding by autoCleaned {
        (FragmentIndexDetailBinding.inflate(
            layoutInflater
        ))
    }
    private val indexAdapter: IndexDetailAdapter by autoCleaned {
        IndexDetailAdapter(
            prefManager.urlIcon,
            this
        )
    }

    private lateinit var linearLayoutManager: LinearLayoutManager

    private var indexCode = ""
    private var indexId = 0
    private var userId = ""
    private var sessionId = ""

    private var sortState = 0

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
        linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rcvStocks.apply {
            adapter = indexAdapter
            layoutManager = linearLayoutManager
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }
    }

    override fun setupComponent() {
        super.setupComponent()
        binding.apply {
            tvIndex.text = indexCode
            searchBar.setHint("Search code or name")

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
                    indexAdapter.clearData()
                    viewModel.searchCodeOrName(s.toString())

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

            }
        }

        binding.rcvStocks.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                (recyclerView.layoutManager as? LinearLayoutManager)?.let { layoutManager ->
                    val visibleItemCount = layoutManager.childCount
                    val pastVisibleItem = layoutManager.findFirstVisibleItemPosition()
                    val total  = indexAdapter.itemCount

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
        viewModel.setListenerIndice()
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
                indexAdapter.setDataPaging(it.filterNotNull())
            }
            isPageLoading = false
        }

        viewModel.getSubscribeStockIndex.observe(viewLifecycleOwner) { item ->
            if (item != null) {
                val itemIndex = indexAdapter.getDataList().indexOfFirst { it.secCode == item.secCode }

                if (itemIndex != -1) {
                    if (!isPageLoading) {
                        indexAdapter.updateData(itemIndex, item)
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