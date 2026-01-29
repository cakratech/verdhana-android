package com.bcasekuritas.mybest.app.feature.pricealert

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.domain.dto.response.PriceAlertItem
import com.bcasekuritas.mybest.app.feature.pricealert.adapter.PriceAlertAdapter
import com.bcasekuritas.mybest.app.feature.pricealert.adapter.PriceAlertSentAdapter
import com.bcasekuritas.mybest.app.feature.stockdetail.StockDetailSharedViewModel
import com.bcasekuritas.mybest.databinding.FragmentPriceAlertBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.constant.Args
import com.bcasekuritas.mybest.ext.constant.NavKeys
import com.bcasekuritas.mybest.ext.delegate.ShowDialog
import com.bcasekuritas.mybest.ext.delegate.ShowDialogImpl
import com.bcasekuritas.mybest.ext.delegate.ShowSnackBarImpl
import com.bcasekuritas.mybest.ext.delegate.ShowSnackBarInterface
import com.bcasekuritas.mybest.ext.listener.OnClickAnyInt
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@AndroidEntryPoint
class PriceAlertFragment : BaseFragment<FragmentPriceAlertBinding, PriceAlertViewModel>(), OnClickAnyInt, ShowSnackBarInterface by ShowSnackBarImpl(), ShowDialog by ShowDialogImpl()  {

    @FragmentScoped
    override val bindingVariable: Int = BR.vmPriceAlert
    override val viewModel: PriceAlertViewModel by viewModels()
    override val binding: FragmentPriceAlertBinding by autoCleaned {
        (FragmentPriceAlertBinding.inflate(
            layoutInflater
        ))
    }

    private val priceAlertAdapter: PriceAlertAdapter by autoCleaned { PriceAlertAdapter(requireContext(),this, prefManager.urlIcon) }
    private val priceAlertSentAdapter: PriceAlertSentAdapter by autoCleaned { PriceAlertSentAdapter(requireContext(), prefManager.urlIcon) }

    lateinit var sharedViewModel: StockDetailSharedViewModel

    private var userId = ""
    private var sessionId = ""
    private var stockCode = ""
    private var deleteId = 0L
    private var currentTab = 1

    private val listItem = arrayListOf<PriceAlertItem>()

    companion object {
        fun newInstance() = PriceAlertFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity()).get(StockDetailSharedViewModel::class.java)

