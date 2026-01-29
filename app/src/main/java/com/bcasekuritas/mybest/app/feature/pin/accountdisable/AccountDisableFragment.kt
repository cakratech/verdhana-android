package com.bcasekuritas.mybest.app.feature.pin.accountdisable

import android.net.Uri
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.app.base.dialog.withvm.BaseDialogFullFragment
import com.bcasekuritas.mybest.databinding.FragmentAccountDisabledBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.constant.ConstKeys

@FragmentScoped
@AndroidEntryPoint
class AccountDisableFragment: BaseDialogFullFragment<FragmentAccountDisabledBinding, AccountDisableViewModel>() {

    override val bindingVariable: Int = BR.vmAccountDisabled
    override val viewModel: AccountDisableViewModel by viewModels()
    override val binding: FragmentAccountDisabledBinding by autoCleaned { (FragmentAccountDisabledBinding.inflate(layoutInflater)) }

    private val webView = CustomTabsIntent.Builder().build()

    override fun initOnClick() {
        super.initOnClick()
        binding.apply {
            btnClose.setOnClickListener {
                dismiss()
            }
            btnOpenAccount.setOnClickListener {
                val url = ConstKeys.FORGOT_PASS_URL
                if (url.isBlank() || !(url.startsWith("http://") || url.startsWith("https://"))) {
                    Toast.makeText(context, "Unavailable url", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                try {
                    webView.launchUrl(requireContext(), Uri.parse(url))
                } catch (ignore: Exception) {}
            }
        }
    }

}