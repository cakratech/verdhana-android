package com.bcasekuritas.mybest.app.feature.rightissue

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.domain.dto.response.RightIssueItem
import com.bcasekuritas.mybest.app.domain.dto.response.RightIssueParcelable
import com.bcasekuritas.mybest.app.feature.rightissue.adapter.RightIssueAdapter
import com.bcasekuritas.mybest.databinding.FragmentRightIssuesBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.common.getCurrentTimeInMillis
import com.bcasekuritas.mybest.ext.common.getStartTimeExercise
import com.bcasekuritas.mybest.ext.constant.Args
import com.bcasekuritas.mybest.ext.date.DateUtils
import com.bcasekuritas.mybest.ext.listener.OnClickAny

@FragmentScoped
@AndroidEntryPoint
class RightIssueFragment: BaseFragment<FragmentRightIssuesBinding, RightIssueViewModel>(), OnClickAny {
    override val bindingVariable: Int = BR.vmRightIssue
    override val viewModel: RightIssueViewModel by viewModels()
    override val binding: FragmentRightIssuesBinding by autoCleaned{ (FragmentRightIssuesBinding.inflate(layoutInflater)) }

    private val adapter: RightIssueAdapter by autoCleaned { RightIssueAdapter(prefManager.urlIcon, this) }

    private val rightIssueEndtimeMap = mutableMapOf<String, Long>()

    private var userId = ""
    private var accNo = ""
    private var sessionId = ""

    private var startTime = 0L
    private var endTime = 0L

    override fun setupComponent() {
        super.setupComponent()
        binding.apply {
            toolbar.tvLayoutToolbarMasterTitle.text = "Right Issues"
            toolbar.ivLayoutToolbarMasterIconRightOne.visibility = View.VISIBLE
            toolbar.ivLayoutToolbarMasterIconRightOne.setImageResource(R.drawable.ic_file)
        }
    }

    override fun initOnClick() {
        super.initOnClick()

        binding.toolbar.ivLayoutToolbarMasterIconLeft.setOnClickListener {
            onBackPressed()
        }

        binding.toolbar.ivLayoutToolbarMasterIconRightOne.setOnClickListener {
            findNavController().navigate(R.id.exercise_order_list_fragment)
        }
    }

    override fun setupAdapter() {
        super.setupAdapter()
        binding.apply {
            rcvRightIssue.adapter = adapter
            rcvRightIssue.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }

    override fun initAPI() {
        super.initAPI()
        userId = prefManager.userId
        accNo = prefManager.accno
        sessionId = prefManager.sessionId

        viewModel.getExerciseSession(userId, sessionId)
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

        viewModel.exerciseSessionResult.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                startTime = response.startTime
                endTime = response.endTime
            }
            viewModel.getRightIssueInfo(userId, accNo, sessionId)
        }

        viewModel.rightIssueInfoResult.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                binding.lyEmptyRightIssues.visibility = View.GONE
                binding.rcvRightIssue.visibility = View.VISIBLE
                adapter.setData(it.filterNotNull())
                it.forEach { item ->
                    if (item != null) {
                        rightIssueEndtimeMap[item.stockCode] = item.endTime
                    }
                }
            } else {
                binding.lyEmptyRightIssues.visibility = View.VISIBLE
                binding.rcvRightIssue.visibility = View.GONE
            }
        }
    }

    override fun onClickAny(valueAny: Any?) {
        valueAny as RightIssueItem

        if (getStartTimeExercise(startTime, endTime)) {
            val endTime = rightIssueEndtimeMap[valueAny.stockCode]?:0L
            val currentTime = getCurrentTimeInMillis()
            if (endTime != 0L && currentTime < endTime) {
                val bundle = Bundle().apply {
                    putParcelable(Args.EXTRA_PARAM_OBJECT, RightIssueParcelable(
                        valueAny.stockCode,
                        instrumentCode = valueAny.instrumentCode,
                        instrumentType = valueAny.instrumentType,
                        price = valueAny.price,
                        status = valueAny.status,
                        maxQty = valueAny.maxQty,
                        stockPosQty = valueAny.stockPosQty,
                        currentPrice = valueAny.currentPrice,
                        totalValue = valueAny.totalValue,
                        startDate = valueAny.startDate,
                        endDate = valueAny.endDate,
                        remarks = valueAny.remarks,
                        stockName = valueAny.stockName
                    ))
                }
                findNavController().navigate(R.id.exercise_fragment, bundle)
            } else {
                showDialogExerciseExpiredBottom(parentFragmentManager)
            }
        } else {
            val startHour = DateUtils.convertLongToDate(startTime, "HH:mm")
            showDialogExerciseNotAvailable(parentFragmentManager, startHour)
        }

    }
}

