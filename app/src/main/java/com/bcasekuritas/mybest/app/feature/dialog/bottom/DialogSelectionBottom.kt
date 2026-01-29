package com.bcasekuritas.mybest.app.feature.dialog.bottom

import android.app.Activity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import com.bcasekuritas.mybest.app.base.dialog.withoutvm.BaseBottomSheet
import com.bcasekuritas.mybest.app.domain.dto.response.source.SelectionCheckRes
import com.bcasekuritas.mybest.app.feature.dialog.adapter.DialogSelectionSingleAdapter
import com.bcasekuritas.mybest.databinding.DialogSelectionBottomBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.constant.NavKeys

@FragmentScoped
@AndroidEntryPoint
class DialogSelectionBottom(var reqTargetCode: String?, var dataList: ArrayList<SelectionCheckRes>, var singleOrMultiple: Boolean, var resTargerCode: String?) :
    BaseBottomSheet<DialogSelectionBottomBinding>() {

    @FragmentScoped
    override val binding: DialogSelectionBottomBinding by autoCleaned{(
        DialogSelectionBottomBinding.inflate(layoutInflater)
    )}

    private val dialogSelectionSingleAdapter: DialogSelectionSingleAdapter by autoCleaned { DialogSelectionSingleAdapter() }

    override fun initOnClick() {
        super.initOnClick()

        binding.buttonSelectionOk.setOnClickListener {
            sendCallback()
            dismiss()

        }
    }

    override fun setupAdapter() {
        super.setupAdapter()

        if (singleOrMultiple){ // True = Single
            binding.rcvDialogSelection.layoutManager = LinearLayoutManager(requireActivity())
            binding.rcvDialogSelection.setHasFixedSize(true)
            binding.rcvDialogSelection.adapter = dialogSelectionSingleAdapter

            dialogSelectionSingleAdapter.setData(dataList)
        }
    }

    private fun sendCallback() {
        val result = Bundle()
        when (reqTargetCode) {
            NavKeys.KEY_FM_E_STATEMENT -> {
                result.putString(NavKeys.KEY_FM_E_STATEMENT, NavKeys.CONST_RES_LIST_E_STATEMENTS)
                result.putString(NavKeys.CONST_RES_LIST_E_STATEMENTS, "RESULT_OK")
                result.putString("REPORT_TYPE", dialogSelectionSingleAdapter.getSelected())
                parentFragmentManager.setFragmentResult(NavKeys.KEY_FM_E_STATEMENT, result)
            }

            else -> {}
        }
    }

}