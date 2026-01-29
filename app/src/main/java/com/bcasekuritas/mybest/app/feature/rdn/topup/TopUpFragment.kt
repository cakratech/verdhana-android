package com.bcasekuritas.mybest.app.feature.rdn.topup


import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.domain.dto.response.source.HowToTransferRes
import com.bcasekuritas.mybest.app.feature.news.adapter.NewsPagerAdapter
import com.bcasekuritas.mybest.app.feature.rdn.adapter.TopUpViewPagerAdapter
import com.bcasekuritas.mybest.app.feature.rdn.adapter.TransferMBankingAdapter
import com.bcasekuritas.mybest.app.feature.rdn.topup.adapter.TopUpPagerAdapter
import com.bcasekuritas.mybest.databinding.FragmentTopUpBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.common.copyToClipboardFromString
import com.bcasekuritas.mybest.ext.listener.OnClickStr
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@AndroidEntryPoint
class TopUpFragment : BaseFragment<FragmentTopUpBinding, TopUpViewModel>(), OnClickStr {

    @FragmentScoped
    override val bindingVariable: Int = BR.vmTopUp
    override val viewModel: TopUpViewModel by viewModels()
    override val binding: FragmentTopUpBinding by autoCleaned {(FragmentTopUpBinding.inflate(layoutInflater))}

    private lateinit var sharedViewModel: TopUpSharedViewModel

    private lateinit var howToTransferList : ArrayList<HowToTransferRes>
    private val transferMBankingAdapter: TransferMBankingAdapter by autoCleaned { TransferMBankingAdapter(this) }

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    companion object {
        fun newInstance() = TopUpFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity()).get(TopUpSharedViewModel::class.java)
    }

    override fun setupViewPager() {
        super.setupViewPager()
        viewPager = binding.viewPager
        tabLayout = binding.tabLayout

        val adapter = TopUpPagerAdapter(this)
        viewPager.adapter = adapter
        viewPager.isUserInputEnabled = false
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "M-Banking"
                1 -> "ATM"
                else -> ""
            }
        }.attach()
    }

    override fun setupComponent() {
        binding.lyToolbarTopup.tvLayoutToolbarMasterTitle.text = getString(R.string.text_top_up)
        binding.lyToolbarTopup.ivLayoutToolbarMasterIconLeft.visibility = View.VISIBLE
        binding.lyToolbarTopup.ivLayoutToolbarMasterIconLeft.setImageResource(R.drawable.ic_back)

        binding.swipeRefreshLayoutTopUp.isEnabled = false

        binding.lyToolbarTopup.ivLayoutToolbarMasterIconLeft.setOnClickListener {
            onBackPressed()
        }

    }

    override fun initAPI() {
        super.initAPI()
        val userId = prefManager.userId
        val sessionId = prefManager.sessionId
        val cifCode = prefManager.cifCode

        viewModel.getAccountInfo(userId, cifCode, sessionId)
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.clientInfoResult.observe(viewLifecycleOwner){
            it?.let {
                if (it.accountGroupList.isNotEmpty()) {
                    val data = it.accountGroupList[0]
                    val bankName = data.rdnBankName + " - " + data.rdnAccNo
                    val accName = data.rdnAccName

                    binding.tvTopupNumAccount.text = bankName
                    binding.tvProfileTopupAccount.text = accName

                    sharedViewModel.setAccountGroupInfo(it.accountGroupList[0])
                }
            }
        }
    }

    // copy rdn code
    override fun onClickStr(value: String?) {
        if (value?.isNotEmpty() == true) {
            copyToClipboardFromString(requireActivity(), value, "RDN number copied")
        }
    }
}