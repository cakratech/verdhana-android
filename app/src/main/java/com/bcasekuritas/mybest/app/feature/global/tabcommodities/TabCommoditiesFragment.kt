package com.bcasekuritas.mybest.app.feature.global.tabcommodities

import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.domain.dto.response.IndCommCurrHelper
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.app.feature.global.GlobalSharedViewModel
import com.bcasekuritas.mybest.app.feature.global.adapter.IndCommCurrAdapter
import com.bcasekuritas.mybest.databinding.FragmentTabCommoditiesBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@AndroidEntryPoint
class TabCommoditiesFragment :
    BaseFragment<FragmentTabCommoditiesBinding, TabCommoditiesViewModel>() {

    override val bindingVariable: Int = BR.vmPortfolio
    override val viewModel: TabCommoditiesViewModel by viewModels()
    override val binding: FragmentTabCommoditiesBinding by autoCleaned {
        (FragmentTabCommoditiesBinding.inflate(
            layoutInflater
        ))
    }

    private val sharedViewModel: GlobalSharedViewModel by lazy {
        ViewModelProvider(requireActivity()).get(GlobalSharedViewModel::class.java)
    }

    private val indCommCurrAdapter: IndCommCurrAdapter by autoCleaned {
        IndCommCurrAdapter(
            ""
        )
    }
    private var sortedCommoditiesList = listOf<IndCommCurrHelper>()
    private var idImage = 0

    companion object {
        fun newInstance() = TabCommoditiesFragment()
    }

    override fun setupAdapter() {
        super.setupAdapter()

        binding.rcvTabIndCommCurr.adapter = indCommCurrAdapter
    }

    override fun initAPI() {
        super.initAPI()
        viewModel.getGlobalCommodities(prefManager.userId, prefManager.sessionId)
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.imqConnectionListener.connListenerLiveData.observe(this) { value ->
            when (value) {
                "Recovered" -> {
                    viewModel.imqConnectionListener.onListener("")
                    viewModel.getGlobalCommodities(prefManager.userId, prefManager.sessionId)
                }

                else -> {}
            }
        }

        viewModel.getGlobalCommoditiesResult.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    when (it.data?.status) {
                        "0" -> {
                            val commodityData = arrayListOf<IndCommCurrHelper>()
                            it.data.comoditiesDataList.map { item ->

                                idImage = if (idImage > 7) 0 else idImage

                                commodityData.add(
                                    IndCommCurrHelper(
                                        code = item.macroName,
                                        name = "",
                                        value = item.value,
                                        change = item.nominalChange,
                                        changePct = item.change,
                                        idImg = idImage
                                    )

                                )
                                idImage += 1
                            }

                            sortedCommoditiesList = commodityData.sortedBy { it.code }

                            indCommCurrAdapter.setData(sortedCommoditiesList, false, true)
                        }
                    }
                }

                else -> {}
            }
        }

        sharedViewModel.getQuerySearch.observe(viewLifecycleOwner) {
            filterItems(it)
        }
    }


    private fun filterItems(query: String) {
        val filteredItems = sortedCommoditiesList.filter { item ->
            item.code.contains(query, ignoreCase = true) ||
                    item.name.contains(query, ignoreCase = true)
        }

        indCommCurrAdapter.setData(filteredItems, false, true)
    }
}