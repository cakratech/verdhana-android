package com.bcasekuritas.mybest.app.base.navigation

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.feature.activity.middle.MiddleActivity
import com.bcasekuritas.mybest.app.feature.login.LoginFragment
import com.bcasekuritas.mybest.app.feature.profile.profileaccount.AccountFragment
import com.bcasekuritas.mybest.app.feature.rdn.detailhistory.DetailRdnHistoryFragment
import com.bcasekuritas.mybest.app.feature.rdn.history.RdnHistoryFragment
import com.bcasekuritas.mybest.app.feature.rdn.topup.TopUpFragment
import com.bcasekuritas.mybest.app.feature.rdn.withdraw.WithdrawFragment
import com.bcasekuritas.mybest.app.feature.runningtrade.RunningTradeFragment
import com.bcasekuritas.mybest.app.feature.stockdetail.StockDetailFragment
import com.bcasekuritas.mybest.app.feature.watchlist.ManageWatchlistFragment
import javax.inject.Inject

open class NavigationMiddle @Inject constructor(middleActivity: Activity) {

    private var containerId: Int = R.id.f_middle_container
    private var fragmentManager: FragmentManager =
        (middleActivity as MiddleActivity).supportFragmentManager

    /** Login */
    fun navigateLogin() {
        val loginFragment = LoginFragment.newInstance()
        fragmentManager.beginTransaction()
            .replace(containerId, loginFragment)
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }

    /** Stock Detail */
    fun navigateStockDetail(bundle: Bundle? = null) {
        val stockDetailFragment = StockDetailFragment.newInstance()
        stockDetailFragment.arguments = bundle
        fragmentManager.beginTransaction()
            .replace(containerId, stockDetailFragment)
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }

    /** RDN Profile Account */
    fun navigateProfileAccount() {
        val accountFragment = AccountFragment.newInstance()
        fragmentManager.beginTransaction()
            .replace(containerId, accountFragment)
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }

    /** RDN Top UP */
    fun navigateRdnTopUp() {
        val topUpFragment = TopUpFragment.newInstance()
        fragmentManager.beginTransaction()
            .replace(containerId, topUpFragment)
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }

    /** RDN Withdraw */
    fun navigateRdnWithdraw() {
        val withdrawFragment = WithdrawFragment.newInstance()
        fragmentManager.beginTransaction()
            .replace(containerId, withdrawFragment)
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }

    /** RDN History */
    fun navigateRdnHistory() {
        val rdnHistoryFragment = RdnHistoryFragment.newInstance()
        fragmentManager.beginTransaction()
            .replace(containerId, rdnHistoryFragment)
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }

    /** RDN Detail History */
    fun navigateRdnDetailHistory() {
        val detailRdnHistoryFragment = DetailRdnHistoryFragment.newInstance()
        fragmentManager.beginTransaction()
            .replace(containerId, detailRdnHistoryFragment)
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }

    /** Running Trade */
    fun navigateRunningTrade() {
        val runningTradeFragment = RunningTradeFragment.newInstance()
        fragmentManager.beginTransaction()
            .replace(containerId, runningTradeFragment)
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }
}