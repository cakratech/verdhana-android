package com.bcasekuritas.mybest.app.feature.dialog.deleteaccount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bcasekuritas.mybest.R

class DialogDeleteAccountFragment : Fragment() {

    companion object {
        fun newInstance() = DialogDeleteAccountFragment()
    }

    private lateinit var viewModel: DialogDeleteAccountViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_delete_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(DialogDeleteAccountViewModel::class.java)

    }

}