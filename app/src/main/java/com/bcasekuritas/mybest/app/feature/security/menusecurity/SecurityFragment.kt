package com.bcasekuritas.mybest.app.feature.security.menusecurity

import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.data.layout.UIDialogModel
import com.bcasekuritas.mybest.app.feature.profile.profileaccount.AccountFragment
import com.bcasekuritas.mybest.databinding.FragmentSecurityBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.constant.ConstKeys
import com.bcasekuritas.mybest.ext.delegate.ShowDialog
import com.bcasekuritas.mybest.ext.delegate.ShowDialogImpl
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped


@FragmentScoped
@AndroidEntryPoint
class SecurityFragment : BaseFragment<FragmentSecurityBinding, SecurityViewModel>(), ShowDialog by ShowDialogImpl() {

    @FragmentScoped
    override val bindingVariable: Int = BR.vmSecurity
    override val viewModel: SecurityViewModel by viewModels()
    override val binding: FragmentSecurityBinding by autoCleaned {
        (FragmentSecurityBinding.inflate(
            layoutInflater
        ))
    }

    private val webView = CustomTabsIntent.Builder().build()

    companion object {
        fun newInstance() = AccountFragment()
    }

    override fun setupComponent() {
        super.setupComponent()

        binding.lyToolbarSecurity.tvLayoutToolbarMasterTitle.text = getString(R.string.text_security)
        binding.lyToolbarSecurity.ivLayoutToolbarMasterIconLeft.visibility = View.VISIBLE
        binding.lyToolbarSecurity.ivLayoutToolbarMasterIconLeft.setImageResource(R.drawable.ic_back)

    }

    override fun initOnClick() {
        super.initOnClick()

        binding.lyToolbarSecurity.ivLayoutToolbarMasterIconLeft.setOnClickListener {
            onBackPressed()
        }

        binding.constraintLayoutChangePassword.setOnClickListener {
            findNavController().navigate(R.id.change_password_fragment)
        }

        binding.constraintLayoutChangePin.setOnClickListener {
            showDialogChangePin(parentFragmentManager, onSuccess = {isSuccess ->
                if (isSuccess) {
                    showSnackBarTop(
                        requireContext(),
                        binding.root,
                        "success",
                        R.drawable.ic_success,
                        "Pin successfully changed",
                        "", requireActivity(), ""
                    )
                }
//                else {
//
//                    showSnackBarTop(
//                        requireContext(),
//                        binding.root,
//                        "error",
//                        R.drawable.ic_error,
//                        "Failed to change pin",
//                        "", requireActivity(), ""
//                    )
//                }
            })
        }

        binding.constraintLayoutChangeEmail.setOnClickListener {

            showDialogInfoBottomCallBack(
                parentFragmentManager,
                true,
                UIDialogModel(titleStr = "Change Email",
                    descriptionStr = "Change your email via the update data menu",
                    btnPositiveStr = "Open Account Management"),
                onOkClicked = {boolean ->
                    val url = ConstKeys.FORGOT_PASS_URL
                    if (url.isBlank() || !(url.startsWith("http://") || url.startsWith("https://"))) {
                        Toast.makeText(context, "Unavailable url", Toast.LENGTH_SHORT).show()
                        return@showDialogInfoBottomCallBack
                    }
                    try {
                        webView.launchUrl(requireContext(), Uri.parse(url))
                    } catch (ignore: Exception) {}
                }
            )
        }

        binding.constraintLayoutChangeMobileNumber.setOnClickListener {

            showDialogInfoBottomCallBack(
                parentFragmentManager,
                true,
                UIDialogModel(titleStr = "Change Mobile Number",
                    descriptionStr = "Change your phone number via the update data menu",
                    btnPositiveStr = "Open Account Management"),
                onOkClicked = {boolean ->
                    val url = ConstKeys.FORGOT_PASS_URL
                    if (url.isBlank() || !(url.startsWith("http://") || url.startsWith("https://"))) {
                        Toast.makeText(context, "Unavailable url", Toast.LENGTH_SHORT).show()
                        return@showDialogInfoBottomCallBack
                    }
                    try {
                        webView.launchUrl(requireContext(), Uri.parse(url))
                    } catch (ignore: Exception) {}
                }
            )
        }

        binding.constraintLayoutChangeBankAccount.setOnClickListener {

            showDialogInfoBottomCallBack(
                parentFragmentManager,
                true,
                UIDialogModel(titleStr = "Change Bank Account",
                    descriptionStr = "Please submit the signed form and contact call center for further guidance",
                    btnPositiveStr = "Open E-Form Request"),
                onOkClicked = {boolean ->
                    val url = ConstKeys.PDF_URL
                    if (url.isBlank() || !(url.startsWith("http://") || url.startsWith("https://"))) {
                        Toast.makeText(context, "Unavailable url file", Toast.LENGTH_SHORT).show()
                        return@showDialogInfoBottomCallBack
                    }
                    try {
                        webView.launchUrl(requireContext(), Uri.parse(url))
                    } catch (ignore: Exception) {}
                }
            )

        }
    }
}