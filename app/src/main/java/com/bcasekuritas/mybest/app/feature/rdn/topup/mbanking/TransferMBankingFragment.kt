package com.bcasekuritas.mybest.app.feature.rdn.topup.mbanking

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.domain.dto.response.source.HowToTransferRes
import com.bcasekuritas.mybest.app.feature.rdn.adapter.TransferMBankingAdapter
import com.bcasekuritas.mybest.app.feature.rdn.topup.TopUpSharedViewModel
import com.bcasekuritas.mybest.databinding.FragmentTransferMBankingBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.common.copyToClipboardFromString
import com.bcasekuritas.mybest.ext.listener.OnClickStr
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@AndroidEntryPoint
class TransferMBankingFragment : BaseFragment<FragmentTransferMBankingBinding, TransferMBankingViewModel>(), OnClickStr {

    @FragmentScoped
    override val bindingVariable: Int = BR.vmMBanking
    override val viewModel: TransferMBankingViewModel by viewModels()
    override val binding: FragmentTransferMBankingBinding by autoCleaned {(FragmentTransferMBankingBinding.inflate(layoutInflater))}

    private lateinit var howToTransferList : ArrayList<HowToTransferRes>
    private val transferMBankingAdapter: TransferMBankingAdapter by autoCleaned { TransferMBankingAdapter(this) }

    private lateinit var sharedViewModel: TopUpSharedViewModel

    companion object {
        fun newInstance() = TransferMBankingFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity()).get(TopUpSharedViewModel::class.java)
        howToTransferList = ArrayList()
    }

    override fun setupComponent() {
        super.setupComponent()
    }

    override fun setupObserver() {
        super.setupObserver()

        sharedViewModel.getAccountGroupInfo.observe(viewLifecycleOwner) {data ->
            if (data != null) {
                howToTransferListItems(data.rdnAccNo)
                transferMBankingAdapter.setData(howToTransferList)
                transferMBankingAdapter.setRdnCode(data.rdnAccNo)


            }
        }
    }

    override fun setupAdapter() {
        super.setupAdapter()
        binding.rcvTransferMBanking.layoutManager = LinearLayoutManager(requireActivity())
        binding.rcvTransferMBanking.setHasFixedSize(true)
        binding.rcvTransferMBanking.adapter = transferMBankingAdapter
    }

    private fun howToTransferListItems(rdn: String){
        howToTransferList.add(HowToTransferRes("Login ke Mobile Banking atau Internet Banking"))
        howToTransferList.add(HowToTransferRes("Pilih menu Transfer"))
        howToTransferList.add(HowToTransferRes("Pilih Rekening tujuan sesuai Bank RDN"))
        howToTransferList.add(HowToTransferRes("Masukkan nomor RDN $rdn sebagai tujuan"))
        howToTransferList.add(HowToTransferRes("Masukkan nominal deposit yang diinginkan"))
        howToTransferList.add(HowToTransferRes("Jika Bank RDN berbeda dengan Bank Pengirim, terdapat biaya transfer sesuai dengan kebijakan masing-masing bank yang berlaku"))
        howToTransferList.add(HowToTransferRes("Ikuti langkah selanjutnya untuk menyelesaikan proses deposit"))
    }

    // copy rdn code
    override fun onClickStr(value: String?) {
        if (value?.isNotEmpty() == true) {
            copyToClipboardFromString(requireActivity(), value, "RDN number copied")
        }
    }
}