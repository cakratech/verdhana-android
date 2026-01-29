package com.bcasekuritas.mybest.app.feature.profile.profileaccount

import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.databinding.FragmentAccountBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.common.copyToClipboardFromString
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@AndroidEntryPoint
class AccountFragment : BaseFragment<FragmentAccountBinding, AccountViewModel>() {

    @FragmentScoped
    override val bindingVariable: Int = BR.vmAccount
    override val viewModel: AccountViewModel by viewModels()
    override val binding: FragmentAccountBinding by autoCleaned {
        (FragmentAccountBinding.inflate(
            layoutInflater
        ))
    }

    private  var clientCode = ""
    private  var loginId = ""
    private var userId = ""
    private var sessionId = ""
    private var accNo = ""
    private var cifCode = ""


    companion object {
        fun newInstance() = AccountFragment()
    }

    override fun setupComponent() {
        binding.lyToolbarProfileAccount.tvLayoutToolbarMasterTitle.text = getString(R.string.text_account)
        binding.lyToolbarProfileAccount.ivLayoutToolbarMasterIconLeft.visibility = View.VISIBLE
        binding.lyToolbarProfileAccount.ivLayoutToolbarMasterIconLeft.setImageResource(R.drawable.ic_back)

        binding.swipeRefreshLayoutAccount.isRefreshing = false
        binding.swipeRefreshLayoutAccount.isEnabled = false

        binding.lyToolbarProfileAccount.ivLayoutToolbarMasterIconLeft.setOnClickListener {
            prefManager.clearPreferences()
            requireActivity().onBackPressed()
        }

        binding.ivClientCodeCopy.setOnClickListener{
            if (clientCode.isNotEmpty()){
                copyToClipboardFromString(requireActivity(), clientCode, "Client Code Copied")
            }
        }

        binding.ivLoginIdCopy.setOnClickListener{
            if (loginId.isNotEmpty()){
                copyToClipboardFromString(requireActivity(), loginId, "Login ID Copied")
            }
        }
    }

    override fun initOnClick() {
        super.initOnClick()

        binding.tvDeleteAccount.setOnClickListener {
            findNavController().navigate(R.id.delete_account_fragment)
        }

    }

    override fun initAPI() {
        super.initAPI()

        userId = prefManager.userId
        sessionId = prefManager.sessionId
        accNo = prefManager.accno
        cifCode = prefManager.cifCode

        clientCode = accNo
        loginId = userId
        viewModel.getAccountInfo(userId, cifCode, sessionId)
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.clientInfoResult.observe(viewLifecycleOwner){response ->
            if (response?.accountGroupList != null) {
                if (response.accountGroupList.isNotEmpty()) {
                    val dataBank = response.accountGroupList[0]
                    binding.apply {
                        tvClientCode.text = accNo
                        tvLoginId.text = userId
                        tvSid.text = response.sid
                        tvSre.text = dataBank.accountinfoList[0].kseiAccno
                        tvSreTwo.text = dataBank.accountinfoList[0].sre04
                        tvEktpNumber.text = response.idNumber
                        tvKtpAddress.text = response.clintName

                        val address1 = response.address1
                        val address2 = response.address2
                        val address3 = response.address3
                        tvAddress.text = address1 + " " + address2 + " " + address3
                        tvEmailAddress.text = response.email.trim()
                        tvMobileNumber.text = response.phone2.trim()
                        tvNpwp.text = response.npwp.trim()
                        tvBankAccount.text =
                            dataBank.bankName + " - " + maskString(dataBank.bankAccno.trim())
                    }
                }
            }
        }
    }

    private fun maskString(input: String): String {
        var result = ""
        if (input.length > 4) {
            val lastChars = input.takeLast(4)
            val maskingChar = "*".repeat(input.length - 4)

            result = maskingChar + lastChars
        }
        return result
    }
}