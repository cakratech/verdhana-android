package com.bcasekuritas.mybest.app.feature.linesetting

import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.feature.activity.fullscreen.FullScreenActivity
import com.bcasekuritas.mybest.app.feature.activity.middle.MiddleActivity
import com.bcasekuritas.mybest.app.feature.activity.splash.SplashActivity
import com.bcasekuritas.mybest.databinding.FragmentLineSettingBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.constant.NavKeys
import com.bcasekuritas.mybest.ext.view.setSafeOnClickListener

@FragmentScoped
@AndroidEntryPoint
class LineSettingFragment: BaseFragment<FragmentLineSettingBinding, LineSettingViewModel>() {

    @FragmentScoped
    override val bindingVariable: Int = BR.vmLineSetting
    override val viewModel: LineSettingViewModel by viewModels()
    override val binding: FragmentLineSettingBinding by autoCleaned { (FragmentLineSettingBinding.inflate(layoutInflater)) }

    private var checkedLine = 0

    override fun setupComponent() {
        super.setupComponent()
        binding.apply {
            lyToolbar.tvLayoutToolbarMasterTitle.text = "Line Settings"
            val conLine = prefManager.connectionLine
            when (conLine) {
                2 -> {
                    radioCustom.isChecked = true
                }
                1 -> {
                    radioDrc.isChecked = true
                }
                else -> {
                    radioPrimary.isChecked = true
                }
            }

            lyToolbar.ivLayoutToolbarMasterIconLeft.setOnClickListener {
                onBackPressed()
            }

            radiogroup.setOnCheckedChangeListener { radioGroup, checkId ->
                when (checkId) {
                    radioPrimary.id -> {
                        checkedLine = 0
                    }
                    radioDrc.id -> {
                        checkedLine = 1
                    }
                    radioCustom.id -> {
                        checkedLine = 2
                    }
                }

                edtTxtHts.isEnabled = radioCustom.isChecked
                edtTxtPort.isEnabled = radioCustom.isChecked
            }

            btnLineSettingSave.setSafeOnClickListener {
                prefManager.connectionLine = checkedLine
                when (checkedLine) {
                    2 -> {
                        prefManager.connectionHts = edtTxtHts.editText?.text.toString()
                        prefManager.connectionPort = edtTxtHts.editText?.text.toString().toInt()
                    }
                    1 -> {
                        prefManager.connectionHts = ""
                        prefManager.connectionPort = 0
                    }
                    else -> {
                        prefManager.connectionHts = ""
                        prefManager.connectionPort = 0
                    }
                }
                showLoading()
                Handler(Looper.getMainLooper()).postDelayed({
                    restartApp()
                }, 1200)
            }
        }
    }

    private fun restartApp() {
        requireActivity().finish()
        val intent = Intent(activity, SplashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        requireActivity().startActivity(intent)
        if (activity != null) {
            requireActivity().finish()
        }
        Runtime.getRuntime().exit(0)
    }
}