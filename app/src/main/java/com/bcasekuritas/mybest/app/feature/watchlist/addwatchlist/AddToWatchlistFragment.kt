package com.bcasekuritas.mybest.app.feature.watchlist.addwatchlist

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.feature.searchstock.SearchStockFragment
import com.bcasekuritas.mybest.app.feature.searchstock.SearchStockSharedViewmodel
import com.bcasekuritas.mybest.databinding.FragmentAddToWatchlistBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.constant.Args

@FragmentScoped
@AndroidEntryPoint
class AddToWatchlistFragment :
    BaseFragment<FragmentAddToWatchlistBinding, AddToWatchlistViewModel>() {

    @FragmentScoped
    override val bindingVariable: Int = BR.vmAddToWatchlist
    override val viewModel: AddToWatchlistViewModel by viewModels()
    override val binding: FragmentAddToWatchlistBinding by autoCleaned {
        (FragmentAddToWatchlistBinding.inflate(
            layoutInflater
        ))
    }

    private lateinit var sharedViewModel: SearchStockSharedViewmodel
    private var checkedStockList = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedViewModel =
            ViewModelProvider(requireActivity()).get(SearchStockSharedViewmodel::class.java)

        sharedViewModel.setCheckedStockParam(arrayListOf())
    }

    override fun setupArguments() {
        super.setupArguments()
    }

    override fun onResume() {
        super.onResume()
        if (checkedStockList.isNotEmpty()){
            binding.lyBtnBottom.tvStockSelected.text = "${checkedStockList.size} Stocks Selected"
        }
    }

    override fun setupObserver() {
        super.setupObserver()

        sharedViewModel.getCheckedStockParam.observe(viewLifecycleOwner) {
            checkedStockList = arrayListOf()
            if (it.isNotEmpty()) {
                binding.lyBtnBottom.tvStockSelected.text = "${it.size} Stocks Selected"
                binding.lyBtnBottom.btnOk.isEnabled = true
                it.map { list -> checkedStockList.add(list)
                }
            } else {
                binding.lyBtnBottom.tvStockSelected.text = "${it.size} Stocks Selected"
                binding.lyBtnBottom.btnOk.isEnabled = false
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = Bundle().apply {
            putBoolean(Args.EXTRA_PARAM_BOOLEAN_TWO, true)
        }

        val searchFragment = SearchStockFragment()
        searchFragment.arguments = bundle
        childFragmentManager.beginTransaction()
            .replace(R.id.fl_container_watchlist, searchFragment)
            .commit()
    }

    override fun setupComponent() {
        super.setupComponent()
        binding.apply {

            lyToolbar.tvLayoutToolbarMasterTitle.text = "Add To Watchlist"
            lyToolbar.ivLayoutToolbarMasterIconLeft.setOnClickListener {
                findNavController().popBackStack()
            }

            lyBtnBottom.btnOk.setOnClickListener {

                if (checkedStockList.size != 0) {

                    val bundle = Bundle().apply {
                        putStringArrayList(Args.EXTRA_PARAM_OBJECT, checkedStockList)
                        putString(Args.EXTRA_PARAM_STR_ONE, "watchlist")
                    }

                    findNavController().navigate(R.id.select_watchlist_fragment, bundle)

                }
            }
        }
    }
}