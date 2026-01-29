package com.bcasekuritas.mybest.app.feature.dialog.expandabletext

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.dialog.withoutvm.BaseBottomSheet
import com.bcasekuritas.mybest.databinding.DialogExpandableTextBinding
import com.bcasekuritas.mybest.databinding.DialogWithdrawConfirmBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.constant.NavKeys

@FragmentScoped
@AndroidEntryPoint
class DialogExpandableTextFragment :
    BaseBottomSheet<DialogExpandableTextBinding>(){

    @FragmentScoped
    override val binding: DialogExpandableTextBinding by autoCleaned {
        (DialogExpandableTextBinding.inflate(
            layoutInflater
        ))
    }

    companion object {
        fun newInstance() = DialogExpandableTextFragment()
    }

    override fun initOnClick() {
        super.initOnClick()

        binding.tvDetSubtitleOne.setOnClickListener {
            if (binding.detExpandTextOne.isExpanded){
                binding.detExpandTextOne.collapse()
            }else{
                binding.detExpandTextOne.expand()
            }
        }
    }

}