package com.bcasekuritas.mybest.app.feature.dialog.bottom

import android.os.Bundle
import android.view.View
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import com.bcasekuritas.mybest.app.base.dialog.withoutvm.BaseBottomSheet
import com.bcasekuritas.mybest.databinding.DialogSortSectorsBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.constant.NavKeys

@FragmentScoped
@AndroidEntryPoint
class DialogSortSector(private val state: Int) : BaseBottomSheet<DialogSortSectorsBinding>(){

    @FragmentScoped
    override val binding: DialogSortSectorsBinding by autoCleaned { (DialogSortSectorsBinding.inflate(layoutInflater)) }

    private var sort = 0

    override fun setupComponent() {
        super.setupComponent()

       checklistState(
           when(state) {
               0 -> ""
               1 -> "gainer"
               2 -> "loser"
               3 -> "az"
               4 ->"za"
               else -> ""
           }
       )

    }

    override fun initOnClick() {
        super.initOnClick()

        binding.btnClose.setOnClickListener {
            dismiss()
        }

        binding.lyGainer.setOnClickListener {
            checklistState("gainer")
        }

        binding.lyLoser.setOnClickListener {
            checklistState("loser")
        }

        binding.lyAZ.setOnClickListener {
            checklistState("az")
        }

        binding.lyZA.setOnClickListener {
            checklistState("za")
        }

        binding.btnApply.setOnClickListener {
            sendCallback(sort)
            dismiss()
        }

    }

    private fun sendCallback(state: Int){
        val result = Bundle()
        result.putString(NavKeys.KEY_FM_SECTOR, NavKeys.CONST_RES_SECTOR)
        result.putString(NavKeys.CONST_RES_SECTOR, "RESULT_OK")
        result.putInt("sort", state)
        parentFragmentManager.setFragmentResult(NavKeys.KEY_FM_SECTOR, result)
    }

    private fun checklistState(state: String) {
        when (state) {
            "gainer" -> {
                binding.ivGainer.visibility = View.VISIBLE
                binding.ivLoser.visibility = View.GONE
                binding.ivAZ.visibility = View.GONE
                binding.ivZA.visibility = View.GONE

                sort = 1
            }
            "loser" -> {
                binding.ivGainer.visibility = View.GONE
                binding.ivLoser.visibility = View.VISIBLE
                binding.ivAZ.visibility = View.GONE
                binding.ivZA.visibility = View.GONE

                sort = 2
            }
            "az" -> {
                binding.ivGainer.visibility = View.GONE
                binding.ivLoser.visibility = View.GONE
                binding.ivAZ.visibility = View.VISIBLE
                binding.ivZA.visibility = View.GONE

                sort = 3
            }
            "za" -> {
                binding.ivGainer.visibility = View.GONE
                binding.ivLoser.visibility = View.GONE
                binding.ivAZ.visibility = View.GONE
                binding.ivZA.visibility = View.VISIBLE

                sort = 4
            }
            else -> {
                binding.ivGainer.visibility = View.GONE
                binding.ivLoser.visibility = View.GONE
                binding.ivAZ.visibility = View.GONE
                binding.ivZA.visibility = View.GONE
                sort = 0
            }
        }
    }

}