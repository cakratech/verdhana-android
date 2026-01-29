package com.bcasekuritas.mybest.app.feature.e_ipo

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.domain.dto.response.IpoData
import com.bcasekuritas.mybest.app.domain.dto.response.StatusEipoList
import com.bcasekuritas.mybest.app.feature.activity.middle.MiddleActivity
import com.bcasekuritas.mybest.app.feature.e_ipo.adapter.EIPORcvAdapter
import com.bcasekuritas.mybest.databinding.FragmentEIPOBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.constant.NavKeys
import com.bcasekuritas.mybest.ext.listener.OnClickStr
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@AndroidEntryPoint
class EIPOFragment : BaseFragment<FragmentEIPOBinding, EIPOViewModel>(), OnClickStr {

    override val viewModel: EIPOViewModel by viewModels()
    override val binding: FragmentEIPOBinding by autoCleaned { (FragmentEIPOBinding.inflate(layoutInflater)) }
    override val bindingVariable: Int = BR.vmEIPO

    private val eipoAdapter: EIPORcvAdapter by autoCleaned { EIPORcvAdapter(prefManager.urlIcon, this) }

    val listItem = arrayListOf<IpoData>()

    private lateinit var linearLayoutManager: LinearLayoutManager
    private var userId = ""
    private var sessionId = ""

    private var isPageLoading = false
    private var page = 1
    private var size = 10

    override fun setupAdapter() {
        super.setupAdapter()
        linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rcvEipo.apply {
            adapter = eipoAdapter
            layoutManager = linearLayoutManager
        }
    }

    override fun setupComponent() {
        super.setupComponent()
        binding.apply {
            lyToolbarEIPO.tvLayoutToolbarMasterTitle.text = "E-IPO"
            lyToolbarEIPO.ivLayoutToolbarMasterIconRightOne.visibility = View.VISIBLE
            lyToolbarEIPO.ivLayoutToolbarMasterIconRightOne.setImageResource(R.drawable.ic_file)
//            searchBar.setHint("Search code or name")
        }
    }

    override fun initOnClick() {
        super.initOnClick()
        binding.apply {
            lyToolbarEIPO.ivLayoutToolbarMasterIconLeft.setOnClickListener {
                onBackPressed()
            }
            lyToolbarEIPO.ivLayoutToolbarMasterIconRightOne.setOnClickListener {
                MiddleActivity.startIntent(requireActivity(), NavKeys.KEY_FM_EIPO_ORDER_LIST)
            }

            btnBackHome.setOnClickListener {
                findNavController().navigate(R.id.home_fragment)
            }
        }
    }

    override fun setupListener() {
        super.setupListener()
        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int,
            ) {
                // Do something before text changes
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Do something when text changes
            }

            override fun afterTextChanged(s: Editable?) {
                searchStock(s.toString())

            }
        }

//        binding.searchBar.setTextWatcher(searchTextWatcher)

//        binding.rcvEipo.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                val visibleItemCount = linearLayoutManager.childCount
//                val pastVisibleItem = linearLayoutManager.findFirstVisibleItemPosition()
//                val total  = eipoAdapter.itemCount
//
//                if (!isPageLoading) {
//                    if ((visibleItemCount + pastVisibleItem) >= total) {
//                        page += 1
//                        viewModel.getIpoList(userId, sessionId, page, size)
//                        isPageLoading = true
//                    }
//                }
//
//
//                super.onScrolled(recyclerView, dx, dy)
//            }
//        })
    }

    private fun searchStock(query: String) {
        if (query.isNotEmpty()) {
            val searchResult = listItem.filter {item ->
                item.code.contains(query, ignoreCase = true) || item.companyName.contains(query, ignoreCase = true)
            }

            binding.lyNoData.visibility = if (searchResult.isNotEmpty()) View.GONE else View.VISIBLE
            binding.tvNoItemFoundTitle.text = "'${query}' is not found"
            binding.tvNoItemFoundDesc.text = "Please check your entered keyword"

            eipoAdapter.clearData()
            eipoAdapter.setData(searchResult)
        } else {
            binding.lyNoData.visibility = View.GONE
            eipoAdapter.setData(listItem)
        }

    }

    override fun onResume() {
        super.onResume()
        userId = prefManager.userId
        sessionId = prefManager.sessionId

        viewModel.getIpoList(userId, sessionId, page, size)
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.getIpoListResult.observe(viewLifecycleOwner) {listData ->
            if (!listData.isNullOrEmpty()) {
                val sortByName = listData.sortedBy { it.code }
                val data = sortedData(sortByName)
                listItem.clear()
                listItem.addAll(data)
                eipoAdapter.setData(data)

                binding.lyNoData.visibility = View.GONE
                binding.rcvEipo.visibility = View.VISIBLE
            } else {
                binding.lyNoData.visibility = View.VISIBLE
                binding.rcvEipo.visibility = View.GONE
            }
        }
    }

    private fun sortedData(data: List<IpoData>): List<IpoData> {
        return data.sortedWith { order1, order2 ->
            // Special case for order vs ongoing (regardless of dates)
            if ((order1.statusNew == StatusEipoList.BOOK_BUILDING && order2.statusNew == StatusEipoList.OFFERING) ||
                (order1.statusNew == StatusEipoList.OFFERING && order2.statusNew == StatusEipoList.BOOK_BUILDING)) {

                // If same date, order comes first
                if (order1.statusDate() == order2.statusDate()) {
                    if (order1.statusNew == StatusEipoList.OFFERING) -1 else 1
                }
                // Otherwise compare by date
                else {
                    order1.statusDate().compareTo(order2.statusDate())
                }
            }
            // Normal case: first sort by status priority
            else if (order1.statusNew.priority != order2.statusNew.priority) {
                order1.statusNew.priority.compareTo(order2.statusNew.priority)
            }
            // Then sort by status-specific date
            else {
                val date1 = order1.statusDate()
                val date2 = order2.statusDate()

                if (date1 == date2) {
                    // For same dates, maintain priority order
                    order1.statusNew.priority.compareTo(order2.statusNew.priority)
                } else {
                    date1.compareTo(date2)
                }
            }
        }
    }

    override fun onClickStr(value: String?) {
        if (value != null) {
            MiddleActivity.startIntentParam(
                requireActivity(),
                NavKeys.KEY_FM_EIPO_DETAIL,
                value,
                ""
            )
        }
    }

}