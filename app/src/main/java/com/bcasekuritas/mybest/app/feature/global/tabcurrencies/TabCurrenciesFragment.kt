package com.bcasekuritas.mybest.app.feature.global.tabcurrencies

import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.domain.dto.response.IndCommCurrHelper
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.app.feature.global.GlobalSharedViewModel
import com.bcasekuritas.mybest.app.feature.global.adapter.IndCommCurrAdapter
import com.bcasekuritas.mybest.databinding.FragmentTabCurrenciesBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@AndroidEntryPoint
class TabCurrenciesFragment : BaseFragment<FragmentTabCurrenciesBinding, TabCurrenciesViewModel>() {

    override val bindingVariable: Int = BR.vmPortfolio
    override val viewModel: TabCurrenciesViewModel by viewModels()
    override val binding: FragmentTabCurrenciesBinding by autoCleaned { (FragmentTabCurrenciesBinding.inflate(layoutInflater)) }

    private val indCommCurrAdapter: IndCommCurrAdapter by autoCleaned { IndCommCurrAdapter(prefManager.urlIcon) }

    private var sortedCurrenciesList = listOf<IndCommCurrHelper>()

    companion object {
        fun newInstance() = TabCurrenciesFragment()
    }

    override fun setupAdapter() {
        super.setupAdapter()

        binding.rcvTabIndCommCurr.adapter = indCommCurrAdapter
    }

    private val sharedViewModel: GlobalSharedViewModel by lazy {
        ViewModelProvider(requireActivity()).get(GlobalSharedViewModel::class.java)
    }

    override fun initAPI() {
        super.initAPI()
        viewModel.getGlobalCurrency(prefManager.userId, prefManager.sessionId)
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.imqConnectionListener.connListenerLiveData.observe(this) { value ->
            when (value) {
                "Recovered" -> {
                    viewModel.imqConnectionListener.onListener("")
                    viewModel.getGlobalCurrency(prefManager.userId, prefManager.sessionId)
                }

                else -> {}
            }
        }

        viewModel.getGlobalCurrencyResult.observe(viewLifecycleOwner){
            when(it){
                is Resource.Success -> {
                    when(it.data?.status){
                        "0" -> {
                            val currenciesData = arrayListOf<IndCommCurrHelper>()
                            it.data.currenciesDataList.map { item ->

                                    currenciesData.add(
                                        IndCommCurrHelper(
                                            code = item.macroName,
                                            name = item.descriptionMacro,
                                            value = item.value,
                                            change = item.nominalChange,
                                            changePct = item.change
                                        )
                                    )
                            }

                            sortedCurrenciesList = currenciesData.sortedBy { it.code }

                            indCommCurrAdapter.setData(sortedCurrenciesList, true, false)
                        }
                    }
                }

                else -> {}
            }

            sharedViewModel.getQuerySearch.observe(viewLifecycleOwner) {
                filterItems(it)
            }
        }
    }




    private fun filterItems(query: String) {
        val filteredItems = sortedCurrenciesList.filter { item ->
            item.code.contains(query, ignoreCase = true)||
            item.name.contains(query, ignoreCase = true)
        }

        indCommCurrAdapter.setData(filteredItems, true, false)
    }

}