package com.bcasekuritas.mybest.app.feature.rdn.topup.atm


import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.domain.dto.response.source.HowToTransferRes
import com.bcasekuritas.mybest.app.feature.rdn.adapter.TransferMBankingAdapter
import com.bcasekuritas.mybest.app.feature.rdn.topup.TopUpSharedViewModel
import com.bcasekuritas.mybest.app.feature.rdn.topup.mbanking.TransferMBankingFragment
import com.bcasekuritas.mybest.databinding.FragmentTransferAtmBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.common.copyToClipboardFromString
import com.bcasekuritas.mybest.ext.listener.OnClickStr


@FragmentScoped
@AndroidEntryPoint
class TransferAtmFragment :
    BaseFragment<FragmentTransferAtmBinding, TransferAtmViewModel>(), OnClickStr {

    @FragmentScoped
    override val bindingVariable: Int = BR.vmAtm
    override val viewModel: TransferAtmViewModel by viewModels()
    override val binding: FragmentTransferAtmBinding by autoCleaned {
        (FragmentTransferAtmBinding.inflate(
            layoutInflater
        ))
    }

    private lateinit var howToTransferList: ArrayList<HowToTransferRes>
    private val transferMBankingAdapter: TransferMBankingAdapter by autoCleaned {
        TransferMBankingAdapter(
            this
        )
    }

    private lateinit var sharedViewModel: TopUpSharedViewModel

    companion object {
        fun newInstance() = TransferMBankingFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity()).get(TopUpSharedViewModel::class.java)
    }

    override fun setupObserver() {
        super.setupObserver()
        sharedViewModel.getAccountGroupInfo.observe(viewLifecycleOwner) {data ->
            if (data != null) {
                howToTransferList = ArrayList()
                howToTransferListItems(data.rdnAccNo)
                transferMBankingAdapter.setData(howToTransferList)
                transferMBankingAdapter.setRdnCode(data.rdnAccNo)


            }
        }
    }

    override fun setupAdapter() {
        super.setupAdapter()
        binding.rcvTransferAtm.layoutManager = LinearLayoutManager(requireActivity())
        binding.rcvTransferAtm.setHasFixedSize(true)
        binding.rcvTransferAtm.adapter = transferMBankingAdapter
    }

    private fun howToTransferListItems(rdnAcc: String) {
        howToTransferList.add(HowToTransferRes("Masukkan Kartu ATM dan PIN ATM"))
        howToTransferList.add(HowToTransferRes("Pilih menu Transfer"))
        howToTransferList.add(HowToTransferRes("Pilih Rekening tujuan sesuai Bank RDN"))
        howToTransferList.add(HowToTransferRes("Masukkan nomor RDN $rdnAcc sebagai tujuan"))
        howToTransferList.add(HowToTransferRes("Masukkan nominal deposit yang diinginkan"))
        howToTransferList.add(HowToTransferRes("Jika Bank RDN berbeda dengan Bank Pengirim, terdapat biaya transfer sesuai dengan kebijakan masing-masing bank yang berlaku"))
        howToTransferList.add(HowToTransferRes("Ikuti langkah selanjutnya untuk menyelesaikan proses deposit"))
    }

    override fun onClickStr(value: String?) {
        if (value?.isNotEmpty() == true) {
            copyToClipboardFromString(requireActivity(), value, "Rekening RDN Number Copied")
        }
    }

}