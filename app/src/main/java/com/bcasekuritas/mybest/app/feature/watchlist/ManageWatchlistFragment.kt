package com.bcasekuritas.mybest.app.feature.watchlist

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.domain.dto.request.ManageWatchlistItem
import com.bcasekuritas.mybest.app.domain.dto.request.WatchListCategory
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.app.feature.watchlist.adapter.ItemMoveCallback
import com.bcasekuritas.mybest.app.feature.watchlist.adapter.ManageWatchlistAdapter
import com.bcasekuritas.mybest.databinding.FragmentManageWatchlistBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.common.showToast
import com.bcasekuritas.mybest.ext.constant.Args
import com.bcasekuritas.mybest.ext.constant.NavKeys
import com.bcasekuritas.mybest.ext.delegate.ShowDialog
import com.bcasekuritas.mybest.ext.delegate.ShowDialogImpl
import com.bcasekuritas.mybest.ext.delegate.ShowSnackBarImpl
import com.bcasekuritas.mybest.ext.delegate.ShowSnackBarInterface
import com.bcasekuritas.mybest.ext.listener.OnClickAny
import com.bcasekuritas.mybest.ext.listener.OnClickBoolean
import com.bcasekuritas.mybest.ext.listener.OnClickStr

@FragmentScoped
@AndroidEntryPoint
class ManageWatchlistFragment :
    BaseFragment<FragmentManageWatchlistBinding, ManageWatchlistViewModel>(), OnClickAny, OnClickBoolean, OnClickStr,
    ShowDialog by ShowDialogImpl(), ShowSnackBarInterface by ShowSnackBarImpl() {

    @FragmentScoped
    override val bindingVariable: Int = BR.vmManageWatchlist
    override val viewModel: ManageWatchlistViewModel by viewModels()
    override val binding: FragmentManageWatchlistBinding by autoCleaned {
        (FragmentManageWatchlistBinding.inflate(
            layoutInflater
        ))
    }

    private val manageWatchlistAdapter: ManageWatchlistAdapter by autoCleaned {
        ManageWatchlistAdapter(
            prefManager.urlIcon, this, this, this
        )
    }

    private val selectedWatchlistItem = mutableMapOf<String , ManageWatchlistItem>()

    private lateinit var userId: String
    private lateinit var sessionId: String
    private var wlCode: String = ""
    private var itemOnMoved = false
    private var isOnDestroy = false
    private var isRename = false
    private var deletedStock = ""

    // after add stock
    private var isFromAddStock = false
    private var groupAddCount = 0
    private var itemAddCount = 0

    private val watchlistMapper = mutableMapOf<String ,WatchListCategory>()

    override fun setupArguments() {
        super.setupArguments()
        arguments?.let {
            itemAddCount = it.getInt(Args.EXTRA_PARAM_INT_ONE)
            groupAddCount = it.getInt(Args.EXTRA_PARAM_INT_TWO)
            isFromAddStock = it.getBoolean(Args.EXTRA_PARAM_BOOLEAN)
        }

        userId = prefManager.userId
        sessionId = prefManager.sessionId

    }

    override fun setupAdapter() {
        super.setupAdapter()

        binding.rcvStock.setHasFixedSize(true)
        binding.rcvStock.adapter = manageWatchlistAdapter

        val touchHelper = ItemTouchHelper(
            ItemMoveCallback(manageWatchlistAdapter)
        )

        touchHelper.attachToRecyclerView(binding.rcvStock)

    }

    override fun setupComponent() {
        super.setupComponent()

        if (isFromAddStock) {
            showSnackBarTop(
                requireContext(),
                binding.root,
                "success",
                R.drawable.ic_success,
                "Added $itemAddCount stocks to $groupAddCount watchlist",
                "" ,
                requireActivity(),
                NavKeys.KEY_FM_MANAGE_WATCHLIST
            )
            isFromAddStock = false
        }

        binding.btnAddStocks.setOnClickListener {
            findNavController().navigate(R.id.add_to_watchlist_fragment)
        }

        binding.lyToolbar.ivBack.setOnClickListener {
            onBackPressed()
        }

        binding.lyToolbar.dropDown.setOnClickListener {
            showDialogSelectCategoryCallBack(
                parentFragmentManager,
                watchlistMapper.values.toList(),
                onOkClicked = { wlCode, isCreateCategory ->
//                    reorderingItemWatchlist()
                    itemOnMoved = false
                    if (isCreateCategory) {
                        val totalCategory = watchlistMapper.keys.size - 1
                        if (totalCategory < 8) {
                            showDialogCreateCategoryCallBack(
                                parentFragmentManager, watchlistMapper.keys.toList(), false, "", onOkClicked = {newWlCode ->
                                    viewModel.addUserWatchlist(userId, newWlCode, emptyList(), sessionId, "*")
                                    binding.lyToolbar.tvTitle.text = newWlCode
                                    manageWatchlistAdapter.clearData()
                                    showData(false)
                                    binding.searchbar.setText("")
                                }
                            )
                        } else {
                            showSnackBarTop(
                                requireContext(),
                                binding.container,
                                "warning",
                                R.drawable.ic_warning_outline,
                                "Max category watchlist have been created (Max 8)",
                                "", requireActivity(), NavKeys.KEY_FM_ORDER
                            )
                        }

                    } else {
                        binding.searchbar.setText("")
                        binding.lyToolbar.tvTitle.text = wlCode
                        this.wlCode = wlCode
                        val stockList = watchlistMapper[wlCode]?.stockListString
                        if (stockList != null) {
                            viewModel.getStockParam(stockList)
                        } else {
                            manageWatchlistAdapter.clearData()
                        }
                        binding.lyToolbar.ivOption.visibility = if (wlCode != "All Watchlist" && wlCode != "Portfolio") View.VISIBLE else View.GONE
                    }

                })
        }

        binding.lyToolbar.ivOption.setOnClickListener {
            wlCode = binding.lyToolbar.tvTitle.text.toString()
            // isDelete true = delete watchlist, false = rename watchlist
            showDialogEditCategoryCallBack(parentFragmentManager, onOkClicked = { isDelete ->
                if (isDelete) {
                    viewModel.removeWatchListCategory(userId, wlCode, sessionId)

                } else {
                    val listStock = arrayListOf<String>()

                    watchlistMapper[wlCode]?.stockListString?.let { it1 -> listStock.addAll(it1) }

                    showDialogCreateCategoryCallBack(
                        parentFragmentManager, watchlistMapper.keys.toList(), true, wlCode, onOkClicked = { newWlCode ->
                            isRename = true
                            viewModel.addUserWatchlist(userId, wlCode, listStock, sessionId, newWlCode)
                            binding.lyToolbar.tvTitle.text = newWlCode
                        }
                    )
                }
            })
        }
    }

    override fun setupListener() {
        super.setupListener()
        binding.apply {
            val searchTextWatcher = object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    // Do something before text changes
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // Do something when text changes
                }

                override fun afterTextChanged(s: Editable?) {
                    getItemSearch(s.toString())
                }
            }
            searchbar.setTextWatcher(searchTextWatcher)
        }
    }

    override fun initAPI() {
        super.initAPI()
        userId = prefManager.userId
        sessionId = prefManager.sessionId
        wlCode = "All Watchlist"

        watchlistMapper["All Watchlist"] = WatchListCategory("All Watchlist")
        viewModel.getAllWatchlist(userId, sessionId)
        viewModel.getSimpleWatchlist(userId, sessionId)

    }

    override fun onPause() {
        super.onPause()

        isOnDestroy = true
        arguments?.clear()
//        reorderingItemWatchlist()
    }

    override fun onResume() {
        super.onResume()

        itemOnMoved = false
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.imqConnectionListener.connListenerLiveData.observe(this) { value ->
            when (value) {
                "Recovered" -> {
                    viewModel.imqConnectionListener.onListener("")
                    initAPI()
                }

                else -> {}
            }
        }

        viewModel.getSimpleAllWatchlistResult.observe(viewLifecycleOwner) {res ->
            if (res != null) {
                if (res.userWatchListItemList?.size != 0) {
                    val listStockParam = arrayListOf<String>()
                    res.userWatchListItemList?.map { listStockParam.add(it.itemCode) }
                    watchlistMapper["All Watchlist"] = WatchListCategory("All Watchlist", stockListString = listStockParam)
                    viewModel.getStockParam(listStockParam)
                } else {
                    showData(false)
                }
            }
        }

        viewModel.getSimpleWatchlistResult.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    showLoading()
                }

                is Resource.Success -> {
                    val res = it.data
                    if (res != null) {
                        when (res.status) {
                            0 -> {
                                for (userWatchListList in res.userWatchListList) {
                                    val listStockParam = arrayListOf<String>()
                                    val wlCode = userWatchListList.userWatchListGroup.wlCode

                                    userWatchListList.userWatchListItemList.map { listStockParam.add(it.itemCode) }
                                    watchlistMapper[wlCode] = WatchListCategory(wlCode, stockListString = listStockParam)
                                }
                            }
                        }
                    }

                }

                else -> {}
            }
        }

        viewModel.getStockParamResult.observe(viewLifecycleOwner){data ->
            if (data != null) {
//                val item = arrayListOf<ManageWatchlistItem>()
                selectedWatchlistItem.clear()
                val listStockCode = watchlistMapper[wlCode]?.stockListString?.toList()
                listStockCode?.map {
                    selectedWatchlistItem[it] = ManageWatchlistItem(stockCode = it)
                }

                data.map {item ->
                    selectedWatchlistItem[item?.stockParam?.stockCode]?.let {
                        if (item != null) {
                            it.stockName = item.stockParam.stockName
                        }
                    }
                }
                val isDelete = binding.lyToolbar.tvTitle.text.toString() != "All Watchlist"

                if (selectedWatchlistItem.values.isNotEmpty()) {
                    showData(true)
                    manageWatchlistAdapter.setData(selectedWatchlistItem.values.toList(), isDelete)
                } else {
                    showData(false)
                }
            }
        }

        viewModel.getAddUserWatchlistGroupResult.observe(viewLifecycleOwner) { data ->
            if (data != null) {
                if (!isOnDestroy) {
                    val newWlCode = data.userWatchListGroup.wlCode
                    if (isRename) {
                        watchlistMapper.remove(wlCode)
                        isRename = false
                    }
                    val listStockParam = arrayListOf<String>()
                    data.userWatchListItemList.map { listStockParam.add(it.itemCode) }
                    watchlistMapper[newWlCode] = WatchListCategory(newWlCode, stockListString = listStockParam)
                    wlCode = newWlCode
                }
            }
        }

        viewModel.removeWatchListCategoryResult.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    showLoading()
                }
                is Resource.Success -> {
                    val res = it.data
                    if (res != null) {
                        when (res.status) {
                            0 -> {
                                watchlistMapper.remove(wlCode)
                                manageWatchlistAdapter.clearData()
                                watchlistMapper["All Watchlist"] = WatchListCategory("All Watchlist")
                                viewModel.getAllWatchlist(userId, sessionId)
                                wlCode = "All Watchlist"
                                binding.lyToolbar.tvTitle.text = "All Watchlist"
                                binding.lyToolbar.ivOption.visibility = View.GONE
                            }
                        }
                    }
                }

                else -> {}
            }
        }

        viewModel.removeItemCategoryResult.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    showLoading()
                }

                is Resource.Success -> {
                    val res = it.data
                    if (res != null) {
                        when (res.status) {
                            0 -> {
                                // update stock category that item was delete
                                val listStockParam = arrayListOf<String>()
                                val sortedListStock = it.data.userWatchListItemList.sortedBy { it.itemSeq }
                                sortedListStock.map { listStockParam.add(it.itemCode) }
                                watchlistMapper[wlCode] = WatchListCategory(wlCode, stockListString = listStockParam)

                                // check if the stocks still in another category
                                var isStillInAnothercategory = false
                                watchlistMapper.mapValues {
                                    if (it.key != "All Watchlist" && it.value.stockListString.contains(deletedStock)) {
                                        isStillInAnothercategory = true
                                    }
                                }
                                if (!isStillInAnothercategory) {
                                    val listStock = watchlistMapper["All Watchlist"]?.stockListString?.toMutableList()
                                    listStock?.removeIf { it == deletedStock }
                                    if (listStock != null) {
                                        watchlistMapper["All Watchlist"] = WatchListCategory("All Watchlist", stockListString = listStock)
                                    } else {
                                        watchlistMapper["All Watchlist"] = WatchListCategory("All Watchlist")
                                    }
                                }

                                // update ui
                                if (it.data.userWatchListItemList.size != 0) {
                                    viewModel.getStockParam(listStockParam)
                                } else {
                                    manageWatchlistAdapter.clearData()
                                    showData(false)
                                }

                            }
                        }
                    }
                }

                else -> {}
            }
        }
    }

    private fun getItemSearch(query: String) {
        val itemList = selectedWatchlistItem.values.toList()
        val searchList = itemList.filter { it.stockCode.contains(query, ignoreCase = true) || it.stockName.contains(query, ignoreCase = true) }

        if (searchList.isNotEmpty()) {
            val isDelete = binding.lyToolbar.tvTitle.text.toString() != "All Watchlist"
            manageWatchlistAdapter.setData(searchList, isDelete)
        } else {
            manageWatchlistAdapter.clearData()
        }
    }

    private fun reorderingItemWatchlist() {
        val wlCode = binding.lyToolbar.tvTitle.text.toString()
        val listItemWatchlist = manageWatchlistAdapter.getListData()
        val listStock = arrayListOf<String>()
        listItemWatchlist.map {
            listStock.add(it.stockCode)
        }

        viewModel.addUserWatchlist(userId, wlCode, listStock, sessionId, "*")

    }

    // click btn delete
    override fun onClickAny(valueAny: Any?) {
        valueAny as Bundle
        val itemCode = valueAny.getString(Args.EXTRA_PARAM_STR_ONE)
        val itemSeq = valueAny.getInt(Args.EXTRA_PARAM_INT_ONE)
        deletedStock = itemCode.toString()

        itemCode?.let {itemCode ->
            viewModel.removeItemCategory(userId, binding.lyToolbar.tvTitle.text.toString(), itemCode, itemSeq, sessionId)
        }
    }

    // check item move
    override fun onClickBoolean(value: Boolean?) {
        if (value != null) {
//            itemOnMoved = value
            reorderingItemWatchlist()
        }
    }

    // when click item
    override fun onClickStr(value: String?) {
        val bundle = Bundle().apply {
            putString(Args.EXTRA_PARAM_STR_ONE, value)
        }
        findNavController().navigate(R.id.stock_detail_fragment, bundle)
    }

    private fun showData(state: Boolean) {
        binding.rcvStock.visibility = if (state) View.VISIBLE else View.GONE
        binding.searchbar.visibility = if (state) View.VISIBLE else View.GONE
        binding.lyWatchlistEmpty.visibility = if (state) View.GONE else View.VISIBLE
    }
}