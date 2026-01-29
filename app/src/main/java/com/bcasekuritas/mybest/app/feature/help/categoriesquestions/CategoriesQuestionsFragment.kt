package com.bcasekuritas.mybest.app.feature.help.categoriesquestions

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bcasekuritas.mybest.R

class CategoriesQuestionsFragment : Fragment() {

    companion object {
        fun newInstance() = CategoriesQuestionsFragment()
    }

    private lateinit var viewModel: CategoriesQuestionsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_categories_questions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(CategoriesQuestionsViewModel::class.java)
    }

}