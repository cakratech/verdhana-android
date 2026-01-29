package com.bcasekuritas.mybest.app.feature.notification.generalnotification

import androidx.fragment.app.viewModels
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.databinding.FragmentGeneralNotificationBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@AndroidEntryPoint
class GeneralNotificationFragment: BaseFragment<FragmentGeneralNotificationBinding, GeneralNotificationViewModel>() {

    override val viewModel: GeneralNotificationViewModel by viewModels()
    override val binding: FragmentGeneralNotificationBinding by autoCleaned { (FragmentGeneralNotificationBinding.inflate(layoutInflater)) }
    override val bindingVariable: Int = BR.vmGeneralNotification

}