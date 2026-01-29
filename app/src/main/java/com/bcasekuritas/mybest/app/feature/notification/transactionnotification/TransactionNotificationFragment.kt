package com.bcasekuritas.mybest.app.feature.notification.transactionnotification

import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.feature.notification.transactionnotification.adapter.TransactionNotificationAdapter
import com.bcasekuritas.mybest.databinding.FragmentTransactionNotificationBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@AndroidEntryPoint
class TransactionNotificationFragment: BaseFragment<FragmentTransactionNotificationBinding, TransactionNotificationViewModel>() {

    override val bindingVariable: Int = BR.vmTransactionNotification
    override val viewModel: TransactionNotificationViewModel by viewModels()
    override val binding: FragmentTransactionNotificationBinding by autoCleaned { (FragmentTransactionNotificationBinding.inflate(layoutInflater)) }

    private val transactionHistoryAdapter: TransactionNotificationAdapter by autoCleaned { TransactionNotificationAdapter() }

    private var userId = ""
    private var sessionId = ""
    private var page = 1
    private var size = 20

    override fun setupAdapter() {
        super.setupAdapter()
        binding.rcvTransactionHistory.apply {
            adapter = transactionHistoryAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }

    override fun setupComponent() {
        super.setupComponent()
        binding.apply {
            swplTransactionNotif.setOnRefreshListener {
                page = 1
                viewModel.getNotificationHistory(userId, sessionId, page, size)
            }
        }
    }

    override fun initAPI() {
        super.initAPI()
        userId = prefManager.userId
        sessionId = prefManager.sessionId

        viewModel.getNotificationHistory(userId, sessionId, page, size)
    }

    override fun setupListener() {
        super.setupListener()
            binding.rcvTransactionHistory.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    (recyclerView.layoutManager as? LinearLayoutManager)?.let { layoutManager ->
                        val visibleItemCount = layoutManager.childCount
                        val totalItemCount = layoutManager.itemCount
                        val pastVisibleItems = layoutManager.findFirstVisibleItemPosition()

                        if (visibleItemCount + pastVisibleItems >= totalItemCount ) {

                            page += 1
                            viewModel.getNotificationHistory(userId, sessionId, page, size)
                        }
                    }

                    super.onScrolled(recyclerView, dx, dy)
                }
            })

    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.imqConnectionListener.connListenerLiveData.observe(this) { value ->
            when (value) {
                "Recovered" -> {
                    viewModel.imqConnectionListener.onListener("")
                    page = 1
                    viewModel.getNotificationHistory(userId, sessionId, page, size)
                }

                else -> {}
            }
        }

        viewModel.isNotificationEmpty.observe(viewLifecycleOwner) {isEmpty ->
            binding.lyEmptyNotification.visibility = if (isEmpty) View.VISIBLE else View.GONE

            binding.swplTransactionNotif.isRefreshing = false
        }

        viewModel.getNotificationHistoryResult.observe(viewLifecycleOwner) { listData ->
            if (listData.isNotEmpty()) {
                val sortData = listData.sortedByDescending { it.time }

                if (page > 1) {
                    transactionHistoryAdapter.addData(sortData)
                } else {
                    transactionHistoryAdapter.setData(sortData)
                }
            }
            binding.swplTransactionNotif.isRefreshing = false
        }
    }

}