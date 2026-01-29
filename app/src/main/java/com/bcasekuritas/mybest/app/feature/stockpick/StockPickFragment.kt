package com.bcasekuritas.mybest.app.feature.stockpick

import android.net.Uri
import android.os.Build
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.feature.activity.middle.MiddleActivity
import com.bcasekuritas.mybest.app.feature.stockpick.adapter.StockPickAdapter
import com.bcasekuritas.mybest.databinding.FragmentStockPickBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.constant.ConstKeys
import com.bcasekuritas.mybest.ext.constant.NavKeys
import com.bcasekuritas.mybest.ext.listener.OnClickStr
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@AndroidEntryPoint
class StockPickFragment : BaseFragment<FragmentStockPickBinding, StockPickViewModel>(), OnClickStr {

    override val bindingVariable: Int = BR.vmStockPick
    override val viewModel: StockPickViewModel by viewModels()
    override val binding: FragmentStockPickBinding by autoCleaned { (FragmentStockPickBinding.inflate(layoutInflater)) }

    private val adapter: StockPickAdapter by autoCleaned { StockPickAdapter(requireContext(), this) }
    private val webView = CustomTabsIntent.Builder().build()

    private var urlViewReport = ""

    companion object {
        fun newInstance() = StockPickFragment()
    }

    override fun setupComponent() {
        super.setupComponent()
        binding.apply {
            toolbar.tvLayoutToolbarMasterTitle.text = "Stock Pick"
            toolbar.ivLayoutToolbarMasterIconLeft.setOnClickListener {
                onBackPressed()
            }

            ivCloseInfoWarning.setOnClickListener {
                groupInfoWarning.isGone = true
            }

            lyViewReport.setOnClickListener {
                val url = "https://docs.google.com/gview?embedded=true&url=$urlViewReport"
                if (url.isBlank() || !(url.startsWith("http://") || url.startsWith("https://"))) {
                    Toast.makeText(context, "Unavailable url file", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                try {
                    webView.launchUrl(requireContext(), Uri.parse(url))
                } catch (ignore: Exception) {}
            }
        }
    }

    override fun setupAdapter() {
        super.setupAdapter()
        binding.apply {
            rcvStockPick.adapter = adapter
            rcvStockPick.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun initAPI() {
        super.initAPI()
        val userId = prefManager.userId

        viewModel.getStockPick(userId, prefManager.sessionId, true)
        viewModel.getStockPickReport(userId, prefManager.sessionId)

    }

    @RequiresApi(Build.VERSION_CODES.O)
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

        viewModel.getStockPickResult.observe(viewLifecycleOwner){
            binding.lyViewReport.visibility = if (it?.newsStockPickData?.newsStockPickDetilList?.isNotEmpty() == true) View.VISIBLE else View.GONE
            if (it?.newsStockPickData?.newsStockPickDetilCount != 0) {
                it?.newsStockPickData?.newsStockPickDetilList?.let { it1 -> adapter.setData(it1) }
            }
        }

        viewModel.getStockPickReportResult.observe(viewLifecycleOwner){
            urlViewReport = ConstKeys.PROMO_BANNER_URL + it?.reportFileObject
        }
    }

    override fun onClickStr(value: String?) {
        MiddleActivity.startIntentParam(requireActivity(), NavKeys.KEY_FM_STOCK_DETAIL, value ?: "", "")
    }


}