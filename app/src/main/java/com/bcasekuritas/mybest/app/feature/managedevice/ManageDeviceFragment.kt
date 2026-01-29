package com.bcasekuritas.mybest.app.feature.managedevice

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.domain.dto.response.OrderSuccessSnackBar
import com.bcasekuritas.mybest.app.domain.dto.response.TrustedDeviceItem
import com.bcasekuritas.mybest.app.feature.activity.main.MainActivity
import com.bcasekuritas.mybest.app.feature.managedevice.adapter.ManageDeviceAdapter
import com.bcasekuritas.mybest.databinding.FragmentManageDeviceBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.constant.Args
import com.bcasekuritas.mybest.ext.constant.NavKeys
import com.bcasekuritas.mybest.ext.converter.GET_STATUS_BUY_SELL
import com.bcasekuritas.mybest.ext.delegate.ShowDialog
import com.bcasekuritas.mybest.ext.delegate.ShowDialogImpl
import com.bcasekuritas.mybest.ext.listener.OnClickInt
import com.bcasekuritas.mybest.ext.listener.OnClickStr
import com.bcasekuritas.rabbitmq.proto.bcas.TrustedDevice
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@AndroidEntryPoint
class ManageDeviceFragment: BaseFragment<FragmentManageDeviceBinding, ManageDeviceViewModel>(), OnClickStr, ShowDialog by ShowDialogImpl() {

    override val viewModel: ManageDeviceViewModel by viewModels()
    override val binding: FragmentManageDeviceBinding by autoCleaned { (FragmentManageDeviceBinding.inflate(layoutInflater)) }
    override val bindingVariable: Int = BR.vmManageDevice

    private val listDeviceAdapter: ManageDeviceAdapter by autoCleaned { ManageDeviceAdapter(this) }

    private var listDevice = arrayListOf<TrustedDeviceItem>()

    private var userId = ""
    private var sessionId = ""
    private var deviceIdForDelete = ""


    override fun setupAdapter() {
        super.setupAdapter()
        binding.rcvDevice.apply {
            adapter = listDeviceAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }

    override fun setupComponent() {
        super.setupComponent()
        binding.apply {
            lyToolbar.tvLayoutToolbarMasterTitle.text = "Managed Device"

        }
    }

    override fun initAPI() {
        super.initAPI()
        userId = prefManager.userId
        sessionId = prefManager.sessionId

        viewModel.getTrustedDevice(userId, sessionId)
    }

    override fun initOnClick() {
        super.initOnClick()
        binding.apply {
            lyToolbar.ivLayoutToolbarMasterIconLeft.setOnClickListener {
                onBackPressed()
            }
        }
    }

    override fun setupListener() {
        super.setupListener()
        parentFragmentManager.setFragmentResultListener(NavKeys.KEY_FM_MANAGE_DEVICE, viewLifecycleOwner) { _, result ->
            // callback dialog remove device
            val confirmResultWithdraw = result.getString(NavKeys.CONST_RES_MANAGED_DEVICE)
            if (confirmResultWithdraw == "RESULT_OK") {
                if (result.getBoolean("confirm")) {
                    viewModel.deleteDevice(userId, sessionId, deviceIdForDelete)
                }
            }
        }
    }

    // on device remove clicked
    override fun onClickStr(value: String?) {
        if (!value.isNullOrEmpty()) {
            deviceIdForDelete = value
            showDialogRemoveTrustedDevice(NavKeys.KEY_FM_MANAGE_DEVICE, parentFragmentManager)
        }
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.getTrustedDeviceResult.observe(viewLifecycleOwner) {res ->
            if (res != null) {
                if (res.isNotEmpty()) {
                    listDevice.clear()
                    listDevice.addAll(res)
                    listDeviceAdapter.setData(listDevice)
                }
            }
        }

        viewModel.deleteDeviceResult.observe(viewLifecycleOwner) {res ->
            if (res != null) {
                if (res.status == 0) {
                    listDevice.removeIf {item -> item.deviceId == deviceIdForDelete }
                    listDeviceAdapter.setData(listDevice)
                }
            }
        }
    }
}