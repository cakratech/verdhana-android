package com.bcasekuritas.mybest.app.feature.profile.estaments

import android.net.Uri
import android.os.Build
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseFragment
import com.bcasekuritas.mybest.app.data.layout.UIDialogModel
import com.bcasekuritas.mybest.databinding.FragmentEStatementsBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.common.getCurrentYearMonthDay
import com.bcasekuritas.mybest.ext.constant.ConstKeys
import com.bcasekuritas.mybest.ext.converter.GET_ESTATEMENT_TYPE
import com.bcasekuritas.mybest.ext.delegate.ShowDropDown
import com.bcasekuritas.mybest.ext.delegate.ShowDropDownImpl
import com.bcasekuritas.mybest.ext.other.getMonthName
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request

@FragmentScoped
@AndroidEntryPoint
class EStatementsFragment : BaseFragment<FragmentEStatementsBinding, EStatementsViewModel>(),
    ShowDropDown by ShowDropDownImpl() {

    @FragmentScoped
    override val bindingVariable: Int = BR.vmEStatements
    override val viewModel: EStatementsViewModel by viewModels()
    override val binding: FragmentEStatementsBinding by autoCleaned {
        (FragmentEStatementsBinding.inflate(
            layoutInflater
        ))
    }

    private val webView = CustomTabsIntent.Builder().build()
    private var currYear = 0
    private var currMonth = 0

    private var selectedYear = 0

    private val listData = listOf("Statement of Account", "Client Sell Activity", "Client Buy Activity", "Cash Dividend", "Bond Coupon", "Transaction Report")

    private var urlEStatement = ""
    private var selectedType = "SOA"

    companion object {
        fun newInstance() = EStatementsFragment()
    }

    override fun setupComponent() {
        binding.lyToolbarEStatements.tvLayoutToolbarMasterTitle.text =
            getString(R.string.text_e_statements)
        binding.lyToolbarEStatements.ivLayoutToolbarMasterIconLeft.visibility = View.VISIBLE

        currYear = getCurrentYearMonthDay("year").toInt()
        selectedYear = currYear
        currMonth = getCurrentYearMonthDay("month").toInt()

        binding.tvEStatementsYear.text = currYear.toString()
        binding.tvEStatementsMonth.text = currMonth.getMonthName()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun initOnClick() {
        super.initOnClick()


        binding.lyToolbarEStatements.ivLayoutToolbarMasterIconLeft.setOnClickListener {
            requireActivity().finish()
        }

        binding.llReportType.setOnClickListener {
            showSimpleDropDown(requireContext(), listData, binding.llReportType){ index, value ->
                binding.tvEStatementsReportType.text = value
                binding.llReportMonth.isGone = index != 0
                binding.tvEStatementsTitleMonth.isGone = index != 0
                selectedType = GET_ESTATEMENT_TYPE(value)
            }
        }

        binding.llReportYear.setOnClickListener {
            showDialogNumberPicker(
                parentFragmentManager,
                "Select Year",
                currYear-2,
                currYear,
                selectedYear
            ){ value ->
                selectedYear = value
                val pickedYear = if(value != 0) value else getCurrentYearMonthDay("year").toInt()
                binding.tvEStatementsYear.text = "$pickedYear"
            }
        }


        binding.llReportMonth.setOnClickListener {
            showDialogMonthPicker(
                parentFragmentManager,
                "Select Month",
                currMonth
            ){ monthNumber ->
                currMonth = monthNumber
                binding.tvEStatementsMonth.text = monthNumber.getMonthName()
            }
        }

        binding.btnDownloadReport.setOnClickListener {
            val client = OkHttpClient()
            val month = if (currMonth.toString().length == 1) "0$currMonth" else "$currMonth"
            urlEStatement = "${ConstKeys.PROMO_BANNER_URL}/api/assets/get-pdf/e-statement/${prefManager.accno}/$selectedType/$selectedYear/$month"

            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val request = Request.Builder().url(urlEStatement).build()
                    val response = client.newCall(request).execute()

                    if (response.body?.contentType().toString() == "application/pdf") {
                        launch(Dispatchers.Main) {
                            showDialogConfirmationCenterCallBack(
                                parentFragmentManager,
                                UIDialogModel(titleStr = "Do you want to download Report?", btnPositiveStr = "OK", btnNegativeStr = "Cancel"),
                                true,
                                onOkClicked = {
                                    if (urlEStatement.isBlank() || !(urlEStatement.startsWith("http://") || urlEStatement.startsWith("https://"))) {
                                        Toast.makeText(context, "Unavailable url file", Toast.LENGTH_SHORT).show()
                                        return@showDialogConfirmationCenterCallBack
                                    }
                                    try {
                                        webView.launchUrl(requireContext(), Uri.parse(urlEStatement))
                                    } catch (ignore: Exception) {}
                                })
                        }
                    } else {
                        launch(Dispatchers.Main) {
                            showDialogConfirmationCenterCallBack(
                                parentFragmentManager,
                                UIDialogModel(titleStr = "Report does not exist", btnPositiveStr = "Confirm", btnNegativeStr = "Cancel"),
                                false,
                                onOkClicked = { })
                        }
                    }
                } catch (e: Exception) {
                    launch(Dispatchers.Main) {
                        showDialogConfirmationCenterCallBack(
                            parentFragmentManager,
                            UIDialogModel(titleStr = "Report does not exist", btnPositiveStr = "Confirm", btnNegativeStr = "Cancel"),
                            false,
                            onOkClicked = { })
                    }
                }
            }
        }
    }

}