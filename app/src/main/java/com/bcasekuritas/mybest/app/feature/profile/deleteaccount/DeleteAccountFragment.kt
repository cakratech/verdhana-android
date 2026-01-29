package com.bcasekuritas.mybest.app.feature.profile.deleteaccount

import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.databinding.FragmentDeleteAccountBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@AndroidEntryPoint
class DeleteAccountFragment : BaseFragment<FragmentDeleteAccountBinding, DeleteAccountViewModel>() {

    @FragmentScoped
    override val bindingVariable: Int = BR.vmAccount
    override val viewModel: DeleteAccountViewModel by viewModels()
    override val binding: FragmentDeleteAccountBinding by autoCleaned {
        (FragmentDeleteAccountBinding.inflate(
            layoutInflater
        ))
    }

    override fun initOnClick() {
        super.initOnClick()

        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.btnContactCustomerService.setOnClickListener {
            // Create an email intent
            val emailIntent = Intent(Intent.ACTION_SEND).apply {
                type = "message/rfc822"
                putExtra(Intent.EXTRA_EMAIL, arrayOf("halo@bcasekuritas.co.id"))
            }

            // Check if an email client is available
            if (emailIntent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(Intent.createChooser(emailIntent, "Choose an email client"))
            } else {
                Toast.makeText(requireContext(), "No email app found", Toast.LENGTH_SHORT).show()
            }
        }
    }

}