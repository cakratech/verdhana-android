package com.bcasekuritas.mybest.app.feature.privacypolicy

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bcasekuritas.mybest.R

class PrivacyPolicyFragment : Fragment() {

    companion object {
        fun newInstance() = PrivacyPolicyFragment()
    }

    private lateinit var viewModel: PrivacyPolicyViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_privacy_policy, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(PrivacyPolicyViewModel::class.java)
        // TODO: Use the ViewModel
    }

}