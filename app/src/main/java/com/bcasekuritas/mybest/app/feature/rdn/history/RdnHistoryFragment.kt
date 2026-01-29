package com.bcasekuritas.mybest.app.feature.rdn.history


import android.graphics.PorterDuff
import android.icu.util.Calendar
import android.os.Build
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.domain.dto.response.RdnHistoryItem
import com.bcasekuritas.mybest.app.domain.dto.response.source.HistoryRdnRes
import com.bcasekuritas.mybest.app.feature.activity.middle.MiddleActivity
import com.bcasekuritas.mybest.app.feature.rdn.adapter.FilterHistoryRdnAdapter
import com.bcasekuritas.mybest.app.feature.rdn.adapter.HistoryRdnAdapter
import com.bcasekuritas.mybest.databinding.FragmentRdnHistoryBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.common.convertToMillis
import com.bcasekuritas.mybest.ext.common.getPrevious90DaysInMillis
import com.bcasekuritas.mybest.ext.constant.NavKeys
import com.bcasekuritas.mybest.ext.delegate.ShowDialog
import com.bcasekuritas.mybest.ext.delegate.ShowDialogImpl
import com.bcasekuritas.mybest.ext.listener.OnClickAny
import com.bcasekuritas.mybest.ext.listener.OnClickStrInt
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import timber.log.Timber

