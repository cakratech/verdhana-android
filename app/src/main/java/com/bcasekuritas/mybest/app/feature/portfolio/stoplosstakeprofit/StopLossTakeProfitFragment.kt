package com.bcasekuritas.mybest.app.feature.portfolio.stoplosstakeprofit

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.domain.dto.request.WithdrawOrderRequest
import com.bcasekuritas.mybest.app.domain.dto.response.AdvancedOrderDetail
import com.bcasekuritas.mybest.app.feature.activity.main.MainActivity
import com.bcasekuritas.mybest.app.feature.activity.main.SharedMainViewModel
import com.bcasekuritas.mybest.app.feature.activity.middle.MiddleActivity
import com.bcasekuritas.mybest.app.feature.portfolio.stoplosstakeprofit.adapter.StopLossTakeProfitAdapter
import com.bcasekuritas.mybest.databinding.FragmentStopLossTakeProfitBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.common.validateSessionPin
import com.bcasekuritas.mybest.ext.constant.Args
import com.bcasekuritas.mybest.ext.constant.NavKeys
import com.bcasekuritas.mybest.ext.converter.GET_BRACKET_STATUS
import com.bcasekuritas.mybest.ext.delegate.ShowDropDown
import com.bcasekuritas.mybest.ext.delegate.ShowDropDownImpl
import com.bcasekuritas.mybest.ext.delegate.ShowSnackBarImpl
import com.bcasekuritas.mybest.ext.delegate.ShowSnackBarInterface
import com.bcasekuritas.mybest.ext.listener.OnClickAny
import com.bcasekuritas.mybest.ext.listener.OnClickAnyInt
import com.bcasekuritas.rabbitmq.proto.bcas.AdvancedOrderInfo
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import timber.log.Timber

