package com.bcasekuritas.mybest.app.base.navigation

import android.app.Activity
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.FragmentManager
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.feature.activity.middle.MiddleActivity
import com.bcasekuritas.mybest.app.feature.stockdetail.financial.balancesheet.BalanceSheetFragment
import com.bcasekuritas.mybest.app.feature.stockdetail.financial.cashflow.CashflowFragment
import com.bcasekuritas.mybest.app.feature.stockdetail.financial.incomestatement.IncomeStatementFragment
import com.bcasekuritas.mybest.app.feature.stockdetail.financial.overview.FinancialOverviewFragment
import javax.inject.Inject

open class NavigationFinancial @Inject constructor(middleActivity: Activity) {

    private var containerId: Int = R.id.f_container_financial
    private var fragmentManager: FragmentManager = (middleActivity as MiddleActivity).supportFragmentManager

    fun navigateFinancialOverview() {
        Handler(Looper.getMainLooper()).postDelayed({
            val financialOverviewFragment = FinancialOverviewFragment.newInstance()
            fragmentManager.beginTransaction()
                .replace(containerId, financialOverviewFragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }, 200)
    }

    fun navigateFinancialIncomeStatement() {
        Handler(Looper.getMainLooper()).postDelayed({
            val incomeStatementFragment = IncomeStatementFragment.newInstance()
            fragmentManager.beginTransaction()
                .replace(containerId, incomeStatementFragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }, 200)
    }

    fun navigateFinancialBalanceSheet() {
        Handler(Looper.getMainLooper()).postDelayed({
            val balanceSheetFragment = BalanceSheetFragment.newInstance()
            fragmentManager.beginTransaction()
                .replace(containerId, balanceSheetFragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }, 200)
    }

    fun navigateFinancialCashflow() {
        Handler(Looper.getMainLooper()).postDelayed({
            val cashflowFragment = CashflowFragment.newInstance()
            fragmentManager.beginTransaction()
                .replace(containerId, cashflowFragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }, 200)
    }
}