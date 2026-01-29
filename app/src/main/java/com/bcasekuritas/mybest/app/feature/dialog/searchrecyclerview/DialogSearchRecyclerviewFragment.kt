package com.bcasekuritas.mybest.app.feature.dialog.searchrecyclerview

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bcasekuritas.mybest.R

class DialogSearchRecyclerviewFragment : Fragment() {

    companion object {
        fun newInstance() = DialogSearchRecyclerviewFragment()
    }

    private lateinit var viewModel: DialogSearchRecyclerviewViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_search_recyclerview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(DialogSearchRecyclerviewViewModel::class.java)

    }

}