@FragmentScoped
@AndroidEntryPoint
class RdnHistoryFragment : BaseFragment<FragmentRdnHistoryBinding, RdnHistoryViewModel>(),
    OnClickStrInt, ShowDialog by ShowDialogImpl(), OnClickAny {

    @FragmentScoped
    override val bindingVariable: Int = BR.vmRdnHistory
    override val viewModel: RdnHistoryViewModel by viewModels()
    override val binding: FragmentRdnHistoryBinding by autoCleaned {
        (FragmentRdnHistoryBinding.inflate(
            layoutInflater
        ))
    }

    private lateinit var historyList: ArrayList<HistoryRdnRes>
    private var strFilterList: List<String> = listOf()
    private var filterList: ArrayList<String> = arrayListOf()

    private lateinit var linearLayoutManager: LinearLayoutManager

    private val historyRdnAdapter: HistoryRdnAdapter by autoCleaned {
        HistoryRdnAdapter(
            requireContext(),
            this
        )
    }
    private val filterHistoryRdnAdapter: FilterHistoryRdnAdapter by autoCleaned {
        FilterHistoryRdnAdapter(
            this
        )
    }

    private var userId = ""
    private var accNo = ""
    private var sessionId = ""
    private var startDateCurrent = 0L
    private var endDateCurrent = 0L
    private var startDateDialog = 0L
    private var endDateDialog = 0L
    private var startDateFilter = 0L
    private var endDateFilter = 0L
    private var minDate = 0L
    private var sortType = "*"
    private var page = 0
    private var totalPage = 0
    private var isPageLoading = false


    companion object {
        fun newInstance() = RdnHistoryFragment()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun setupComponent() {
        minDate = getPrevious90DaysInMillis()

        binding.lyToolbarRdnHistory.tvLayoutToolbarMasterTitle.text =
            getString(R.string.text_history)
        binding.lyToolbarRdnHistory.ivLayoutToolbarMasterIconLeft.visibility = View.VISIBLE
        binding.lyToolbarRdnHistory.ivLayoutToolbarMasterIconLeft.setImageResource(R.drawable.ic_back)
        binding.lyToolbarRdnHistory.ivLayoutToolbarMasterIconRightOne.visibility = View.VISIBLE
        binding.lyToolbarRdnHistory.ivLayoutToolbarMasterIconRightOne.setImageResource(R.drawable.ic_filter_history)
        binding.lyToolbarRdnHistory.ivLayoutToolbarMasterIconRightOne.setColorFilter(R.color.textPrimary)
    }

    override fun initOnClick() {
        super.initOnClick()

        binding.lyToolbarRdnHistory.ivLayoutToolbarMasterIconLeft.setOnClickListener {
            onBackPressed()
        }

        binding.lyToolbarRdnHistory.ivLayoutToolbarMasterIconRightOne.setOnClickListener {

            startDateDialog = if (startDateFilter == 0L) startDateCurrent else startDateFilter
            endDateDialog = if (endDateFilter == 0L) endDateCurrent else endDateFilter

            showDialogRdnHistoryFilterBottom(
                filterList,
                startDateDialog,
                endDateDialog,
                parentFragmentManager
            )

            parentFragmentManager.setFragmentResultListener(
                NavKeys.KEY_FM_RDN_HISTORY,
                viewLifecycleOwner
            ) { _, result ->
                val confirmResult = result.getString(NavKeys.CONST_RES_RDN_HISTORY_FILTER)

                if (confirmResult == "RESULT_OK") {
                    filterList.clear()
                    page = 0
                    val strDateFrom = result.getString("StrDateFrom")
                    val strDateTo = result.getString("strDateTo")
                    val strDividend = result.getString("strDividend")
                    val strTopUp = result.getString("strTopUp")
                    val strWithdraw = result.getString("strWithdraw")
                    val date: String

                    if (strDateFrom.equals(strDateTo)) {
                        date = strDateFrom.toString()
                    } else {
                        date = "$strDateFrom - $strDateTo"
                    }

                    startDateFilter = convertToMillis(strDateFrom ?: "", "dd MMM yyyy")
                    endDateFilter = convertToMillis(strDateTo ?: "", "dd MMM yyyy")

                    startDateDialog = startDateFilter
                    endDateDialog = endDateFilter

                    if (strDividend.isNullOrEmpty()) {
                        viewModel.getRdnHistory(
                            userId,
                            accNo,
                            startDateFilter,
                            endDateFilter,
                            "",
                            sessionId,
                            page
                        )
                    } else {
                        viewModel.getRdnHistory(
                            userId,
                            accNo,
                            startDateFilter,
                            endDateFilter,
                            strDividend,
                            sessionId,
                            page
                        )
                        sortType =  strDividend
                    }



                    strFilterList = listOf(
                        date,
                        strDividend.toString(),
                        strTopUp.toString(),
                        strWithdraw.toString()
                    )

                    for (i in 0..3) {
                        if (strFilterList[i].isNotEmpty()) {
                            filterList.add(strFilterList[i])
                        }
                    }

                    checkFilterList(filterList, false)
                }
            }
        }
    }

    override fun setupListener() {
        super.setupListener()

        binding.swipeRefresh.setOnRefreshListener {
            page = 0
            if (filterList.size == 2) {
                viewModel.getRdnHistory(userId, accNo, startDateFilter, endDateFilter, sortType, sessionId, page)
            } else if (filterList.size == 1) {
                val type = filterList.get(0)
                when (type) {
                    "*", "V", "C", "W" -> viewModel.getRdnHistory( userId, accNo, startDateCurrent, endDateCurrent, sortType, sessionId, page)

                    else -> {
                        sortType = "*"
                        viewModel.getRdnHistory(userId, accNo, startDateFilter, endDateFilter, sortType, sessionId, page)
                    }
                }
            } else {
                sortType = "*"
                viewModel.getRdnHistory(userId, accNo, startDateCurrent, endDateCurrent, sortType, sessionId, page)
            }
        }

        binding.rcvRdnHistory.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val scrollOffset = recyclerView.computeVerticalScrollOffset()
                val scrollExtent = recyclerView.computeVerticalScrollExtent()
                val scrollRange = recyclerView.computeVerticalScrollRange()

                if (scrollOffset + scrollExtent >= scrollRange) {
                    page++
                    if (page < totalPage ){
                        viewModel.getRdnHistory(
                            userId,
                            accNo,
                            startDateCurrent,
                            endDateCurrent,
                            sortType,
                            sessionId,
                            page
                        )
                        isPageLoading = true
                    }
                    Log.d("RecyclerView", "Reached the bottom")
                }

//                    if (!isPageLoading && (visibleItemCount + lastVisibleItemPosition + 1) >= totalItemCount) {
//
//                        Timber.d("scrolldown rdn $page")
//                        page++
//                        viewModel.getRdnHistory(
//                            userId,
//                            accNo,
//                            startDateCurrent,
//                            endDateCurrent,
//                            sortType,
//                            sessionId,
//                            page
//                        )
//                        isPageLoading = true
//
//            }

            }
        })
    }

    override fun setupAdapter() {
        super.setupAdapter()

        linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rcvRdnHistory.layoutManager = linearLayoutManager
        binding.rcvRdnHistory.adapter = historyRdnAdapter

//        binding.rcvFilterRdnHistory.setHasFixedSize(true)
        binding.rcvFilterRdnHistory.adapter = filterHistoryRdnAdapter
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun initAPI() {
        super.initAPI()

        userId = prefManager.userId
        accNo = prefManager.accno
        sessionId = prefManager.sessionId

        val calendar = Calendar.getInstance()
        endDateCurrent = calendar.timeInMillis
        startDateCurrent = getPrevious90DaysInMillis()

        viewModel.getRdnHistory(userId, accNo, startDateCurrent, endDateCurrent, sortType, sessionId, page)
    }

    @RequiresApi(Build.VERSION_CODES.O)
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

        viewModel.rdnHistoryResult.observe(viewLifecycleOwner) { resource ->
            if (resource != null) {
                when (resource.status) {
                    0 -> {
                        totalPage = resource.page.totalPages
                        val rdnList = resource.accountCashMovementInfoList
                        val listData = rdnList.map { data ->
                            RdnHistoryItem(
                                data.clCode,
                                data.transCode,
                                data.transType,
                                data.transAmount,
                                data.currencyCode,
                                data.bankName,
                                data.bankAccountNo,
                                data.orderSource,
                                data.orderSourceReff,
                                data.status,
                                data.createdDate,
                                data.lastModifiedDate
                            )
                        }

                        val sortedList = listData.sortedByDescending { it.createdDate }
                        if (page > 0){
                            historyRdnAdapter.addData(sortedList)
                        } else {
                            historyRdnAdapter.setData(sortedList)
                        }
                    }
                }
            }

            isPageLoading = false
            binding.swipeRefresh.isRefreshing = false
        }
    }

    override fun onClickStrInt(valueStr: String?, valueInt: Int?) {
//        when(valueStr){
//            NavKeys.KEY_FM_RDN_DETAIL_HISTORY -> MiddleActivity.startIntent(requireActivity(), NavKeys.KEY_FM_RDN_DETAIL_HISTORY)
//            NavKeys.KEY_FM_RDN_HISTORY_FILTER -> {
//                filterList.removeAt(valueInt!!.toInt())
//                checkFilterList(filterList)
//            }
//        }


        filterList.removeAt(valueInt!!.toInt())
        checkFilterList(filterList, true)
//        when(valueStr) {
//            "*", "V", "C", "W"  -> {
//                filterList.removeAt(valueInt!!.toInt())
//                checkFilterList(filterList)
//            }
//            else -> {
//                filterList.removeAt(valueInt!!.toInt())
//                checkFilterList(filterList)
//            }
//        }
    }

    override fun onClickAny(valueAny: Any?) {
        if (valueAny is RdnHistoryItem) {
            MiddleActivity.startIntentParam(
                requireActivity(),
                NavKeys.KEY_FM_RDN_DETAIL_HISTORY,
                valueAny,
                ""
            )
        }
    }

    private fun checkFilterList(list: ArrayList<String>, isFilter: Boolean) {
        if (list.isEmpty()) {
            binding.lyToolbarRdnHistory.ivLayoutToolbarMasterIconRightOne.setColorFilter(
                ContextCompat.getColor(requireContext(), R.color.brandPrimaryNightBlue),
                PorterDuff.Mode.SRC_ATOP
            )
            sortType = "*"
            viewModel.getRdnHistory(userId, accNo, startDateCurrent, endDateCurrent, sortType, sessionId, page)
            startDateDialog = 0L
            endDateDialog = 0L

            binding.rcvFilterRdnHistory.visibility = View.GONE
        } else {
            if (isFilter) {
                list.forEach {
                    when (it) {
                        "*", "V", "C", "W" -> {
                            viewModel.getRdnHistory(
                                userId,
                                accNo,
                                startDateCurrent,
                                endDateCurrent,
                                it,
                                sessionId,
                                page
                            )
                            startDateDialog = 0L
                            endDateDialog = 0L
                        }

                        else -> {
                            sortType = "*"
                            viewModel.getRdnHistory(
                                userId,
                                accNo,
                                startDateFilter,
                                endDateFilter,
                                sortType,
                                sessionId,
                                page
                            )
                        }
                    }
                }
            }

            binding.lyToolbarRdnHistory.ivLayoutToolbarMasterIconRightOne.setColorFilter(
                ContextCompat.getColor(requireContext(), R.color.brandSecondaryBlue),
                PorterDuff.Mode.SRC_ATOP
            )
            binding.rcvFilterRdnHistory.visibility = View.VISIBLE
            filterHistoryRdnAdapter.setData(list)
        }
    }

}