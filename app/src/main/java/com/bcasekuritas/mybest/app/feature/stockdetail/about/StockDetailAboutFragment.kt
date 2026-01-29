package com.bcasekuritas.mybest.app.feature.stockdetail.about

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.app.feature.stockdetail.StockDetailSharedViewModel
import com.bcasekuritas.mybest.databinding.FragmentStockDetailAboutBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.common.capitalizeWords
import com.bcasekuritas.mybest.ext.date.DateUtils.convertLongToDate
import com.bcasekuritas.rabbitmq.proto.news.StockProfileDirector
import java.util.Locale

@FragmentScoped
@AndroidEntryPoint
class StockDetailAboutFragment : BaseFragment<FragmentStockDetailAboutBinding, StockDetailAboutViewModel>() {

    @FragmentScoped
    override val bindingVariable: Int = BR.vmKeyStats
    override val viewModel: StockDetailAboutViewModel by viewModels()
    override val binding: FragmentStockDetailAboutBinding by autoCleaned {
        FragmentStockDetailAboutBinding.inflate(
            layoutInflater
        )
    }

    lateinit var sharedViewModel: StockDetailSharedViewModel
    private var stockCode = ""
    private var listViceDirector: MutableList<String> = mutableListOf()
    private var listDirector: MutableList<String> = mutableListOf()
    private var listIndependentCommisioner: MutableList<String> = mutableListOf()
    private var listCommisioner: MutableList<String> = mutableListOf()
    var viceDirector = ""
    var director = ""
    var commisioner = ""
    var independenCommissioner = ""

    companion object {
        fun newInstance() = StockDetailAboutFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity()).get(StockDetailSharedViewModel::class.java)
    }

    override fun setupComponent() {
        super.setupComponent()
        showLoading()
        clearList()
    }

    override fun initAPI() {
        super.initAPI()

        val userId = prefManager.userId
        val sessionId = prefManager.sessionId
        stockCode = prefManager.stockDetailCode

        clearList()

        viewModel.getCompanyProfile(userId, sessionId, stockCode)
    }

    @SuppressLint("SetTextI18n")
    override fun setupObserver() {
        super.setupObserver()
        sharedViewModel.getRefreshFragmentResult.observe(viewLifecycleOwner){
            if (it == true){
                initAPI()
            }
        }

        sharedViewModel.getStockCodeChangeResult.observe(viewLifecycleOwner){
            if (it == true){
                showLoading()
                initAPI()
            }
        }

        viewModel.getCompanyProfileResult.observe(viewLifecycleOwner) {
            if (it != null){
                when (it) {
                    is Resource.Loading -> {
                        showLoading()
                    }

                    is Resource.Success -> {
                        when (it.data?.status) {
                            0.toString() -> {
                                if (it.data != null) {
                                    it.data.data.let { res ->
                                        binding.apply {
                                            listDirector.clear()
                                            listViceDirector.clear()
                                            listCommisioner.clear()
                                            listIndependentCommisioner.clear()

                                            tvSdAboutCompanyName.text    = ": ${res.stockName ?: "-"}"
                                            tvSdAboutDate.text           = ": ${convertLongToDate(res.ipoListingDate)}"
                                            tvSdAboutDescription.text    = res.generalInformation ?: "-"
                                            tvSdAboutBusinessField.text  = res.sector ?: "-"

                                            if (res.directorsList.isNotEmpty()) {
                                                for (data in res.directorsList){
                                                    if (data.directorRole.lowercase().contains("wakil")){
                                                        listViceDirector.add(capitalizeWords(data.directorName.toLowerCase(Locale.ROOT)))
                                                    }else if (data.directorRole.lowercase().contains("presiden") || data.directorRole.lowercase().contains("utama")){
                                                        tvSdAboutPresidentDirector.text = ": ${capitalizeWords(data.directorName.toLowerCase(Locale.ROOT))}"
                                                    }else{
                                                        listDirector.add(capitalizeWords(data.directorName.toLowerCase(Locale.ROOT)))
                                                    }
                                                }
                                            } else {
                                                tvSdAboutPresidentDirector.text = ": -"
                                            }


                                            if (res.directorsList.isNotEmpty()) {
                                                for (data in res.commissionersList){
                                                    if (data.commissionerRole.lowercase().contains("presiden") || data.commissionerRole.lowercase().contains("utama")){
                                                        tvSdAboutPresidentCommisioner.text = ": ${capitalizeWords(data.commissionerName.toLowerCase(Locale.ROOT))}"
                                                    }else if (data.commissionerRole.lowercase().contains("komisaris")){
                                                        if (data.independent.lowercase().contains("ya")){
                                                            listIndependentCommisioner.add(capitalizeWords(data.commissionerName.toLowerCase(Locale.ROOT)))
                                                        }else{
                                                            listCommisioner.add(capitalizeWords(data.commissionerName.toLowerCase(Locale.ROOT)))
                                                        }
                                                    }
                                                }
                                            } else {
                                                tvSdAboutPresidentCommisioner.text = ": -"
                                            }


                                            if (listViceDirector.isNotEmpty()){
                                                lvSdAboutVicePresidentDirector.text = listViceDirector.joinToString("\n\n")
                                            } else {
                                                lvSdAboutVicePresidentDirector.text = "-"
                                            }

                                            if (listDirector.isNotEmpty()){
                                                lvSdAboutDirector.text = listDirector.joinToString("\n\n")
                                            } else {
                                                lvSdAboutDirector.text = "-"
                                            }

                                            if (listIndependentCommisioner.isNotEmpty()){
                                                lvSdAboutIndependentCommisioner.text = listIndependentCommisioner.joinToString("\n\n")
                                            } else {
                                                lvSdAboutIndependentCommisioner.text = "-"
                                            }

                                            if (listCommisioner.isNotEmpty()){
                                                lvSdAboutCommisioner.text = listCommisioner.joinToString("\n\n")
                                            } else {
                                                lvSdAboutCommisioner.text = "-"
                                            }
                                        }
                                    }
                                    hideLoading()
                                } else{
                                    emptyData()
                                }
                            }

                            else -> emptyData()
                        }
                    }

                    is Resource.Failure -> {
                        emptyData()
                        Toast.makeText(requireContext(), it.failureData.message, Toast.LENGTH_SHORT)
                            .show()
                    }

                    else -> {
                        emptyData()
                    }
                }
            }else{
                emptyData()
            }
        }
    }

    private fun clearList(){
        listDirector.clear()
        listViceDirector.clear()
        listCommisioner.clear()
        listIndependentCommisioner.clear()
    }

    private fun emptyData(){
        hideLoading()
        binding.apply {
            tvSdAboutCompanyName.text    = "-"
            tvSdAboutDate.text           = "-"
            tvSdAboutDescription.text    = "-"
            tvSdAboutBusinessField.text  = "-"
            tvSdAboutPresidentDirector.text = "-"
            lvSdAboutVicePresidentDirector.text = "-"
            lvSdAboutDirector.text = "-"
            tvSdAboutPresidentCommisioner.text = "-"
            lvSdAboutIndependentCommisioner.text = "-"
            lvSdAboutCommisioner.text = "-"

        }
    }
}