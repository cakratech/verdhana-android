package com.bcasekuritas.mybest.app.feature.selectaccount

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.domain.dto.response.source.AccountRes
import com.bcasekuritas.mybest.app.domain.dto.response.source.StockNotationRes
import com.bcasekuritas.mybest.app.domain.dto.response.source.StockParamRes
import com.bcasekuritas.mybest.app.domain.subscribers.Resource
import com.bcasekuritas.mybest.app.feature.activity.main.MainActivity
import com.bcasekuritas.mybest.app.feature.selectaccount.adapter.SelectAccountAdapter
import com.bcasekuritas.mybest.databinding.FragmentSelectAccountBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.constant.NavKeys
import com.bcasekuritas.mybest.ext.listener.OnClickAny
import com.bcasekuritas.rabbitmq.proto.bcas.StockParamInfo
import java.util.TreeMap

@FragmentScoped
@AndroidEntryPoint
class SelectAccountFragment : BaseFragment<FragmentSelectAccountBinding, SelectAccountViewModel>(),
    OnClickAny {

    @FragmentScoped
    override val bindingVariable: Int = BR.vmSelectAccount
    override val viewModel: SelectAccountViewModel by viewModels()
    override val binding: FragmentSelectAccountBinding by autoCleaned {
        (FragmentSelectAccountBinding.inflate(
            layoutInflater
        ))
    }
    private val selectAccountAdapter: SelectAccountAdapter by autoCleaned {
        SelectAccountAdapter(
            this
        )
    }

    private var listAccount = ArrayList<AccountRes>()
    private var accNo = ""
    private var cifCode = ""
    private var selected = ""

    override fun initAPI() {
        super.initAPI()

        val userId = prefManager.userId
        val cifCode = prefManager.cifCode
        val sessionId = prefManager.sessionId

        viewModel.getCashPos(userId, cifCode, sessionId)
        viewModel.getStockParamList(userId, sessionId)
    }

    override fun setupAdapter() {
        super.setupAdapter()
        binding.rcvAccount.setHasFixedSize(true)
        binding.rcvAccount.adapter = selectAccountAdapter

    }

    override fun setupObserver() {
        super.setupObserver()

        viewModel.getStockParamListResult.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                }

                is Resource.Success -> {
                    when (it.data?.status) {
                        0 -> {
                            it.data.stockParamInfoList?.map { res ->
                                val stockNotasi = StockParamInfo.newBuilder().setStockcode(res.stockcode).addAllStockNotasi(res.stockNotasiList)
                                val stockNotasiByte = stockNotasi.build().toByteArray()
                                viewModel.insertStockParamDao(StockParamRes(
                                    stockCode = res.stockcode,
                                    stockName = res.stockname,
                                    idxTrdBoard = res.idxTrdboard,
                                    stockNotasi = stockNotasiByte
                                ))

                                for (data in res.stockNotasiList){
                                    viewModel.insertStockNotationDao(
                                        StockNotationRes(
                                            stockCode = res.stockcode,
                                            notation = data.code,
                                            description = data.description
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                else -> {}
            }
        }

        viewModel.getCashPosResult.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    showLoading()
                }

                is Resource.Success -> {
                    when (it.data?.status) {
                        0 -> {
                            it.data.let { res ->
                                val clientName = res.cifInfo.clientName

                                res.groupCashPosList[0].accountInfoList.map { accMap ->
                                    val accountRes = AccountRes(
                                        accno = accMap.accno,
                                        accname = accMap.accname,
                                        accstatus = accMap.accstatus,
                                        prdType = accMap.prdType,
                                        cifCode = accMap.cifCode,
                                        kseiAccNo = accMap.kseiAccNo,
                                        clientName = clientName,
                                        userId = prefManager.userId
                                    )
                                    listAccount.add(accountRes)
//                                    viewModel.insertAccountDao(accountRes)
                                }

                                binding.tvName.text = clientName // setClientName

                            }

                            binding.rcvAccount.adapter?.let {
                                selectAccountAdapter.setData(listAccount)
                            }
                        }
                    }
                }

                else -> {}
            }
        }
    }

    override fun setupComponent() {
        super.setupComponent()

        binding.btnContinue.setOnClickListener {
            if (selected.isNotEmpty()){
                MainActivity.startIntentWithFinish(
                    requireActivity(),
                    NavKeys.KEY_MAIN_HOME
                )

                prefManager.cifCode = cifCode
                prefManager.accno = accNo
            }
        }
    }

    override fun onClickAny(valueAny: Any?) {
        valueAny as TreeMap<*, *>
        accNo = valueAny["accNo"].toString()
        cifCode = valueAny["cifCode"].toString()
        selected = valueAny["selected"].toString()
    }
}