package com.bcasekuritas.mybest.app.feature.dialog.bottom.runningtrade

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import androidx.lifecycle.ViewModelProvider
import com.bcasekuritas.mybest.app.base.dialog.withoutvm.BaseBottomSheet
import com.bcasekuritas.mybest.app.data.layout.UIDialogRunningTradeModel
import com.bcasekuritas.mybest.app.domain.dto.request.WatchListCategory
import com.bcasekuritas.mybest.app.feature.activity.main.MainActivity
import com.bcasekuritas.mybest.app.feature.dialog.order.viewmodel.DialogOrderBuyViewModel
import com.bcasekuritas.mybest.databinding.ButtonFilterRunningTradeBinding
import com.bcasekuritas.mybest.databinding.DialogFilterRunningTradeBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.constant.Args
import com.bcasekuritas.mybest.ext.constant.NavKeys
import com.bcasekuritas.mybest.ext.converter.formatPriceWithoutDecimal
import com.bcasekuritas.mybest.ext.converter.removeSeparator
import com.bcasekuritas.mybest.ext.delegate.ShowDropDown
import com.bcasekuritas.mybest.ext.delegate.ShowDropDownImpl
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import com.bcasekuritas.rabbitmq.proto.news.ViewIndexSector
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@AndroidEntryPoint
class DialogFilterRunningTrade(private val model: UIDialogRunningTradeModel, private val defaultFilter: UIDialogRunningTradeModel, private val isFirstOpen: Boolean): BaseBottomSheet<DialogFilterRunningTradeBinding>(), ShowDropDown by ShowDropDownImpl() {

    override val binding: DialogFilterRunningTradeBinding by autoCleaned { (DialogFilterRunningTradeBinding.inflate(layoutInflater)) }
    private lateinit var mViewModel: DialogFilterRunningTradeViewModel
    private lateinit var stickyButtonBinding: ButtonFilterRunningTradeBinding

    private var indexList = mutableMapOf<String, ViewIndexSector>()
    private var sectorList = mutableMapOf<String, ViewIndexSector>()

    private var category = 0
    private var indexSectorId = 0L
    private var listStockSelected = arrayListOf<String>()

    private var userId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(requireActivity()).get(DialogFilterRunningTradeViewModel::class.java)
        stickyButtonBinding = ButtonFilterRunningTradeBinding.inflate(LayoutInflater.from(requireContext()))
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog: Dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        dialog.setOnShowListener {
            (dialog as BottomSheetDialog).behavior.state = BottomSheetBehavior.STATE_EXPANDED
            (dialog as BottomSheetDialog).behavior.isFitToContents = true

            val containerLayout = dialog?.findViewById(
                com.google.android.material.R.id.container
            ) as? FrameLayout

            val layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM
            )

            containerLayout?.addView(
                stickyButtonBinding?.root,
                layoutParams
            )

            stickyButtonBinding?.root?.viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    stickyButtonBinding?.root?.viewTreeObserver?.removeOnGlobalLayoutListener(this)

