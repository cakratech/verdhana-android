package com.bcasekuritas.mybest.app.feature.index

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.domain.dto.response.IndexSectorDetailData
import com.bcasekuritas.mybest.app.feature.index.adapter.IndexAdapter
import com.bcasekuritas.mybest.databinding.FragmentIndexBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.common.showToast
import com.bcasekuritas.mybest.ext.constant.Args
import com.bcasekuritas.mybest.ext.listener.OnClickStrInt


@FragmentScoped
@AndroidEntryPoint
class IndexFragment : BaseFragment<FragmentIndexBinding, IndexViewModel>(), OnClickStrInt {

    @FragmentScoped
    override val bindingVariable: Int = BR.vmIndex
    override val viewModel: IndexViewModel by viewModels()
    override val binding: FragmentIndexBinding by autoCleaned{ (FragmentIndexBinding.inflate(layoutInflater)) }

    private val mAdapter: IndexAdapter by autoCleaned { IndexAdapter(requireContext(), this) }

    private var userId = ""
    private var sessionId = ""

    private var listItem = arrayListOf<IndexSectorDetailData>()

    override fun setupComponent() {
        super.setupComponent()
        binding.apply {
            searchbar.setHint("Search index code")
            toolbar.tvLayoutToolbarMasterTitle.text = "Index"
        }
    }

    override fun setupAdapter() {
        super.setupAdapter()
        binding.rcvStocks.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }

    override fun setupListener() {
        super.setupListener()
        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                // Do something before text changes
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Do something when text changes
            }

            override fun afterTextChanged(s: Editable?) {
                filterItems(binding.searchbar.getText())
            }
        }

        binding.searchbar.setTextWatcher(searchTextWatcher)
    }

    override fun initOnClick() {
        super.initOnClick()
        binding.apply {
            toolbar.ivLayoutToolbarMasterIconLeft.setOnClickListener {
                onBackPressed()
            }
        }
    }

    override fun initAPI() {
        super.initAPI()
        userId = prefManager.userId
        sessionId = prefManager.sessionId

        viewModel.getIndexData(userId, sessionId)
    }

    override fun onResume() {
        super.onResume()
        viewModel.setListenerIndice()
    }

    override fun onPause() {
        super.onPause()
        viewModel.unSubscribeIndiceData()
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.imqConnectionListener.connListenerLiveData.observe(this) { value ->
            when (value) {
                "Recovered" -> {
                    viewModel.imqConnectionListener.onListener("")
                    viewModel.getIndexData(userId, sessionId)
                }

                else -> {}
            }
        }

        viewModel.getIndexDetailDataResult.observe(viewLifecycleOwner) {
            if (it != null) {
                listItem.clear()
                it.map {
                    if (it != null) {
                        listItem.add(it)
                    }
                }
                val sortedList = it.sortedBy { it?.indiceCode }
                mAdapter.setData(sortedList.filterNotNull())
            }
        }

    }

    private fun filterItems(query: String) {
        val filteredItems = listItem.filter { item ->
            item.indiceCode.contains(query, ignoreCase = true)
        }
        viewModel.setQuerySearch(query)
        mAdapter.setData(filteredItems)
    }

    override fun onClickStrInt(valueStr: String?, valueInt: Int?) {
        if (!valueStr.equals("") && valueInt != 0) {
            val bundle = Bundle().apply {
                putString(Args.EXTRA_PARAM_STR_ONE, valueStr)
                putInt(Args.EXTRA_PARAM_INT_ONE, valueInt!!)
            }
            findNavController().navigate(R.id.index_detail_fragment, bundle)
        }
    }
}