package com.bcasekuritas.mybest.app.feature.rdn.topup.branch


import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.domain.dto.response.source.HowToTransferRes
import com.bcasekuritas.mybest.app.feature.rdn.adapter.TransferMBankingAdapter
import com.bcasekuritas.mybest.app.feature.rdn.topup.mbanking.TransferMBankingFragment
import com.bcasekuritas.mybest.databinding.FragmentTransferBranchBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.common.copyToClipboardFromString
import com.bcasekuritas.mybest.ext.listener.OnClickStr

@FragmentScoped
@AndroidEntryPoint
class TransferBranchFragment :
    BaseFragment<FragmentTransferBranchBinding, TransferBranchViewModel>(), OnClickStr {

    @FragmentScoped
    override val bindingVariable: Int = BR.vmBranch
    override val viewModel: TransferBranchViewModel by viewModels()
    override val binding: FragmentTransferBranchBinding by autoCleaned {
        (FragmentTransferBranchBinding.inflate(
            layoutInflater
        ))
    }

    private lateinit var howToTransferList: ArrayList<HowToTransferRes>
    private val transferMBankingAdapter: TransferMBankingAdapter by autoCleaned {
        TransferMBankingAdapter(
           this
        )
    }

    companion object {
        fun newInstance() = TransferMBankingFragment()
    }

    override fun setupObserver() {
        super.setupObserver()
        howToTransferList = ArrayList()
        howToTransferListItems()
        transferMBankingAdapter.setData(howToTransferList)
    }

    override fun setupAdapter() {
        super.setupAdapter()
        binding.rcvTransferBranch.layoutManager = LinearLayoutManager(requireActivity())
        binding.rcvTransferBranch.setHasFixedSize(true)
        binding.rcvTransferBranch.adapter = transferMBankingAdapter
    }

    private fun howToTransferListItems() {
        howToTransferList.add(HowToTransferRes("Login ke m-BCA di applikasi BCA Mobile"))
        howToTransferList.add(HowToTransferRes("Pilih menu m-Transfer"))
        howToTransferList.add(HowToTransferRes("Daftarkan rekening RDN dengan klik Antar Rekening di Daftar Transfer"))
        howToTransferList.add(HowToTransferRes("Isi nomor rekening RDN kamu \n1284018401"))
        howToTransferList.add(HowToTransferRes("Setelah rekening RDN terdaftar, pilih Antar Rekening di menu Transfer"))
        howToTransferList.add(HowToTransferRes("Pilih rekening RDN di Rekening Tujuan dan isi nominal yang diinginkan"))
        howToTransferList.add(HowToTransferRes("Klik Send dan ikuti langkah selanjutnya"))
        howToTransferList.add(HowToTransferRes("Deposito kamu akan diproses"))
    }

    override fun onClickStr(value: String?) {
        if (value?.isNotEmpty() == true) {
            copyToClipboardFromString(requireActivity(), value, "Rekening RDN Number Copied")
        }
    }

}