                    val height = stickyButtonBinding?.root?.measuredHeight
                    binding.root.setPadding(0, 0, 0, height ?: 0)
                }

            })

            stickyButtonBinding?.buttonApply?.setOnClickListener {
                sendCallback()
                dismiss()
            }

            stickyButtonBinding?.buttonReset?.setOnClickListener {
                resetFilter()
            }
        }

        return dialog
    }

    override fun initAPI() {
        super.initAPI()
        mViewModel.getIndexSectorData(userId, prefManager.sessionId)

    }

    override fun initOnClick() {
        super.initOnClick()
        binding.apply {
            btnClose.setOnClickListener {
                dismiss()
            }

            tvIndexVal.setOnClickListener {
                val listItem = if (category == 1) indexList.keys.toList() else sectorList.keys.toList()
                val hint = if (category == 1) "index" else "sector"
                showDropDownStringSearchable(
                    requireContext(),
                    listItem,
                    viewDropdown,
                    "Search $hint code"
                ) { _, value ->
                    tvIndexVal.text = value

                    indexSectorId = if (category == 1) indexList[value]?.id?: 0L else sectorList[value]?.id?: 0L
                    mViewModel.getStockIndexSector(userId, prefManager.sessionId, indexSectorId)
                }
            }
        }
    }

    override fun setupComponent() {
        super.setupComponent()
        userId = prefManager.userId
        binding.apply {
            chipListener()
        }
    }

    private fun setupFilter(filter: UIDialogRunningTradeModel) {
        binding.apply {
            category = filter.category
            var selectedIndexOrSector = ""
            when (filter.category) {
                0 -> chipAllStocks.isChecked = true
                1 -> {
                    chipIndex.isChecked = true
                    selectedIndexOrSector = indexList.values.find { item -> item.id == filter.indexSector }?.indexCode?: ""
                }
                2 -> {
                    chipSubsector.isChecked = true
                    selectedIndexOrSector = sectorList.values.find { item -> item.id == filter.indexSector }?.indexCode?: ""
                }
                3 -> chipFocusStock.isChecked = true
                4 -> chipStocksInPortfolio.isChecked = true
                else -> chipAllStocks.isChecked = true
            }

            groupIndexSector.visibility = if (category == 1 || category == 2) View.VISIBLE else View.GONE
            tvIndex.text = if (category == 1) "Index" else "Sector"
            tvIndexVal.text = if ((category == 1 || category == 2) && selectedIndexOrSector.isNotEmpty()) selectedIndexOrSector else "All"

            etPriceFrom.setText(if (filter.priceFrom != 0.0) "Rp${filter.priceFrom.formatPriceWithoutDecimal()}" else "Rp0")
            etPriceTo.setText(if (filter.priceTo != 0.0) filter.priceTo.formatPriceWithoutDecimal() else "")

            etPriceFromChange.setText(if (filter.changeFrom != 0.0) filter.changeFrom.formatPriceWithoutDecimal() else "0")
            etPriceToChange.setText(if (filter.changeTo != 0.0) filter.changeTo.formatPriceWithoutDecimal() else "")

            etPriceFromVolume.setText(if (filter.volumeFrom != 0.0) filter.volumeFrom.div(100).formatPriceWithoutDecimal() else "0")
            etPriceToVolume.setText(if (filter.volumeFrom != 0.0) filter.volumeTo.div(100).formatPriceWithoutDecimal() else "")
        }

    }

    private fun chipListener() {
        binding.apply {
            chipGroupType.setOnCheckedStateChangeListener { _, checkedIds ->
                for (chipId in checkedIds) {
                    listStockSelected.clear()

                    val selectedChip = chipGroupType.findViewById(chipId) as Chip

                    category = when (selectedChip.text.toString().lowercase()) {
                        "all stocks" -> 0
                        "index" -> 1
                        "sector" -> 2
                        "watchlist" -> 3
                        "stocks in portfolio" -> 4
                        else -> 0
                    }

                    when (category) {
                        3 -> mViewModel.getAllWatchlist(userId, prefManager.sessionId)
                        4 -> mViewModel.getListStockPortfolio(userId, prefManager.sessionId)
                    }

                    groupIndexSector.visibility = if (category == 1 || category == 2) View.VISIBLE else View.GONE
                    tvIndex.text = if (category == 1) "Index" else "Sector"
                    tvIndexVal.text = "All"
                }
            }
        }

    }

    override fun setupObserver() {
        super.setupObserver()
        mViewModel.getListIndex.observe(viewLifecycleOwner) {listIndex ->
            if (!listIndex.isNullOrEmpty()) {
                listIndex.map {item ->
                    indexList[item.indexCode] =  item
                }
            }
        }

        mViewModel.getListSector.observe(viewLifecycleOwner) {listSector ->
            if (!listSector.isNullOrEmpty()) {
                listSector.map {item ->
                    sectorList[item.indexCode] =  item
                }
            }

            if (defaultFilter != UIDialogRunningTradeModel()) {
                if (isFirstOpen || model == defaultFilter) {
                    stickyButtonBinding.checkboxFilterRunningTrade.isChecked = true
                    mViewModel.getStockIndexSector(userId, prefManager.sessionId, defaultFilter.indexSector)
                    setupFilter(defaultFilter)
                } else {
                    stickyButtonBinding.checkboxFilterRunningTrade.isChecked = false
                    listStockSelected = ArrayList(model.stockCodes)
                    setupFilter(model)
                }
            } else {
                listStockSelected = ArrayList(model.stockCodes)
                setupFilter(model)
            }
        }

        mViewModel.getListStockIndexSector.observe(viewLifecycleOwner) {listStocks ->
            if (listStocks.isNotEmpty()) {
                listStockSelected.clear()
                listStockSelected.addAll(listStocks)
            }
        }
    }

    private fun resetFilter() {
        binding.apply {
            chipAllStocks.isChecked = true
            category = 0
            indexSectorId = 0

            etPriceFrom.setText("Rp0")
            etPriceTo.setText("")
            etPriceFromChange.setText("0")
            etPriceToChange.setText("")
            etPriceFromVolume.setText("0")
            etPriceToVolume.setText("")

            stickyButtonBinding.checkboxFilterRunningTrade.isChecked = false
        }
    }

    private fun sendCallback() {
        val priceFrom = binding.etPriceFrom.text.toString().removeSeparator().replace("Rp", "").toDoubleOrNull()?: 0.0
        val priceTo = binding.etPriceTo.text.toString().removeSeparator().replace("Rp", "").toDoubleOrNull()?: 0.0
        val changeFrom = binding.etPriceFromChange.text.toString().removeSeparator().toDoubleOrNull() ?: 0.0
        val changeTo = binding.etPriceToChange.text.toString().removeSeparator().toDoubleOrNull() ?: 0.0
        val volumeFrom = binding.etPriceFromVolume.text.toString().removeSeparator().toDoubleOrNull() ?: 0.0
        val volumeTo = binding.etPriceToVolume.text.toString().removeSeparator().toDoubleOrNull() ?: 0.0

        val inputFilter = UIDialogRunningTradeModel(category, indexSectorId, priceFrom, priceTo, changeFrom, changeTo, volumeFrom.times(100), volumeTo.times(100),
            listStockSelected.ifEmpty { emptyList() })

        if (stickyButtonBinding.checkboxFilterRunningTrade.isChecked && inputFilter != UIDialogRunningTradeModel()) {
            mViewModel.insertDefaultFilter(userId, inputFilter)
        }

        if (inputFilter == UIDialogRunningTradeModel()) {
            mViewModel.resetDefaultFilterRunningTrade(userId)
        }

        val result = Bundle()
        result.putString(NavKeys.KEY_FM_RUNNING_TRADE, NavKeys.CONST_RES_RUNNING_TRADE)
        result.putString(NavKeys.CONST_RES_RUNNING_TRADE, "RESULT_OK")
        result.putParcelable(Args.EXTRA_PARAM_OBJECT, inputFilter)
        parentFragmentManager.setFragmentResult(NavKeys.KEY_FM_RUNNING_TRADE, result)
    }

    override fun setupListener() {
        super.setupListener()
        val textWatcherPriceFrom = object : TextWatcher {
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
                binding.etPriceFrom.removeTextChangedListener(this)

                val removeSeparator = s.toString().removeSeparator().replace("Rp", "")
                val formattedText = "Rp${removeSeparator.formatPriceWithoutDecimal()}"

                binding.etPriceFrom.setText(formattedText)
                binding.etPriceFrom.setSelection(formattedText.length)
                binding.etPriceFrom.addTextChangedListener(this)
            }
        }
        binding.etPriceFrom.addTextChangedListener(textWatcherPriceFrom)

        val textWatcherPriceTo = object : TextWatcher {
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
                binding.etPriceTo.removeTextChangedListener(this)
                val removeSeparator = if (s.toString() != "") s.toString().removeSeparator().replace("Rp", "") else s.toString()
                val formattedText = if (removeSeparator != "" && removeSeparator != "0") "Rp${removeSeparator.formatPriceWithoutDecimal()}" else ""

                binding.etPriceTo.setText(formattedText)
                binding.etPriceTo.setSelection(formattedText.length)
                binding.etPriceTo.addTextChangedListener(this)
            }
        }
        binding.etPriceTo.addTextChangedListener(textWatcherPriceTo)

        val textWatcherChangeFrom = object : TextWatcher {
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
                binding.etPriceFromChange.removeTextChangedListener(this)

                val removeSeparator = s.toString().removeSeparator()
                val formattedText = removeSeparator.formatPriceWithoutDecimal()

                binding.etPriceFromChange.setText(formattedText)
                binding.etPriceFromChange.setSelection(formattedText.length)
                binding.etPriceFromChange.addTextChangedListener(this)
            }
        }
        binding.etPriceFromChange.addTextChangedListener(textWatcherChangeFrom)

        val textWatcherChangeTo = object : TextWatcher {
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
                binding.etPriceToChange.removeTextChangedListener(this)

                val removeSeparator = if (s.toString() != "") s.toString().removeSeparator() else s.toString()
                val formattedText = if (removeSeparator != "" && removeSeparator != "0") removeSeparator.formatPriceWithoutDecimal() else ""

                binding.etPriceToChange.setText(formattedText)
                binding.etPriceToChange.setSelection(formattedText.length)
                binding.etPriceToChange.addTextChangedListener(this)
            }
        }
        binding.etPriceToChange.addTextChangedListener(textWatcherChangeTo)

        val textWatcherVolumeFrom = object : TextWatcher {
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
                binding.etPriceFromVolume.removeTextChangedListener(this)

                val removeSeparator = s.toString().removeSeparator()
                val formattedText = removeSeparator.formatPriceWithoutDecimal()

                binding.etPriceFromVolume.setText(formattedText)
                binding.etPriceFromVolume.setSelection(formattedText.length)
                binding.etPriceFromVolume.addTextChangedListener(this)
            }
        }
        binding.etPriceFromVolume.addTextChangedListener(textWatcherVolumeFrom)

        val textWatcherVolumeTo = object : TextWatcher {
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
                binding.etPriceToVolume.removeTextChangedListener(this)

                val removeSeparator = if (s.toString() != "") s.toString().removeSeparator() else s.toString()
                val formattedText = if (removeSeparator != "" && removeSeparator != "0") removeSeparator.formatPriceWithoutDecimal() else ""

                binding.etPriceToVolume.setText(formattedText)
                binding.etPriceToVolume.setSelection(formattedText.length)
                binding.etPriceToVolume.addTextChangedListener(this)
            }
        }
        binding.etPriceToVolume.addTextChangedListener(textWatcherVolumeTo)
    }
}