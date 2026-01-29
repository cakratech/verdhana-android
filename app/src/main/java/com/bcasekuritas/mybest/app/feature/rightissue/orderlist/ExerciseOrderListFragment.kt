package com.bcasekuritas.mybest.app.feature.rightissue.orderlist

import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.domain.dto.request.WithdrawExerciseOrderRequest
import com.bcasekuritas.mybest.app.feature.rightissue.orderlist.adapter.ExerciseOrderListAdapter
import com.bcasekuritas.mybest.databinding.FragmentExerciseOrderListBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.common.getIpAddress
import com.bcasekuritas.mybest.ext.common.getRandomString
import com.bcasekuritas.mybest.ext.constant.Args
import com.bcasekuritas.mybest.ext.constant.NavKeys
import com.bcasekuritas.mybest.ext.delegate.ShowDialog
import com.bcasekuritas.mybest.ext.delegate.ShowDialogImpl
import com.bcasekuritas.mybest.ext.delegate.ShowSnackBarImpl
import com.bcasekuritas.mybest.ext.delegate.ShowSnackBarInterface

@FragmentScoped
@AndroidEntryPoint
class ExerciseOrderListFragment: BaseFragment<FragmentExerciseOrderListBinding, ExerciseOrderListViewModel>()
    , ShowDialog by ShowDialogImpl(), ShowSnackBarInterface by ShowSnackBarImpl() {

    override val bindingVariable: Int = BR.vmExerciseOrderList
    override val viewModel: ExerciseOrderListViewModel by viewModels()
    override val binding: FragmentExerciseOrderListBinding by autoCleaned { (FragmentExerciseOrderListBinding.inflate(layoutInflater)) }

    private val adapter: ExerciseOrderListAdapter by autoCleaned { ExerciseOrderListAdapter(requireContext()) }

    private var ipAddress = ""
    private var transCode: String = ""
    private var userId: String = ""
    private var accNo: String = ""
    private var sessionId: String = ""

    private var showOrderSuccessDialog = false

    override fun setupArguments() {
        super.setupArguments()
        arguments?.let {
            showOrderSuccessDialog = it.getBoolean(Args.EXTRA_PARAM_BOOLEAN)
        }
    }

    override fun setupAdapter() {
        super.setupAdapter()
        binding.apply {
            rcvExerciseOrderList.adapter = adapter
            rcvExerciseOrderList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

    }


    override fun setupComponent() {
        super.setupComponent()
        binding.apply {
            toolbar.tvLayoutToolbarMasterTitle.text = "Exercise Order List"
            toolbar.ivLayoutToolbarMasterIconLeft.setOnClickListener {
                onBackPressed()
            }

            if (showOrderSuccessDialog) {
                showSnackBarTop(
                    requireContext(),
                    binding.root,
                    "success",
                    R.drawable.ic_success,
                    "Order Placed",
                    getString(R.string.desc_snackbar_order_success_exercise), requireActivity(), NavKeys.KEY_FM_EXERCISE_ORDER_LIST
                )
            }
        }
    }

    override fun setupListener() {
        super.setupListener()
        childFragmentManager.setFragmentResultListener(NavKeys.KEY_FM_EXERCISE_ORDER_LIST, viewLifecycleOwner) { _, result ->
            // withdraw dialog
            val confirmResultWithdraw = result.getString(NavKeys.CONST_RES_WITHDRAW_ORDER_CONFIRM)
            if (confirmResultWithdraw == "RESULT_OK") {
                if (result.getBoolean("confirm")) {
                    sendWithdraw()
                    showSnackBarTop(requireContext(), binding.root, "success", R.drawable.ic_success, "Withdraw Buy is done", "", requireActivity(), "")
                    viewModel.getExerciseOrderList("A", listOf(accNo), userId, sessionId)
                }
            }
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.getExerciseOrderList("A", listOf(accNo), userId, sessionId)
        }
    }

    override fun initAPI() {
        super.initAPI()

        userId = prefManager.userId
        accNo  = prefManager.accno
        sessionId = prefManager.sessionId

        ipAddress = getIpAddress(requireContext())
        viewModel.getIpAddress()

        viewModel.getExerciseOrderList("A", listOf(accNo), userId, sessionId)
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.getIpAddressResult.observe(viewLifecycleOwner) {value ->
            ipAddress = value.ifEmpty { getIpAddress(requireContext()) }
        }

        viewModel.exerciseOrderListResult.observe(viewLifecycleOwner) {
            if (it?.size != 0) {
                if (it != null) {
                    adapter.setData(it.sortedByDescending { it.transDate })
                }
            }
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun sendWithdraw() {
        val accNo = prefManager.accno
        val userId = prefManager.userId
        val sessionId = prefManager.sessionId

        val withdrawExerciseOrderRequest = WithdrawExerciseOrderRequest(
            sessionId,
            transCode,
            getRandomString(),
            userId,
            ipAddress,
            accNo
        )

        viewModel.sendWithdraw(withdrawExerciseOrderRequest)
    }

//    override fun onClickStr(value: String?) {
//        if (value?.isNotEmpty() == true) {
//            transCode = value
//        }
//        showDialogWithdrawOrderBottom(NavKeys.KEY_FM_EXERCISE_ORDER_LIST, childFragmentManager)
//    }
//
//    override fun onClickAny(valueAny: Any?) {
//        val bundle = Bundle().apply {
//            valueAny as ExerciseOrderListItem
//            putParcelable(Args.EXTRA_PARAM_OBJECT, valueAny)
//        }
//
//        findNavController().navigate(R.id.exercise_detail_fragment, bundle)
//    }
}