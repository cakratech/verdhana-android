package com.bcasekuritas.mybest.app.feature.global.tabindex

import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.domain.dto.response.IndCommCurrHelper
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.app.feature.global.GlobalSharedViewModel
import com.bcasekuritas.mybest.app.feature.global.adapter.IndCommCurrAdapter
import com.bcasekuritas.mybest.databinding.FragmentGlobalIndexBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@AndroidEntryPoint
class TabIndexFragment : BaseFragment<FragmentGlobalIndexBinding, TabIndexViewModel>() {

    override val bindingVariable: Int = BR.vmPortfolio
    override val viewModel: TabIndexViewModel by viewModels()
    override val binding: FragmentGlobalIndexBinding by autoCleaned {
        (FragmentGlobalIndexBinding.inflate(
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

    private var sortedIndexList = listOf<IndCommCurrHelper>()
    private var idImage = 0

    companion object {
        fun newInstance() = TabIndexFragment()
    }


    override fun setupAdapter() {
        super.setupAdapter()

        binding.rcvTabIndCommCurr.adapter = indCommCurrAdapter
    }

    override fun initAPI() {
        super.initAPI()
        viewModel.getGlobalIndex(prefManager.userId, prefManager.sessionId)
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.imqConnectionListener.connListenerLiveData.observe(this) { value ->
            when (value) {
                "Recovered" -> {
                    viewModel.imqConnectionListener.onListener("")
                    viewModel.getGlobalIndex(prefManager.userId, prefManager.sessionId)
                }

                else -> {}
            }
        }

        viewModel.getGlobalIndexResult.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    when (it.data?.status) {
                        "0" -> {
                            val indexData = arrayListOf<IndCommCurrHelper>()
                            it.data.indexDataList.sortedBy {it1 ->
                                it1.yahooCode.replace("^", "")
                            }.map { item ->
                                idImage = if (idImage > 7) 0 else idImage
                                val code = item.yahooCode.replace("^", "")

                                indexData.add(
                                    IndCommCurrHelper(
                                        code = code,
                                        name = item.macroName,
                                        value = item.value,
                                        change = item.nominalChange,
                                        changePct = item.change,
                                        idImg = idImage
                                    )

                                )

                                idImage += 1
                            }
                            sortedIndexList = indexData
                            indCommCurrAdapter.setData(sortedIndexList, false, false)
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
        val filteredItems = sortedIndexList.filter { item ->
            item.code.contains(query, ignoreCase = true) ||
                    item.name.contains(query, ignoreCase = true)
        }

        indCommCurrAdapter.setData(filteredItems, false, false)
    }

}