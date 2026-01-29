package com.bcasekuritas.mybest.app.feature.watchlist.selectwatchlist

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.domain.dto.request.WatchListCategory
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.app.feature.watchlist.selectwatchlist.adapter.SelectWatchlistAdapter
import com.bcasekuritas.mybest.databinding.FragmentSelectWatchlistBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.common.showToast
import com.bcasekuritas.mybest.ext.constant.Args
import com.bcasekuritas.mybest.ext.delegate.ShowDialog
import com.bcasekuritas.mybest.ext.delegate.ShowDialogImpl
import com.bcasekuritas.mybest.ext.listener.OnClickAny

@FragmentScoped
@AndroidEntryPoint
class AddToWatchlistFragment : BaseFragment<FragmentSelectWatchlistBinding, SelectWatchlistViewModel>(), OnClickAny,
    ShowDialog by ShowDialogImpl() {

    @FragmentScoped
    override val bindingVariable: Int = BR.vmAddToWatchlist
    override val viewModel: SelectWatchlistViewModel by viewModels()
    override val binding: FragmentSelectWatchlistBinding by autoCleaned {
        (FragmentSelectWatchlistBinding.inflate(
            layoutInflater
        ))
    }

    private val selectWatchlistAdapter: SelectWatchlistAdapter by autoCleaned { SelectWatchlistAdapter(this) }
    private var watchlistList: MutableList<WatchListCategory> = mutableListOf()
    private var wlCodes = ""
    private var stockList: ArrayList<String> = ArrayList()
    private var wlCatList: ArrayList<String> = ArrayList()
    private var fromLayout = ""


    private lateinit var userId: String
    private lateinit var sessionId: String

    override fun setupComponent() {
        super.setupComponent()

        binding.apply {
            lyToolbar.tvLayoutToolbarMasterTitle.text = "Select Watchlist"
            lyBtnBottom.btnOk.isEnabled = false
            btnCreateCategory.setOnClickListener {
                val listCategory = watchlistList.map { it.category }
                showDialogCreateCategoryCallBack(parentFragmentManager, listCategory, false, "" ,onOkClicked = {wlCode ->
                    wlCodes = wlCode
                    viewModel.addUserWatchList(userId, wlCodes, stockList, sessionId)
                })
            }

            lyBtnBottom.btnOk.setOnClickListener {
                viewModel.addItemToCategory(userId, wlCatList, stockList, sessionId)
            }
        }
    }

    override fun setupArguments() {
        super.setupArguments()
        arguments?.let {
            stockList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.getStringArrayList(Args.EXTRA_PARAM_OBJECT) as ArrayList<String>
            } else {
                it.getStringArrayList(Args.EXTRA_PARAM_OBJECT) ?: ArrayList()
            }

            fromLayout = it.getString(Args.EXTRA_PARAM_STR_ONE).toString()
        }

        userId = prefManager.userId
        sessionId = prefManager.sessionId
    }

    override fun setupObserver() {
        super.setupObserver()

        viewModel.getSimpleWatchlistResult.observe(viewLifecycleOwner){
            when(it){
                is Resource.Loading -> {
                    showLoading()
                }
                is Resource.Success -> {
                    val res = it.data
                    if (res != null) {
                        when (res?.status) {
                            0 -> {
                                if (res != null) {
                                    for (userWatchListList in res.userWatchListList) {
                                        val listStockParam: MutableList<String> = mutableListOf()
                                        userWatchListList.userWatchListItemList.map {
                                            listStockParam.add(
                                                it.itemCode
                                            )
                                        }

                                        if (userWatchListList.userWatchListGroup.wlCode.lowercase() != "portfolio") {
                                            watchlistList.add(
                                                WatchListCategory(
                                                    userWatchListList.userWatchListGroup.wlCode,
                                                    stockListString = listStockParam
                                                )
                                            )
                                        }
                                    }

                                    selectWatchlistAdapter.setData(watchlistList)
                                }
                            }
                        }
                    }
                }

                else -> {}
            }
        }

        viewModel.addUserWatchlistResult.observe(viewLifecycleOwner) {
            when(it){
                is Resource.Success -> {
                    val res = it.data
                    if (res != null) {
                        if (res?.status == 0){
                            val bundle = Bundle().apply {
                                putBoolean(Args.EXTRA_PARAM_BOOLEAN, true)
                                putInt(Args.EXTRA_PARAM_INT_ONE, res.userWatchListItemCount)
                                putInt(Args.EXTRA_PARAM_INT_TWO, 1)
                            }

                            val backstack = if (fromLayout == "stock_detail") R.id.stock_detail_fragment else R.id.manage_watchlist_fragment
                            val inclusive = fromLayout != "stock_detail"

                            findNavController().navigate(R.id.manage_watchlist_fragment, bundle,
                                NavOptions.Builder()
                                    .setPopUpTo(backstack, inclusive).build())
                        }
                    }

                }

                else -> {}
            }
        }

        viewModel.addItemCategoryResult.observe(viewLifecycleOwner) {
            when(it){
                is Resource.Success -> {
                    val res = it.data
                    if (res != null) {
                        if (res?.status == 0) {
                            val bundle = Bundle().apply {
                                putBoolean(Args.EXTRA_PARAM_BOOLEAN, true)
                                putInt(Args.EXTRA_PARAM_INT_ONE, res.userWatchListItemCount)
                                putInt(Args.EXTRA_PARAM_INT_TWO, res.userWatchListGroupCount)
                            }

                            val backstack = if (fromLayout == "stock_detail") R.id.stock_detail_fragment else R.id.manage_watchlist_fragment
                            val inclusive = fromLayout != "stock_detail"

                            findNavController().navigate(
                                R.id.manage_watchlist_fragment, bundle,
                                NavOptions.Builder()
                                    .setPopUpTo(backstack, inclusive).build()
                            )
                        }
                    }
                }

                else -> {}
            }
        }
    }

    override fun setupAdapter() {
        super.setupAdapter()

        binding.rcvCategory.setHasFixedSize(true)
        binding.rcvCategory.adapter = selectWatchlistAdapter
    }

    override fun initAPI() {
        super.initAPI()
        val userId = prefManager.userId
        val sessionId = prefManager.sessionId

        viewModel.getSimpleWatchlist(userId, "",sessionId)

        binding.lyToolbar.ivLayoutToolbarMasterIconLeft.setOnClickListener {
            onBackPressed()
//            findNavController().popBackStack()
        }
    }

    override fun onClickAny(valueAny: Any?) {
        valueAny as List<WatchListCategory>

        binding.lyBtnBottom.btnOk.isEnabled = if (valueAny.size != 0) true else false
        binding.lyBtnBottom.tvStockSelected.text = "${valueAny.size} Category Selected"
        wlCatList.clear()
        valueAny.map { wlCatList.add(it.category) }
        wlCatList.distinctBy { it }
    }
}