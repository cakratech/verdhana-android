package com.bcasekuritas.mybest.app.feature.e_ipo.orderlist

import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.feature.e_ipo.orderlist.adapter.EipoOrderListAdapter
import com.bcasekuritas.mybest.databinding.FragmentEipoOrderListBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@AndroidEntryPoint
class EipoOrderListFragment: BaseFragment<FragmentEipoOrderListBinding, EipoOrderListViewModel>() {

    override val viewModel: EipoOrderListViewModel by viewModels()
    override val binding: FragmentEipoOrderListBinding by autoCleaned { (FragmentEipoOrderListBinding.inflate(layoutInflater)) }
    override val bindingVariable: Int = BR.vmOrderListEipo

    private val mAdapter: EipoOrderListAdapter by autoCleaned { EipoOrderListAdapter() }
    private lateinit var linearLayoutManager: LinearLayoutManager

    private var sizeItem = 15
    private var page = 1
    private var isLastPage = false
    private var isPageLoading = false

    override fun setupAdapter() {
        super.setupAdapter()
        linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rcvOrderList.apply {
            adapter = mAdapter
            layoutManager = linearLayoutManager
        }
    }

    override fun setupComponent() {
        super.setupComponent()
        binding.apply {
            lyToolbar.tvLayoutToolbarMasterTitle.text = "E-IPO Order List"
        }
    }

    override fun initOnClick() {
        super.initOnClick()
        binding.apply {
            lyToolbar.ivLayoutToolbarMasterIconLeft.setOnClickListener {
                onBackPressed()
            }
        }
    }

    override fun setupListener() {
        super.setupListener()
//        binding.rcvOrderList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                val scrollOffset = recyclerView.computeVerticalScrollOffset()
//                val scrollExtent = recyclerView.computeVerticalScrollExtent()
//                val scrollRange = recyclerView.computeVerticalScrollRange()
//                Timber.d("SCROLLSCROLL")
//
//                if (!isPageLoading) {
//                    if (scrollOffset + scrollExtent >= scrollRange && !isLastPage) {
//                        page += 1
//                        viewModel.getEipoOrderList(prefManager.userId, prefManager.sessionId, prefManager.accno, "*", page, sizeItem)
//                        isPageLoading = true
//                    }
//                }
//
//
//                super.onScrolled(recyclerView, dx, dy)
//            }
//        })
    }

    override fun initAPI() {
        super.initAPI()
        page = 1
        viewModel.getEipoOrderList(prefManager.userId, prefManager.sessionId, prefManager.accno,"*", page, sizeItem)
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.getEipoOrderListResult.observe(viewLifecycleOwner) {orderList ->
            if (orderList.isNotEmpty()) {
                mAdapter.setData(orderList)
            }
            isPageLoading = false
        }
    }

}