@FragmentScoped
@AndroidEntryPoint
class StopLossTakeProfitFragment: BaseFragment<FragmentStopLossTakeProfitBinding, StopLossTakeProfitViewModel>(),
    OnClickAnyInt, OnClickAny, ShowDropDown by ShowDropDownImpl(), ShowSnackBarInterface by ShowSnackBarImpl() {

    override val bindingVariable: Int = BR.vmStopLossTakeProfit
    override val viewModel: StopLossTakeProfitViewModel by viewModels()
    override val binding: FragmentStopLossTakeProfitBinding by autoCleaned { (FragmentStopLossTakeProfitBinding.inflate(layoutInflater)) }

    private val mAdapter: StopLossTakeProfitAdapter by autoCleaned { StopLossTakeProfitAdapter(requireContext(), this, this) }

    private val listItem = arrayListOf<AdvancedOrderInfo>()
    private var orderId = ""
    private var stockCodeWithdraw = ""
    private var buySell = ""

    private var userId = ""
    private var accNo = ""
    private var isWithdraw = false

    override fun setupComponent() {
        super.setupComponent()
        binding.toolbar.tvLayoutToolbarMasterTitle.text = "Advanced Order List"

        val previousStack = findNavController().previousBackStackEntry?.destination?.id ?: 0
        if (previousStack == R.id.condition_advanced_fragment) {
            val onBackPressedCallback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    MainActivity.startIntentParam(
                        requireActivity(),
                        NavKeys.KEY_FM_TAB_PORTFOLIO,
                        1,
                        false
                    )
                }
            }

            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)
        }

        binding.apply {
            swipeRefresh.setOnRefreshListener {
                viewModel.getAdvanceOrderList(userId, accNo)
            }
        }

    }

    override fun initOnClick() {
        super.initOnClick()
        binding.apply {
            toolbar.ivLayoutToolbarMasterIconLeft.setOnClickListener {
                onBackPressed()
            }
        }
    }

    override fun setupAdapter() {
        super.setupAdapter()
        binding.rcvStopLossTakeProfit.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }

    override fun setupListener() {
        super.setupListener()
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
                filterItems(binding.etSearchStopLoss.getText())
            }
        }

        binding.etSearchStopLoss.setTextWatcher(searchTextWatcher)

    }

    private fun filterItems(query: String) {
        val filteredItems = listItem
            .filter { item -> item.stockCode.contains(query, ignoreCase = true) }
            .sortedByDescending { item -> item.orderTime }

        mAdapter.setData(filteredItems)
    }

    override fun initAPI() {
        super.initAPI()
        userId = prefManager.userId
        accNo = prefManager.accno

        isWithdraw = false
        viewModel.getAdvanceOrderList(userId, accNo)
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.imqConnectionListener.connListenerLiveData.observe(this) { value ->
            when (value) {
                "Recovered" -> {
                    viewModel.imqConnectionListener.onListener("")
                    viewModel.getAdvanceOrderList(userId, accNo)
                }

                else -> {}
            }
        }

        viewModel.getAdvanceOrderListResult.observe(viewLifecycleOwner){
            if (it.size != 0) {
                listItem.clear()
                listItem.addAll(it)
                val sortedList = it.sortedByDescending { item -> item.orderTime }
                mAdapter.setData(sortedList)
                binding.tvTotalStock.text = "My Stocks (${it.size})"
                binding.swipeRefresh.isRefreshing = false
            }
            binding.swipeRefresh.isRefreshing = false
        }
    }


    private fun showDialogWithdraw() {
        if (isWithdraw) {
            showDialogWithdrawOrderBottom(
                NavKeys.KEY_FM_TAB_PORTFOLIO_ORDER,
                childFragmentManager
            )

            // withdraw dialog
            childFragmentManager.setFragmentResultListener(
                NavKeys.KEY_FM_TAB_PORTFOLIO_ORDER,
                viewLifecycleOwner
            ) { _, result ->

                // withdraw dialog
                val confirmResultWithdraw = result.getString(NavKeys.CONST_RES_WITHDRAW_ORDER_CONFIRM)
                if (confirmResultWithdraw == "RESULT_OK") {
                    if (result.getBoolean("confirm")) {

                        sendWithdraw(orderId)
                        showSnackBarTop(requireContext(), binding.root, "success", R.drawable.ic_success, "Withdraw ${buySell} $stockCodeWithdraw is submitted", "", requireActivity(), "")
                        Handler(Looper.getMainLooper()).postDelayed({
                            viewModel.getAdvanceOrderList(userId, accNo)
                        }, 1000)

                    }
                }
            }
        }
    }

    //when click option menu
    override fun onClickAnyInt(valueAny: Any?, valueInt: Int) {
        valueAny as AdvancedOrderInfo
        stockCodeWithdraw = valueAny.stockCode
        buySell = if(valueAny.buySell.equals("B")) "Buy" else "Sell"
        orderId = valueAny.orderID
        isWithdraw = true
        showDialogWithdraw()
    }

    //when click item
    override fun onClickAny(valueAny: Any?) {
        valueAny as AdvancedOrderInfo
        val data = AdvancedOrderDetail(
            valueAny.orderID,
            valueAny.stockCode,
            valueAny.orderTime,
            valueAny.status,
            valueAny.bracketStatus.GET_BRACKET_STATUS(),
            valueAny.buySell,
            valueAny.validUntil,
            valueAny.ordQty,
            valueAny.totalMatch,
            valueAny.ordPrice,
            valueAny.advType,
            valueAny.splitBlockSize,
            valueAny.splitNumber,
            valueAny.remark,
            valueAny.stopLossCriteria.opr,
            valueAny.stopLossCriteria.triggerVal.toDouble(),
            valueAny.stopLossCriteria.triggerOrder.ordQty.toDouble(),
            valueAny.stopLossCriteria.triggerOrder.ordPrice.toDouble(),
            valueAny.takeProfitCriteria.opr,
            valueAny.takeProfitCriteria.triggerVal.toDouble(),
            valueAny.takeProfitCriteria.triggerOrder.ordQty.toDouble(),
            valueAny.takeProfitCriteria.triggerOrder.ordPrice.toDouble(),
            valueAny.opr,
            valueAny.triggerPrice.toDouble(),
            valueAny.triggerCategory,
            valueAny.stopLossCriteria.triggerCategory,
            valueAny.takeProfitCriteria.triggerCategory,
            valueAny.takeProfitMQty,
            valueAny.stopLossMQty,
            valueAny.mQty

        )
        val bundle = Bundle().apply {
            putParcelable(Args.EXTRA_PARAM_OBJECT_TWO, data)
        }

        findNavController().navigate(R.id.order_detail_fragment, bundle)
    }

    private fun sendWithdraw(orderId: String) {
        val accNo = prefManager.accno
        val userId = prefManager.userId
        val sessionId = prefManager.sessionId

        val sendWithdraw = WithdrawOrderRequest(
            oldCliOrderRef = orderId,
            orderID = orderId,
            inputBy = userId,
            sessionId = sessionId,
            accNo = accNo
        )

        viewModel.sendWithdraw(sendWithdraw)
    }
}