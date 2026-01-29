package com.bcasekuritas.mybest.app.feature.security.changepassword

import android.text.Editable
import android.text.TextWatcher
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.data.entity.BiometricObject
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.databinding.FragmentChangePasswordBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.common.isValidPassword
import com.bcasekuritas.mybest.ext.delegate.ShowSnackBarImpl
import com.bcasekuritas.mybest.ext.delegate.ShowSnackBarInterface
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@AndroidEntryPoint
class ChangePasswordFragment :
    BaseFragment<FragmentChangePasswordBinding, ChangePasswordViewModel>(), ShowSnackBarInterface by ShowSnackBarImpl() {


    @FragmentScoped
    override val bindingVariable: Int = BR.vmChangePassword
    override val viewModel: ChangePasswordViewModel by viewModels()
    override val binding: FragmentChangePasswordBinding by autoCleaned {
        (FragmentChangePasswordBinding.inflate(layoutInflater))
    }

    override fun setupObserver() {
        super.setupObserver()

        viewModel.getChangePasswordResult.observe(viewLifecycleOwner) {
            when(it){
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    when(it.data?.status){
                        0 -> {
                            showSnackBarTop(
                                requireContext(),
                                binding.root,
                                "success",
                                R.drawable.ic_success,
                                "Password successfully changed",
                                "", requireActivity(), ""
                            )


                            prefManager.isBiometricActive = false

                            viewModel.insertToken(
                                BiometricObject(
                                    prefManager.userId,
                                    binding.edtReEnterPassword.getText()
                                )
                            )
                            onBackPressed()

                        }
                        else -> {
                            binding.tvErrorChangePassword.text = it.data?.remarks
                            binding.tvErrorChangePassword.isGone = false
                        }
                    }

                }
                is Resource.Failure -> {

                }

                else -> {}
            }
        }
    }

    override fun setupComponent() {
        super.setupComponent()

        binding.edtNewPassword.changeIconPasswordToggle()
        binding.edtCurrentPassword.changeIconPasswordToggle()
        binding.edtReEnterPassword.changeIconPasswordToggle()

        binding.lyToolbarChangePassword.tvLayoutToolbarMasterTitle.text = "Security"

        binding.lyToolbarChangePassword.ivLayoutToolbarMasterIconLeft.setOnClickListener {
            onBackPressed()
        }

        val currPass = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                binding.edtCurrentPassword.hideError()
                binding.tvErrorChangePassword.isGone = true

                checkEmptyField()
            }
        }

        binding.edtCurrentPassword.setTextWatcher(currPass)


        val edtNewPass = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                binding.edtNewPassword.hideError()
                binding.tvErrorChangePassword.isGone = true

                val newPass = binding.edtNewPassword.getText()
                val confirmPass = binding.edtReEnterPassword.getText()

                if (newPass.isEmpty()) {
                    // Handle empty new password
                } else {
                    if (!isValidPassword(s.toString())) {
                        binding.edtNewPassword.showError("Password must be at least 8 alphanumeric characters.")
                    }
                }
                if (confirmPass != "" && newPass != confirmPass) {
                    binding.edtReEnterPassword.showError("Password doesn't match")
                }
                checkEmptyField()
            }
        }

        binding.edtNewPassword.setTextWatcher(edtNewPass)


        val edtConfirmPass = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                binding.edtReEnterPassword.hideError()
                binding.tvErrorChangePassword.isGone = true

                val newPass = binding.edtNewPassword.getText()
                val confirmPass = binding.edtReEnterPassword.getText()

                if (confirmPass.isEmpty()) {
                    // Handle empty confirm password
                } else {
                    if (s.toString() != newPass) {
                        binding.edtReEnterPassword.showError("Password doesn't match")
                    }
                }
                checkEmptyField()
            }
        }

        binding.edtReEnterPassword.setTextWatcher(edtConfirmPass)
    }

    fun checkEmptyField() {
        val oldPass = binding.edtCurrentPassword.getText()
        val newPass = binding.edtNewPassword.getText()
        val confirmPass = binding.edtReEnterPassword.getText()

        binding.btnSaveChanges.isEnabled = if (oldPass.isNotEmpty() && newPass.isNotEmpty() && confirmPass.isNotEmpty()) {
            if (isValidPassword(newPass)) {
                newPass == confirmPass
            } else {
                false
            }
        } else {
            false
        }
    }

    override fun initOnClick() {
        super.initOnClick()

        binding.btnSaveChanges.setOnClickListener {
            viewModel.getChangePassword(
                prefManager.userId,
                binding.edtCurrentPassword.getText(),
                binding.edtNewPassword.getText(),
                binding.edtReEnterPassword.getText()
            )
        }
    }

}