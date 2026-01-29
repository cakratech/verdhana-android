package com.bcasekuritas.mybest.app.feature.stockdetail.specialnotes

import android.view.View
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.feature.stockdetail.adapter.SpecialNotesAdapter
import com.bcasekuritas.mybest.databinding.FragmentSpecialNotesBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.constant.Args
import timber.log.Timber

@FragmentScoped
@AndroidEntryPoint
class SpecialNotesFragment : BaseFragment<FragmentSpecialNotesBinding, SpecialNotesViewModel>() {


    @FragmentScoped
    override val bindingVariable: Int = BR.vmStockDetail
    override val viewModel: SpecialNotesViewModel by viewModels()
    override val binding: FragmentSpecialNotesBinding by autoCleaned {
        FragmentSpecialNotesBinding.inflate(
            layoutInflater
        )
    }

    private val specialNotesAdapter: SpecialNotesAdapter by autoCleaned { SpecialNotesAdapter() }

    private var acceleration = ""

    companion object {
        fun newInstance() = SpecialNotesFragment()
    }

    override fun setupArguments() {
        super.setupArguments()
        arguments.let {
            if (it != null) {
                acceleration = it.getString(Args.EXTRA_PARAM_STR_ONE) ?: ""
            }
        }
    }

    override fun setupComponent() {
        super.setupComponent()
        binding.lyToolbarSpecialNotes.tvLayoutToolbarMasterTitle.text = getString(R.string.text_special_notes_information)
        binding.lyToolbarSpecialNotes.ivLayoutToolbarMasterIconLeft.visibility = View.VISIBLE

        binding.lyToolbarSpecialNotes.ivLayoutToolbarMasterIconLeft.setOnClickListener {
            onBackPressed()
        }

        if (acceleration.isNotEmpty()) {
            binding.tvTitleAcceleration.visibility = View.VISIBLE
            binding.tvDescAcceleration.visibility = View.VISIBLE

            binding.tvTitleAcceleration.text = acceleration
            binding.tvDescAcceleration.text = when (acceleration) {
                "Development" -> resources.getString(R.string.acceleration_development)
                "Acceleration" -> resources.getString(R.string.acceleration_desc)
                "Watch-List" -> resources.getString(R.string.acceleration_watch_list)
                "New Economy" -> resources.getString(R.string.acceleration_new_economy)
                else -> ""
            }
        }
    }

    override fun setupAdapter() {
        super.setupAdapter()
        binding.rcvSpecialNotes.setHasFixedSize(false)
        binding.rcvSpecialNotes.adapter = specialNotesAdapter
    }

    override fun initAPI() {
        super.initAPI()
        var stockCode = prefManager.stockDetailCode

        if (acceleration.isEmpty()) {
            viewModel.getStockNotation(stockCode)
        }
    }

    override fun setupObserver() {
        super.setupObserver()

        // Get Notation
        viewModel.getStockNotationResult.observe(viewLifecycleOwner){
            var listData = it.distinctBy {
                it!!.notation
            }
            specialNotesAdapter.setData(listData)
        }
    }
}