        arguments?.let {
            it.getString(Args.EXTRA_PARAM_STR_ONE)?.let {
                stockCode = it
            }
        }
    }

    override fun setupAdapter() {
        super.setupAdapter()
        binding.rcvPriceAlert.apply {
            adapter = priceAlertAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        binding.rcvPriceAlertSent.apply {
            adapter = priceAlertSentAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }

    override fun setupComponent() {
        super.setupComponent()
        binding.apply {
            searchBar.setHint("Search code or name")
            toolbar.tvLayoutToolbarMasterTitle.text = "Manage Price Alert"

            Handler(Looper.getMainLooper()).postDelayed({

                if (stockCode.isNotEmpty()){
                    binding.searchBar.setText(stockCode)
                }

            }, 200)

            swplPriceAlert.setOnRefreshListener {
                viewModel.getListPriceAlert(userId, sessionId, "*")
            }
        }
        pageChange()
    }

    private fun pageChange() {
        binding.apply {
            chipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
                for (chipId in checkedIds) {
                    val selectedChip = chipGroup.findViewById(chipId) as Chip
                    when (selectedChip.text.toString()) {
                        "Active" -> {
                            currentTab = 1
                            rcvPriceAlertSent.visibility = View.GONE
                        }
                        "Sent" -> {
                            currentTab = 2
                            rcvPriceAlert.visibility = View.GONE
                        }
                    }
                }
                viewModel.getListPriceAlert(userId, sessionId, "*")
            }
        }
    }

    override fun initOnClick() {
        super.initOnClick()
        binding.apply {
            toolbar.ivLayoutToolbarMasterIconLeft.setOnClickListener {
                onBackPressed()
            }

            btnSetAlert.setOnClickListener {
                findNavController().navigate(R.id.create_edit_price_alert_fragment)
            }

            btnAddPriceAlert.setOnClickListener {
                if (stockCode.isNotEmpty()) {
                    val bundle = Bundle().apply {
                        putString(Args.EXTRA_PARAM_STR_ONE, stockCode)
                    }
                    findNavController().navigate(R.id.create_edit_price_alert_fragment, bundle)
                } else {
                    findNavController().navigate(R.id.create_edit_price_alert_fragment)

                }
            }
        }
    }

    override fun initAPI() {
        super.initAPI()
        userId = prefManager.userId
        sessionId = prefManager.sessionId
        viewModel.getListPriceAlert(userId, sessionId, "*")
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.imqConnectionListener.connListenerLiveData.observe(this) { value ->
            when (value) {
                "Recovered" -> {
                    viewModel.getListPriceAlert(userId, sessionId, "*")
                }

                else -> {}
            }
        }

        viewModel.getListPriceAlertResult.observe(viewLifecycleOwner) {data ->
            listItem.clear()

            val searchStock = binding.searchBar.getText()
            if (currentTab == 1) {
                priceAlertAdapter.clearData()
                val filteredList = data.filter { it.triggerAt == 0L }
                listItem.addAll(filteredList)

                if (searchStock.isNotEmpty()) {
                    searchItems(searchStock)
                } else {
                    if (filteredList.isNotEmpty()) {
                        val sortedList = filteredList.sortedByDescending { it.id }
                        priceAlertAdapter.setData(sortedList)
                        binding.rcvPriceAlert.visibility = View.VISIBLE
                    } else {
                        binding.rcvPriceAlert.visibility = View.GONE
                        binding.groupNoPriceAlert.visibility = View.VISIBLE
                        binding.groupDataExist.visibility = View.GONE
                    }
                }


            } else {
                priceAlertSentAdapter.clearData()
                val filteredList = data.filter { it.triggerAt != 0L }
                listItem.addAll(filteredList)

                if (searchStock.isNotEmpty()) {
                    searchItems(searchStock)
                } else {
                    if (filteredList.isNotEmpty()) {
                        val sortedList = filteredList.sortedByDescending { it.triggerAt }
                        priceAlertSentAdapter.setData(sortedList)
                        binding.rcvPriceAlertSent.visibility = View.VISIBLE
                    } else {
                        binding.rcvPriceAlertSent.visibility = View.GONE
                        binding.groupNoPriceAlert.visibility = View.VISIBLE
                        binding.groupDataExist.visibility = View.GONE
                    }
                }
            }

            binding.swplPriceAlert.isRefreshing = false
        }

        viewModel.isPriceAlertEmpty.observe(viewLifecycleOwner) {isEmpty ->
            binding.groupDataExist.visibility = if (isEmpty) View.GONE else View.VISIBLE
            binding.groupNoPriceAlert.visibility = if (isEmpty) View.VISIBLE else View.GONE

            if (currentTab == 1) {
                binding.rcvPriceAlert.visibility = if (isEmpty) View.GONE else View.VISIBLE
            } else {
                binding.rcvPriceAlertSent.visibility = if (isEmpty) View.GONE else View.VISIBLE
            }

            binding.swplPriceAlert.isRefreshing = false
        }

        viewModel.removePriceAlertResult.observe(viewLifecycleOwner) {result ->
            if (result != null) {
                when (result.status) {
                    0 -> {
                        showSnackBarTop(requireContext(), binding.root, "success", R.drawable.ic_success, "Price Alert Has Been Removed", "Set up another", requireActivity(), "")
                        viewModel.getListPriceAlert(userId, sessionId, "*")
                    }
                    else -> {}
                }
            }
        }

        sharedViewModel.getPopUpSuccessPriceAlert.observe(viewLifecycleOwner) {isSuccess ->
            if (isSuccess != null) {
                if (isSuccess == true) {
                    showSnackBarTop(requireContext(), binding.root, "success", R.drawable.ic_success, "Price Alert Has Been Set", "You will receive a notification when stock price has reached your target.", requireActivity(), "")
                    sharedViewModel.clearValuePopUpPriceAlert()
                } else {
                    showSnackBarTop(requireContext(), binding.root, "error", R.drawable.ic_error, "", "Can't add an alert with the same price as active alert", requireActivity(), "")
                    sharedViewModel.clearValuePopUpPriceAlert()
                }
            }
        }
    }

    override fun setupListener() {
        super.setupListener()
        parentFragmentManager.setFragmentResultListener(
            NavKeys.KEY_FM_PRICE_ALERT,
            viewLifecycleOwner
        ) {_, result ->
            val confirmResultWithdraw = result.getString(NavKeys.CONST_RES_REMOVE_PRICE_ALERT)
            if (confirmResultWithdraw == "RESULT_OK") {
                if (result.getBoolean("confirm")) {

                    viewModel.deletePriceAlert(userId, sessionId, deleteId)
                }
            }
        }

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
                searchItems(binding.searchBar.getText())
            }
        }

        binding.searchBar.setTextWatcher(searchTextWatcher)
    }

    override fun onClickAnyInt(valueAny: Any?, valueInt: Int) {
        valueAny as PriceAlertItem
        when (valueInt) {
            // 0 edit, 1 delete
            0 -> {
                val bundle = Bundle().apply {
                    putParcelable(Args.EXTRA_PARAM_OBJECT, valueAny)
                    putBoolean(Args.EXTRA_PARAM_BOOLEAN, true)
                }
                findNavController().navigate(R.id.create_edit_price_alert_fragment, bundle)
            }
            1 -> {
                deleteId = valueAny.id
                showDialogRemovePriceAlert(NavKeys.KEY_FM_PRICE_ALERT, parentFragmentManager)
            }
        }
    }

    private fun searchItems(query: String) {
        val filteredItems = listItem
            .filter { item -> item.stockCode.contains(query, ignoreCase = true) }
            .sortedByDescending { item ->
                if (currentTab == 1) item.id else item.triggerAt
            }

        if (currentTab == 1) {
            priceAlertAdapter.setData(filteredItems)
        } else {
            priceAlertSentAdapter.setData(filteredItems)
        }
    }
}