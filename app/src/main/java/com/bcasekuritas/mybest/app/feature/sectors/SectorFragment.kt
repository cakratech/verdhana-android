package com.bcasekuritas.mybest.app.feature.sectors

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.domain.dto.response.IndexSectorDetailData
import com.bcasekuritas.mybest.app.feature.sectors.adapter.SectorAdapter
import com.bcasekuritas.mybest.databinding.FragmentSectorsBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.constant.Args
import com.bcasekuritas.mybest.ext.constant.NavKeys
import com.bcasekuritas.mybest.ext.delegate.ShowDialog
import com.bcasekuritas.mybest.ext.delegate.ShowDialogImpl
import com.bcasekuritas.mybest.ext.listener.OnClickAny
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import timber.log.Timber

@FragmentScoped
@AndroidEntryPoint
class SectorFragment : BaseFragment<FragmentSectorsBinding, SectorViewModel>(), OnClickAny, ShowDialog by ShowDialogImpl() {

    @FragmentScoped
    override val bindingVariable: Int = BR.vmSector
    override val viewModel: SectorViewModel by viewModels()
    override val binding: FragmentSectorsBinding by autoCleaned{ (FragmentSectorsBinding.inflate(layoutInflater)) }

    private val mAdapter: SectorAdapter by autoCleaned { SectorAdapter(requireContext(), this) }

    private var userId = ""
    private var sessionId = ""

    private var sortState = 3

    private val listSector = arrayListOf<IndexSectorDetailData>()

    override fun setupAdapter() {
        super.setupAdapter()
        binding.rcvSectors.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }

    override fun initOnClick() {
        super.initOnClick()
        binding.apply {
            btnBack.setOnClickListener {
                onBackPressed()
            }

            btnSort.setOnClickListener {
                showDialogSortSectorBottom(sortState, parentFragmentManager)
            }
        }
    }

    override fun setupListener() {
        super.setupListener()
        parentFragmentManager.setFragmentResultListener(NavKeys.KEY_FM_SECTOR, viewLifecycleOwner) {_, result ->
            val confirmResult = result.getString(NavKeys.CONST_RES_SECTOR)

            if (confirmResult == "RESULT_OK") {
                val sortResult = result.getInt("sort")
                sortState = sortResult
                val sortedList = sortingData(listSector)
                if (sortedList.isNotEmpty()) {
                    mAdapter.setData(sortedList)
                }
            }
        }
    }

    override fun initAPI() {
        super.initAPI()
        userId = prefManager.userId
        sessionId = prefManager.sessionId

        viewModel.getSectorData(userId, sessionId)
    }

    override fun onResume() {
        super.onResume()
        Timber.d("SectorFragment onResume")
        viewModel.setListenerIndice()
    }

    override fun onPause() {
        super.onPause()
        viewModel.unSubscribeIndiceData()
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.imqConnectionListener.connListenerLiveData.observe(this) { value ->
            when (value) {
                "Recovered" -> {
                    viewModel.imqConnectionListener.onListener("")
                    viewModel.getSectorData(userId, sessionId)
                }

                else -> {}
            }
        }

        viewModel.getSectorDataResult.observe(viewLifecycleOwner) {
            if (it != null) {
                val listItem = arrayListOf<String>()
                it.map { item ->
                    item?.let { it1 -> listItem.add(it1.indexCode) }
                }
                if (listItem.size != 0) {
                    viewModel.getSectorDetailData(userId, sessionId, listItem)
                }
            }
        }

        viewModel.getSectorDetailDataResult.observe(viewLifecycleOwner) {
            if (it != null) {
                listSector.clear()
                val data = sortingData(it.filterNotNull())
                mAdapter.setData(data)
                listSector.addAll(data)
            }
        }
    }

    override fun onClickAny(valueAny: Any?) {
        val data = valueAny as IndexSectorDetailData
        if (data.indiceCode != "" && data.id.toInt() != 0 && data.stockCount != 0) {
            val bundle = Bundle().apply {
                putString(Args.EXTRA_PARAM_STR_ONE, data.indiceCode )
                putInt(Args.EXTRA_PARAM_INT_ONE, data.id.toInt())
                putInt(Args.EXTRA_PARAM_INT_TWO, data.stockCount)
            }
            findNavController().navigate(R.id.sector_detail_fragment, bundle)
        }
    }

    private fun sortingData(data: List<IndexSectorDetailData>): List<IndexSectorDetailData> {
        return when (sortState) {
            1 -> data.sortedByDescending { it.chgPercent }
            2 -> data.sortedBy { it.chgPercent }
            3 -> data.sortedBy { it.indiceCode }
            4 -> data.sortedByDescending { it.indiceCode }
            else -> data.sortedBy { it.indiceCode }
        }
    }
}