package com.bcasekuritas.mybest.app.feature.notificationsettings

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bcasekuritas.mybest.R

class NotificationSettingsFragment : Fragment() {

    companion object {
        fun newInstance() = NotificationSettingsFragment()
    }

    private lateinit var viewModel: NotificationSettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notification_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(NotificationSettingsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}