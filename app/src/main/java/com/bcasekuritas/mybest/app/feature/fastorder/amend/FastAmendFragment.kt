package com.bcasekuritas.mybest.app.feature.fastorder.amend

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bcasekuritas.mybest.R

class FastAmendFragment : Fragment() {

    companion object {
        fun newInstance() = FastAmendFragment()
    }

    private lateinit var viewModel: FastAmendViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_fast_amend, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(FastAmendViewModel::class.java)
        // TODO: Use the ViewModel
    }

}