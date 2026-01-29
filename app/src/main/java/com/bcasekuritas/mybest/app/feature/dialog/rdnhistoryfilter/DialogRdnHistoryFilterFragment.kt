package com.bcasekuritas.mybest.app.feature.dialog.rdnhistoryfilter

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.annotation.RequiresApi
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.dialog.withoutvm.BaseBottomSheet
import com.bcasekuritas.mybest.databinding.DialogRdnHistoryFilterBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.common.convertMillisToDate
import com.bcasekuritas.mybest.ext.common.convertToMillis
import com.bcasekuritas.mybest.ext.common.getPrevious90DaysInMillis
import com.bcasekuritas.mybest.ext.common.initCalendarDialogForRange
import com.bcasekuritas.mybest.ext.common.initCalenderDialog
import com.bcasekuritas.mybest.ext.constant.NavKeys
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import java.text.SimpleDateFormat
import java.util.Date

@FragmentScoped
@AndroidEntryPoint
class DialogRdnHistoryFilterFragment(var filterList: List<String>, var startDate: Long, var endDate: Long) :
    BaseBottomSheet<DialogRdnHistoryFilterBinding>() {

    @FragmentScoped
    override val binding: DialogRdnHistoryFilterBinding by autoCleaned {
        (DialogRdnHistoryFilterBinding.inflate(
            layoutInflater
        ))
    }

    private lateinit var dateFromFilter: String
    private lateinit var dateToFilter: String
    private var countFilter = 0
    private var countNotValid = 0

    override fun setupComponent() {
        super.setupComponent()

        binding.tvFilterDateFrom.text = convertMillisToDate(startDate,"dd MMM yyyy")
        binding.tvFilterDateTo.text = convertMillisToDate(endDate,"dd MMM yyyy")

        binding.tvFilterDateTo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val dateStart = convertToMillis(binding.tvFilterDateFrom.text.toString(), "dd MMM yyyy")
                val dateEnd = convertToMillis(binding.tvFilterDateTo.text.toString(), "dd MMM yyyy")
                if (dateStart > dateEnd){
                    // Make the start date same as end Date
                    binding.tvFilterDateFrom.text = binding.tvFilterDateTo.text.toString()
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        if (filterList!= null && filterList.isNotEmpty()){
            for (i in filterList.indices){
                when (filterList[i]){
                    "V"      -> {
                        checkDividend()
//                        countFilter++
                    }
                    "C"        -> {
                        checkTopUp()
//                        countFilter++
                    }
                    "W"    -> {
                        checkWithdrawal()
//                        countFilter++
                    }
                    "*"    -> {
                        checkAll()
//                        countFilter++
                    }
                    else -> {
//                        if (filterList[i].contains("-")){
//                            var listDate = filterList[0].split("-")
//                            dateFromFilter = listDate[0]
//                            dateToFilter = listDate[1]
//
//                        }else{
//                            dateFromFilter = filterList[0]
//                            dateToFilter = dateFromFilter
//                        }
//
//                        binding.tvFilterDateFrom.text = dateFromFilter
//                        binding.tvFilterDateTo.text = dateToFilter
                    }
                }
            }

//            if (countFilter == 3){
//                binding.ctvFilterAll.setCheckMarkDrawable(R.drawable.ic_check)
//                binding.ctvFilterAll.isChecked = true
//            }

//            if (filterList[0].contains("-")){
//                var listDate = filterList[0].split("-")
//                dateFromFilter = listDate[0]
//                dateToFilter = listDate[1]
//
//            }else{
//                dateFromFilter = filterList[0]
//                dateToFilter = dateFromFilter
//            }
//
//            binding.tvFilterDateFrom.text = dateFromFilter
//            binding.tvFilterDateTo.text = dateToFilter
//
//            if (filterList[1].isNotEmpty() && filterList[2].isNotEmpty() && filterList[3].isNotEmpty()){
//                checkAll()
//                checkDividend()
//                checkTopUp()
//                checkWithdrawal()
//            }else{
//                if (filterList[1].isNotEmpty()){
//                    checkDividend()
//                }
//
//                if (filterList[2].isNotEmpty()){
//                    checkTopUp()
//                }
//
//                if (filterList[3].isNotEmpty()){
//                    checkWithdrawal()
//                }
//            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun initOnClick() {
        super.initOnClick()

        binding.tvFilterDateFrom.setOnClickListener {
//            initCalenderDialog(requireContext(), binding.tvFilterDateFrom, 1)
            endDate = convertToMillis(binding.tvFilterDateTo.text.toString(), "dd MMM yyyy")
            val pickStartDate = convertToMillis(binding.tvFilterDateFrom.text.toString(), "dd MMM yyyy")
//            initCalendarDialogForRange(requireContext(), binding.tvFilterDateFrom, null, endDate, getPrevious90DaysInMillis(), pickStartDate, true)
            initCalenderDialog(requireContext(), binding.tvFilterDateFrom, 1)
        }

        binding.tvFilterDateTo.setOnClickListener {
//            initCalenderDialog(requireContext(), binding.tvFilterDateTo, 1)
            startDate = convertToMillis(binding.tvFilterDateFrom.text.toString(), "dd MMM yyyy")
            val pickEndDate = convertToMillis(binding.tvFilterDateTo.text.toString(), "dd MMM yyyy")
//            initCalendarDialogForRange(requireContext(), binding.tvFilterDateTo, startDate, null, getPrevious90DaysInMillis(),pickEndDate,false)
            initCalenderDialog(requireContext(), binding.tvFilterDateTo, 1)
        }

        binding.buttonFilterClose.setOnClickListener {
            dismiss()
        }

        binding.buttonFilterApply.setOnClickListener {
            countNotValid = 0

            var dateFrom = binding.tvFilterDateFrom.text.toString()
            var dateTo = binding.tvFilterDateTo.text.toString()
            var filterType = ""

            if (dateValidation(dateFrom, dateTo)){
                when{
                    binding.ctvFilterAll.isChecked -> filterType = "*"
                    binding.ctvFilterDividend.isChecked -> filterType = "V"
                    binding.ctvFilterTopup.isChecked -> filterType = "C"
                    binding.ctvFilterWithdrawal.isChecked -> filterType = "W"
                }

                sendCallback(dateFrom, dateTo, filterType, "", "")
                dismiss()
            }
        }

        binding.buttonFilterReset.setOnClickListener {
            countNotValid = 0
            endDate = convertToMillis(binding.tvFilterDateTo.text.toString(), "dd MMM yyyy")
            startDate = convertToMillis(binding.tvFilterDateFrom.text.toString(), "dd MMM yyyy")
            binding.tvFilterDateFrom.text = convertMillisToDate(startDate,"dd MMM yyyy")
            binding.tvFilterDateTo.text = convertMillisToDate(endDate,"dd MMM yyyy")

            binding.ctvFilterAll.checkMarkDrawable = null
            binding.ctvFilterAll.isChecked = false
            binding.ctvFilterDividend.checkMarkDrawable = null
            binding.ctvFilterDividend.isChecked = false
            binding.ctvFilterTopup.checkMarkDrawable = null
            binding.ctvFilterTopup.isChecked = false
            binding.ctvFilterWithdrawal.checkMarkDrawable = null
            binding.ctvFilterWithdrawal.isChecked = false
        }

        binding.ctvFilterAll.setOnClickListener{
            checkAll()
        }

        binding.ctvFilterDividend.setOnClickListener{
            checkDividend()
        }

        binding.ctvFilterTopup.setOnClickListener{
            checkTopUp()
        }

        binding.ctvFilterWithdrawal.setOnClickListener{
            checkWithdrawal()
        }
    }

    private fun sendCallback(strDateFrom: String, strDateTo: String, strDividend: String, strTopUp: String, strWithdraw: String){
        val result = Bundle()
        result.putString(NavKeys.KEY_FM_RDN_HISTORY, NavKeys.CONST_RES_RDN_HISTORY_FILTER)
        result.putString(NavKeys.CONST_RES_RDN_HISTORY_FILTER, "RESULT_OK")
        result.putString("StrDateFrom", strDateFrom)
        result.putString("strDateTo", strDateTo)
        result.putString("strDividend", strDividend)
        result.putString("strTopUp", strTopUp)
        result.putString("strWithdraw", strWithdraw)
        parentFragmentManager.setFragmentResult(NavKeys.KEY_FM_RDN_HISTORY, result)
    }

    private fun checkAll(){
        if (binding.ctvFilterAll.isChecked){
            binding.ctvFilterAll.checkMarkDrawable = null
            binding.ctvFilterAll.isChecked = false
        }else{
            binding.ctvFilterAll.setCheckMarkDrawable(R.drawable.ic_check)
            binding.ctvFilterAll.isChecked = true
            binding.ctvFilterTopup.isChecked = false
            binding.ctvFilterDividend.isChecked = false
            binding.ctvFilterWithdrawal.isChecked = false
            binding.ctvFilterTopup.checkMarkDrawable = null
            binding.ctvFilterDividend.checkMarkDrawable = null
            binding.ctvFilterWithdrawal.checkMarkDrawable = null
        }
    }

    private fun checkDividend(){
        if (binding.ctvFilterDividend.isChecked){
            binding.ctvFilterDividend.checkMarkDrawable = null
            binding.ctvFilterDividend.isChecked = false
        }else{
            binding.ctvFilterDividend.setCheckMarkDrawable(R.drawable.ic_check)
            binding.ctvFilterDividend.isChecked = true
            binding.ctvFilterTopup.isChecked = false
            binding.ctvFilterAll.isChecked = false
            binding.ctvFilterWithdrawal.isChecked = false
            binding.ctvFilterTopup.checkMarkDrawable = null
            binding.ctvFilterAll.checkMarkDrawable = null
            binding.ctvFilterWithdrawal.checkMarkDrawable = null
        }
    }

    private fun checkTopUp(){
        if (binding.ctvFilterTopup.isChecked){
            binding.ctvFilterTopup.checkMarkDrawable = null
            binding.ctvFilterTopup.isChecked = false
        }else{
            binding.ctvFilterTopup.setCheckMarkDrawable(R.drawable.ic_check)
            binding.ctvFilterTopup.isChecked = true
            binding.ctvFilterAll.isChecked = false
            binding.ctvFilterDividend.isChecked = false
            binding.ctvFilterWithdrawal.isChecked = false
            binding.ctvFilterDividend.checkMarkDrawable = null
            binding.ctvFilterAll.checkMarkDrawable = null
            binding.ctvFilterWithdrawal.checkMarkDrawable = null
        }
    }

    private fun checkWithdrawal(){
        if (binding.ctvFilterWithdrawal.isChecked){
            binding.ctvFilterWithdrawal.checkMarkDrawable = null
            binding.ctvFilterWithdrawal.isChecked = false
        }else{
            binding.ctvFilterWithdrawal.setCheckMarkDrawable(R.drawable.ic_check)
            binding.ctvFilterWithdrawal.isChecked = true
            binding.ctvFilterAll.isChecked = false
            binding.ctvFilterDividend.isChecked = false
            binding.ctvFilterTopup.isChecked = false
            binding.ctvFilterTopup.checkMarkDrawable = null
            binding.ctvFilterAll.checkMarkDrawable = null
            binding.ctvFilterDividend.checkMarkDrawable = null
        }
    }

    private fun dateValidation(strDateFrom: String, strDateTo: String): Boolean{
        if (strDateFrom.isNotEmpty() && strDateTo.isNotEmpty()){
            val sdf = SimpleDateFormat("dd MMM yyyy")
            val dateFrom: Date = sdf.parse(strDateFrom)
            val dateTo: Date = sdf.parse(strDateTo)

            if (dateFrom > dateTo) {
                binding.tvFilterDateTo.error = "Date To Not Valid !"
                countNotValid++
            }
        }else{
            if (strDateTo.isEmpty()){
                binding.tvFilterDateFrom.error = "Select Date From !"
                countNotValid++
            }

            if (strDateTo.isEmpty()){
                binding.tvFilterDateTo.error = "Select Date From !"
                countNotValid++
            }
        }

        return countNotValid == 0
    }

}