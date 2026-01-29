package com.bcasekuritas.mybest.app.feature.dialog.notificationreport

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bcasekuritas.mybest.R

class DialogNotificationReportFragment : Fragment() {

    companion object {
        fun newInstance() = DialogNotificationReportFragment()
    }

    private lateinit var viewModel: DialogNotificationReportViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_notification_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(DialogNotificationReportViewModel::class.java)

    }

}