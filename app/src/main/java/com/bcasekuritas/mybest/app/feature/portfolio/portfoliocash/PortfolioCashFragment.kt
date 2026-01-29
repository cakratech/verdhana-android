package com.bcasekuritas.mybest.app.feature.portfolio.portfoliocash

import androidx.fragment.app.viewModels
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.databinding.FragmentTabCashPortfolioBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.other.formatPercent
import com.bcasekuritas.mybest.ext.other.formatPriceWithoutDecimal
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@AndroidEntryPoint
class PortfolioCashFragment :
    BaseFragment<FragmentTabCashPortfolioBinding, PortfolioCashViewModel>() {

    override val bindingVariable: Int = BR.vmPortfolioCash
    override val viewModel: PortfolioCashViewModel by viewModels()
    override val binding: FragmentTabCashPortfolioBinding by autoCleaned {
        (FragmentTabCashPortfolioBinding.inflate(
            layoutInflater
        ))
    }
    private var userId = ""
    private var sessionId = ""
    private var cifCode = ""
    private var accNo = ""

    override fun setupComponent() {
        super.setupComponent()

        binding.swplCashPortfolio.setOnRefreshListener {
            binding.swplCashPortfolio.isRefreshing = true

            viewModel.getCashPos(userId, cifCode, sessionId)
            viewModel.getSettlementSched(userId, accNo, sessionId)
        }
    }

    override fun initOnClick() {
        super.initOnClick()
        binding.apply {
            toolbar.tvLayoutToolbarMasterTitle.text = "Cash"
            toolbar.ivLayoutToolbarMasterIconLeft.setOnClickListener {
                onBackPressed()
            }

            llRdn.setOnClickListener {
                showDialogPortfolioSummaryInfoBottom(
                    "RDN",
                    "Saldo yang tersedia di rekening RDN. Jika kamu memiliki open buy atau transaksi beli yang belum settle, saldo yang bisa ditarik mungkin akan lebih sedikit.",
                    "The balance in the RDN account includes the funds you use for unsettled transactions.",
                    parentFragmentManager
                )
            }

            llRdnT2.setOnClickListener {
                showDialogPortfolioSummaryInfoBottom(
                    "RDN T+2",
                    "Saldo yang akan tersedia di rekening RDN setelah proses penyelesaian transaksi 2 hari bursa.",
                    "The balance that will be available in the RDN account after the settlement process of 2 trading days.",
                    parentFragmentManager
                )
            }

            llOpenBuy.setOnClickListener {
                showDialogPortfolioSummaryInfoBottom(
                    "Open Buy",
                    "Saldo yang digunakan untuk melakukan transaksi beli yang masih open.",
                    "Funds used for open buy transactions..",
                    parentFragmentManager
                )
            }

            llCurrentRatio.setOnClickListener {
                showDialogPortfolioSummaryInfoBottom(
                    "Current Ratio",
                    "Nilai terhutang dalam bentuk %.",
                    "Outstanding amount in percentage.",
                    parentFragmentManager
                )
            }

            llAvailableCash.setOnClickListener {
                showDialogPortfolioSummaryInfoBottom(
                    "Available Cash",
                    "Dana tersedia yang dapat digunakan untuk melakukan aktivitas pembelian saham.",
                    "The amount of cash available for stock purchases.",
                    parentFragmentManager
                )
            }

            llTotalAsset.setOnClickListener {
                showDialogPortfolioSummaryInfoBottom(
                    "Total Asset Value",
                    "Nilai seluruh aset nasabah yang terdiri dari market value dan cash available.",
                    "The total value of your assets, including market value and cash available.",
                    parentFragmentManager
                )
            }
        }
    }

    override fun initAPI() {
        super.initAPI()
        userId = prefManager.userId
        sessionId = prefManager.sessionId
        cifCode = prefManager.cifCode
        accNo = prefManager.accno

        viewModel.getCashPos(userId, cifCode, sessionId)
        viewModel.getSettlementSched(userId, accNo, sessionId)
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.imqConnectionListener.connListenerLiveData.observe(this) { value ->
            when (value) {
                "Recovered" -> {
                    viewModel.imqConnectionListener.onListener("")
                    initAPI()
                }

                else -> {}
            }
        }

        viewModel.getCashPosResult.observe(viewLifecycleOwner) {
            binding.swplCashPortfolio.isRefreshing = false
            if (it != null) {
                if (it.groupCashPosCount != 0) {
                    it.groupCashPosList.forEach { item ->
                        if (item.ccGroupCode == accNo) {
                            binding.tvRdn.text = "${item.cashonhand.formatPriceWithoutDecimal()}"
                            binding.tvRdnTTwo.text = "${item.realCashBalance.formatPriceWithoutDecimal()}"
                            binding.tvOpenBuy.text = "${item.tbuyopen.formatPriceWithoutDecimal()}"
                            binding.tvCurrentRatio.text =
                                "${(item.realRatio * 100).formatPercent()}%"
                            binding.tvCashAvailable.text =
                                "${item.potCashBalance.formatPriceWithoutDecimal()}"
                            binding.tvTotalAssetValue.text = "${item.netAsset.formatPriceWithoutDecimal()}"
                        }
                    }
                }
            }
        }

        viewModel.getSettlementSchedResult.observe(viewLifecycleOwner) {
            binding.swplCashPortfolio.isRefreshing = false
            if (it != null) {
                for (settlementSched in it.settlementScheduleInfoList) {
                    binding.apply {
                        when (settlementSched.account) {
                            "Payable" -> {
                                tvReceivableToday.text =
                                    settlementSched.tzero.formatPriceWithoutDecimal()
                                tvReceivableTOne.text =
                                    settlementSched.tone.formatPriceWithoutDecimal()
                                tvReceivableTTwo.text =
                                    settlementSched.ttwo.formatPriceWithoutDecimal()
                            }

                            "Receivable" -> {
                                tvPayableToday.text =
                                    settlementSched.tzero.formatPriceWithoutDecimal()
                                tvPayableTOne.text =
                                    settlementSched.tone.formatPriceWithoutDecimal()
                                tvPayableTTwo.text =
                                    settlementSched.ttwo.formatPriceWithoutDecimal()
                            }

                            "Net Amount" -> {
                                tvNetAmountToday.text =
                                    settlementSched.tzero.formatPriceWithoutDecimal()
                                tvNetAmountTOne.text =
                                    settlementSched.tone.formatPriceWithoutDecimal()
                                tvNetAmountTTwo.text =
                                    settlementSched.ttwo.formatPriceWithoutDecimal()
                            }

                            "Interest" -> {
                                tvInterestToday.text =
                                    settlementSched.tzero.formatPriceWithoutDecimal()
                                tvInterestTOne.text =
                                    settlementSched.tone.formatPriceWithoutDecimal()
                                tvInterestTTwo.text =
                                    settlementSched.ttwo.formatPriceWithoutDecimal()
                            }

                            "Fund Transfer" -> {
                                tvFundTransferToday.text =
                                    settlementSched.tzero.formatPriceWithoutDecimal()
                                tvFundTransferTOne.text =
                                    settlementSched.tone.formatPriceWithoutDecimal()
                                tvFundTransferTTwo.text =
                                    settlementSched.ttwo.formatPriceWithoutDecimal()
                            }

                            "Net Cash" -> {
                                tvCashToday.text = settlementSched.tzero.formatPriceWithoutDecimal()
                                tvCashTOne.text = settlementSched.tone.formatPriceWithoutDecimal()
                                tvCashTTwo.text = settlementSched.ttwo.formatPriceWithoutDecimal()
                            }
                        }

                    }
                }
            }
        }
    }
}