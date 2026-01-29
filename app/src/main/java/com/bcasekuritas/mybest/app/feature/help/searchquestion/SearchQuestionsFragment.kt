package com.bcasekuritas.mybest.app.feature.help.searchquestion

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.domain.dto.response.FaqHelpData
import com.bcasekuritas.mybest.app.feature.help.adapter.HelpFaqAdapter
import com.bcasekuritas.mybest.databinding.FragmentSearchQuestionsBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
@AndroidEntryPoint
class SearchQuestionsFragment : BaseFragment<FragmentSearchQuestionsBinding, SearchQuestionsViewModel>() {

    override val bindingVariable: Int = BR.vmSearchQuestions
    override val viewModel: SearchQuestionsViewModel by viewModels()
    override val binding: FragmentSearchQuestionsBinding by autoCleaned { (FragmentSearchQuestionsBinding.inflate(layoutInflater)) }

    private val faqHelpAdapter: HelpFaqAdapter by autoCleaned { HelpFaqAdapter(requireContext()) }
    private var searchQuery = ""
    private var currentPage = 0

    val listTopFive = arrayListOf<FaqHelpData>()

    override fun setupAdapter() {
        super.setupAdapter()
        binding.rcvQuestionsSearch.apply {
            adapter = faqHelpAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }

    override fun setupComponent() {
        super.setupComponent()
        binding.apply {
//            etSearchHelp.requestFocus()
        }
    }

    override fun initOnClick() {
        super.initOnClick()
        binding.apply {
            ivBack.setOnClickListener {
                onBackPressed()
            }
            ivClearSearch.setOnClickListener{
                etSearchHelp.setText("")
            }
        }
    }

    override fun initAPI() {
        super.initAPI()
        val userId = prefManager.userId
        val sessionId = prefManager.sessionId
        viewModel.getFaq(userId, sessionId)
        viewModel.getTopFiveFaq(userId, sessionId)
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.getFaqResult.observe(viewLifecycleOwner) {listFaq ->
            if (!listFaq.isNullOrEmpty()) {
                faqHelpAdapter.setData(listFaq)
            } else {
                binding.lyNoDataSearch.visibility = View.VISIBLE
            }
        }
        viewModel.getSearchFaqUseCaseResult.observe(viewLifecycleOwner) {searching ->
            if (currentPage > 0){
                faqHelpAdapter.addData(searching?.faqDataList.orEmpty())
            } else {
                if (searchQuery.isNotEmpty()) {
                    binding.tvSumFoundTopic.isGone = searching?.searchHit == 0
                    binding.lyNoDataSearch.isGone = searching?.searchHit != 0
                    if (searching?.searchHit != 0){
                        binding.tvSumFoundTopic.text = "Found (${searching?.searchHit})"
                        faqHelpAdapter.setData(searching?.faqDataList.orEmpty())
                    } else {
                        binding.tvNotFoundData.text = "'${searchQuery}' is not found. Please chack again or find our top 5 asked questions:"
                        faqHelpAdapter.setData(listTopFive )
                    }
                } else {
                    binding.tvSumFoundTopic.isGone = true
                    binding.lyNoDataSearch.isGone = true

                    faqHelpAdapter.setData(searching?.faqDataList.orEmpty())
                }

            }
        }

        viewModel.getTopFiveFaqResult.observe(viewLifecycleOwner) {listItem ->
            if (!listItem.isNullOrEmpty()) {
                listTopFive.addAll(listItem)
            }
        }

    }

    override fun setupListener() {
        super.setupListener()
        binding.apply {

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
                    searchQuery = s.toString()
                    currentPage = 0
                    viewModel.getSearchFaq(prefManager.userId, prefManager.sessionId, searchQuery, currentPage)
                }
            }
            binding.etSearchHelp.addTextChangedListener(searchTextWatcher)

            binding.rcvQuestionsSearch.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dy > 0) {
                        (recyclerView.layoutManager as? LinearLayoutManager)?.let { layoutManager ->
                            val visibleItemCount = layoutManager.childCount
                            val totalItemCount = layoutManager.itemCount
                            val pastVisibleItems = layoutManager.findFirstVisibleItemPosition()

                            // Check if we are at the bottom
                            if (visibleItemCount + pastVisibleItems >= totalItemCount) {
                                // Perform the action when at the bottom
                                currentPage++
                                viewModel.getSearchFaq(prefManager.userId, prefManager.sessionId, searchQuery, currentPage )

                            }
                        }
                    }

                    super.onScrolled(recyclerView, dx, dy)
                }
            })
        }
    }

//    private fun searchFaq(query: String) {
//        if (query.isNotEmpty()) {
//            val data = listItem
//            val searchResult = if (query.isNotEmpty()) data.filter {item ->
//                item.questionName.contains(query, ignoreCase = true)
//            } else data
//
//            binding.tvSumFoundTopic.text = "Found (${searchResult.size})"
//            binding.tvSumFoundTopic.visibility = if (searchResult.isNotEmpty()) View.VISIBLE else View.GONE
//            binding.lyNoDataSearch.visibility = if (searchResult.isNotEmpty()) View.GONE else View.VISIBLE
//            binding.tvNotFoundData.text = "'${query}' is not found. Please chack again or find our top 5 asked questions:"
//
//            faqHelpAdapter.clearData()
//            faqHelpAdapter.setData(
//                searchResult.ifEmpty { listTopFive }
//            )
//        } else {
//            binding.tvSumFoundTopic.visibility = View.GONE
//            binding.lyNoDataSearch.visibility = View.GONE
//            faqHelpAdapter.setData(listItem)
//        }
//
//    }

}