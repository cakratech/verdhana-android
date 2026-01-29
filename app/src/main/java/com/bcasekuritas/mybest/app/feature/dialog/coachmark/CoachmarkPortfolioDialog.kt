package com.bcasekuritas.mybest.app.feature.dialog.coachmark

import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.dialog.withvm.BaseDialogFullFragment
import com.bcasekuritas.mybest.app.domain.dto.response.PortfolioOrderItem
import com.bcasekuritas.mybest.app.domain.dto.response.PortfolioStockDataItem
import com.bcasekuritas.mybest.app.feature.dialog.coachmark.adapter.CoachmarkOrdersTabAdapter
import com.bcasekuritas.mybest.app.feature.dialog.coachmark.adapter.CoachmarkPortfolioTabAdapter
import com.bcasekuritas.mybest.databinding.DialogCoachmarkPortfolioBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CoachmarkPortfolioDialog: BaseDialogFullFragment<DialogCoachmarkPortfolioBinding, CoachmarkViewModel>() {

    override val viewModel: CoachmarkViewModel by viewModels()
    override val binding: DialogCoachmarkPortfolioBinding by autoCleaned { (DialogCoachmarkPortfolioBinding.inflate(layoutInflater)) }
    override val bindingVariable: Int = BR.vmCoachmark

    private val adapterPortfolio: CoachmarkPortfolioTabAdapter by autoCleaned { CoachmarkPortfolioTabAdapter() }
    private val adapterOrders: CoachmarkOrdersTabAdapter by autoCleaned { CoachmarkOrdersTabAdapter() }

    private var currentStep = 1


    override fun setupAdapter() {
        super.setupAdapter()
        binding.rcvPortfolio.apply {
            adapter = adapterPortfolio
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
        binding.rcvActiveOrders.apply {
            adapter = adapterOrders
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

    }

    override fun setupComponent() {
        super.setupComponent()
        binding.bottomNavigation.itemIconTintList = null
        binding.bottomNavigationCash.itemIconTintList = null
        adapterPortfolio.setData(portfolioData(), prefManager.urlIcon)
        adapterOrders.setData(ordersData())
        binding.bottomNavigation.selectedItemId = R.id.navigation_portfolio
        binding.bottomNavigationCash.selectedItemId = R.id.navigation_portfolio

        setHeightCoachmark()
    }

    override fun initOnClick() {
        super.initOnClick()
        binding.apply {
            btnCoachmarkSkipBot.setOnClickListener {
                dismiss()
            }

            btnCoachmarkNextBot.setOnClickListener {
                currentStep += 1
                nextStep()
            }

            btnCoachmarkSkipTop.setOnClickListener {
                dismiss()
            }

            btnCoachmarkNextTop.setOnClickListener {
                currentStep += 1
                nextStep()
            }

            btnCoachmarkSkipCash.setOnClickListener {
                dismiss()
            }

            btnCoachmarkNextCash.setOnClickListener {
                dismiss()
            }
        }
    }

    private fun nextStep() {
        binding.apply {
            when (currentStep) {
                2 -> {
                    lyCoachMarkBot.visibility = View.GONE
                    lyCoachMarkTop.visibility = View.VISIBLE
                    bgCoachmarkTop.visibility = View.VISIBLE
                }
                3 -> {
                    val tab = tabLayoutPortfolio.getTabAt(1)
                    tab?.select()
                    containerPortfolio.visibility = View.GONE
                    containerOrders.visibility = View.VISIBLE
                    lyCoachMarkTop.visibility = View.GONE
                    bgCoachmarkTop.visibility = View.GONE
                    lyCoachMarkBot.visibility = View.VISIBLE
                    bgCoachmarkBot.visibility = View.VISIBLE

                    tvCoachmarkStepBot.text = "3 of 4"
                    tvCoachmarkTitleBot.text = "View All Orders"
                    tvCoachmarkDescBot.text = "View all of your active orders, auto orders, or matched orders here. Swipe left to amend or withdraw active orders."
                }
                4 -> {
                    bgCoachmarkTop.visibility = View.GONE
                    bgCoachmarkBot.visibility = View.GONE
                    bgCoachmarkCash.visibility = View.VISIBLE
                    lyPortfolio.visibility = View.GONE
                    lyPortfolioCash.visibility = View.VISIBLE

                    toolbar.tvLayoutToolbarMasterTitle.text = "Cash"
                }
                else -> dismiss()
            }
            setHeightCoachmark()
        }
    }

    private fun setHeightCoachmark() {
        binding.apply {
            when (currentStep) {
                1 -> {
                    container.post {
                        val container = container.height
                        val tablayout = tabLayoutPortfolio.bottom

                        val layoutParams = bgCoachmarkBot.layoutParams
                        layoutParams.height = container - tablayout
                        bgCoachmarkBot.layoutParams = layoutParams
                    }
                }
                2 -> {
                    rcvPortfolio.post {
                        val containerHeight = container.height
                        val top = lySummaryPortfolio.bottom + containerPortfolio.top
                        val viewHolderBot = rcvPortfolio.findViewHolderForAdapterPosition(2)
                        val itemViewBot = viewHolderBot?.itemView

                        if (itemViewBot != null) {
                            val itemBot = itemViewBot.bottom + rcvPortfolio.top + containerPortfolio.top + lyContent.top

                            val layoutParamsTop = binding.bgCoachmarkTop.layoutParams
                            layoutParamsTop.height = top
                            binding.bgCoachmarkTop.layoutParams = layoutParamsTop

                            val layoutParamsBot = binding.bgCoachmarkBot.layoutParams
                            layoutParamsBot.height = containerHeight - itemBot
                            binding.bgCoachmarkBot.layoutParams = layoutParamsBot

                            adapterPortfolio.showSwipeButton()
                        }
                    }
                }
                3 -> {
                    rcvActiveOrders.post {
                        val viewHolderBot = rcvActiveOrders.findViewHolderForAdapterPosition(2)
                        val itemViewBot = viewHolderBot?.itemView
                        if (itemViewBot != null){
                            val container = container.height
                            val coachmarkBotHeight = itemViewBot.bottom + rcvActiveOrders.top + containerOrders.top + lyActiveOrders.top

                            val layoutParams = bgCoachmarkBot.layoutParams
                            layoutParams.height = container - coachmarkBotHeight
                            bgCoachmarkBot.layoutParams = layoutParams

                        }
                    }
                }

            }
        }
    }

    fun portfolioData(): ArrayList<PortfolioStockDataItem> {
        return arrayListOf<PortfolioStockDataItem>(
            PortfolioStockDataItem(stockcode = "BBCA", avgprice = 10000.0, reffprice = 10000.0, value = 10000000.0, qtyStock = 10.0, profitLoss = 0.0, pct = 0.0),
            PortfolioStockDataItem(stockcode = "BMRI", avgprice = 5000.0, reffprice = 5800.0, value = 5800000.0, qtyStock = 10.0, profitLoss = 800000.0, pct = 16.0),
            PortfolioStockDataItem(stockcode = "ULTJ", avgprice = 1500.0, reffprice = 1350.0, value = 13500000.0, qtyStock = 10.0, profitLoss = -1500000.0, pct = -10.0),
        )
    }

    fun ordersData(): ArrayList<PortfolioOrderItem> {
        return arrayListOf(
            PortfolioOrderItem(stockCode = "KLBF", price = 1850.0, orderQty = 1000.0, buySell = "S", status = "O", timeInForce = "0", ordPeriod = 1746002886),
            PortfolioOrderItem(stockCode = "KLBF", price = 1850.0, orderQty = 500.0, buySell = "S", status = "A", timeInForce = "0", ordPeriod = 1746002886),
            PortfolioOrderItem(stockCode = "BBCA", price = 9300.0, orderQty = 1000.0, buySell = "B", status = "M", timeInForce = "0", ordPeriod = 1746002886),
            PortfolioOrderItem(stockCode = "BBCA", price = 0.0, orderQty = 1000.0, buySell = "B", status = "W", timeInForce = "0", ordPeriod = 1746002886),
            PortfolioOrderItem(stockCode = "BBCA", price = 0.0, orderQty = 1000.0, buySell = "B", status = "W", timeInForce = "0", ordPeriod = 1746002886)
        )
    }

}