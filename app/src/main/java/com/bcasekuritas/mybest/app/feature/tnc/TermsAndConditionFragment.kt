package com.bcasekuritas.mybest.app.feature.tnc

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bcasekuritas.mybest.R

class TermsAndConditionFragment : Fragment() {

    companion object {
        fun newInstance() = TermsAndConditionFragment()
    }

    private lateinit var viewModel: TermsAndConditionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_terms_and_condition, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(TermsAndConditionViewModel::class.java)
        // TODO: Use the ViewModel
    }

}