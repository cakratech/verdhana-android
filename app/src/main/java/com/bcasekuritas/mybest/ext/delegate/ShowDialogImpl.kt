package com.bcasekuritas.mybest.ext.delegate

import androidx.fragment.app.FragmentManager
import com.bcasekuritas.mybest.app.data.layout.UIDialogExerciseOrderModel
import com.bcasekuritas.mybest.app.data.layout.UIDialogModel
import com.bcasekuritas.mybest.app.data.layout.UIDialogOrderConfirmationModel
import com.bcasekuritas.mybest.app.data.layout.UIDialogPortfolioHistoryModel
import com.bcasekuritas.mybest.app.data.layout.UIDialogPortfolioOrderModel
import com.bcasekuritas.mybest.app.data.layout.UIDialogPortfolioRealizedModel
import com.bcasekuritas.mybest.app.data.layout.UIDialogRunningTradeModel
import com.bcasekuritas.mybest.app.data.layout.UIDialogSellModel
import com.bcasekuritas.mybest.app.data.layout.UIDialogWithdrawModel
import com.bcasekuritas.mybest.app.domain.dto.request.AmendFastOrderReq
import com.bcasekuritas.mybest.app.domain.dto.request.CancelFastOrderReq
import com.bcasekuritas.mybest.app.domain.dto.request.SendOrderReq
import com.bcasekuritas.mybest.app.domain.dto.request.WatchListCategory
import com.bcasekuritas.mybest.app.domain.dto.response.QtyPriceItem
import com.bcasekuritas.mybest.app.domain.dto.response.source.SelectionCheckRes
import com.bcasekuritas.mybest.app.feature.dialog.bottom.DialogAllocationEipoBottom
import com.bcasekuritas.mybest.app.feature.dialog.bottom.DialogAmendGtcOrder
import com.bcasekuritas.mybest.app.feature.dialog.bottom.DialogCreateRenameCategory
import com.bcasekuritas.mybest.app.feature.dialog.bottom.DialogEditWatchlist
import com.bcasekuritas.mybest.app.feature.dialog.bottom.DialogExerciseExpiredBottom
import com.bcasekuritas.mybest.app.feature.dialog.bottom.DialogExerciseNotAvailableBottom
import com.bcasekuritas.mybest.app.feature.dialog.bottom.DialogFilterPortfolioHistory
import com.bcasekuritas.mybest.app.feature.dialog.bottom.DialogFilterPortfolioOrder
import com.bcasekuritas.mybest.app.feature.dialog.bottom.DialogFilterPortfolioRealized
import com.bcasekuritas.mybest.app.feature.dialog.bottom.DialogInfoBottom
import com.bcasekuritas.mybest.app.feature.dialog.bottom.DialogInfoFibonacci
import com.bcasekuritas.mybest.app.feature.dialog.bottom.DialogInfoManyDevicesOtp
import com.bcasekuritas.mybest.app.feature.dialog.bottom.DialogInfoManyRequestOtp
import com.bcasekuritas.mybest.app.feature.dialog.bottom.DialogRemovePriceAlert
import com.bcasekuritas.mybest.app.feature.dialog.bottom.DialogRemoveTrustedDevice
import com.bcasekuritas.mybest.app.feature.dialog.bottom.DialogSelectCategory
import com.bcasekuritas.mybest.app.feature.dialog.bottom.DialogSelectionBottom
import com.bcasekuritas.mybest.app.feature.dialog.bottom.DialogSortPortfolio
import com.bcasekuritas.mybest.app.feature.dialog.bottom.DialogSortSector
import com.bcasekuritas.mybest.app.feature.dialog.bottom.DialogTncEipoOrder
import com.bcasekuritas.mybest.app.feature.dialog.bottom.DialogWithdrawGtcOrder
import com.bcasekuritas.mybest.app.feature.dialog.bottom.DialogWithdrawPortfolioOrder
import com.bcasekuritas.mybest.app.feature.dialog.bottom.fastorder.DialogAmendFastOrder
import com.bcasekuritas.mybest.app.feature.dialog.bottom.fastorder.DialogConfirmFastOrder
import com.bcasekuritas.mybest.app.feature.dialog.bottom.fastorder.DialogFastOrderSetting
import com.bcasekuritas.mybest.app.feature.dialog.bottom.fastorder.DialogOrderFastOrder
import com.bcasekuritas.mybest.app.feature.dialog.bottom.fastorder.DialogPreventFastOrder
import com.bcasekuritas.mybest.app.feature.dialog.bottom.fastorder.DialogWithdrawAllFastOrder
import com.bcasekuritas.mybest.app.feature.dialog.bottom.fastorder.DialogWithdrawFastOrder
import com.bcasekuritas.mybest.app.feature.dialog.bottom.order.DialogOrderInfoGTC
import com.bcasekuritas.mybest.app.feature.dialog.bottom.order.DialogOrderInfoOrderType
import com.bcasekuritas.mybest.app.feature.dialog.bottom.order.DialogOrderInfoSliceOrder
import com.bcasekuritas.mybest.app.feature.dialog.bottom.order.DialogOrderInfoSltp
import com.bcasekuritas.mybest.app.feature.dialog.bottom.portfolio.DialogInfoRealizedGainLoss
import com.bcasekuritas.mybest.app.feature.dialog.bottom.portfolio.DialogPortfolioSummaryInfo
import com.bcasekuritas.mybest.app.feature.dialog.bottom.runningtrade.DialogFilterRunningTrade
import com.bcasekuritas.mybest.app.feature.dialog.center.DialogConfirmationCenter
import com.bcasekuritas.mybest.app.feature.dialog.center.DialogInfoCenter
import com.bcasekuritas.mybest.app.feature.dialog.center.DialogLoadingCenter
import com.bcasekuritas.mybest.app.feature.dialog.center.DialogMonthPicker
import com.bcasekuritas.mybest.app.feature.dialog.center.DialogNumberPicker
import com.bcasekuritas.mybest.app.feature.dialog.coachmark.CoachmarkFastOrderDialog
import com.bcasekuritas.mybest.app.feature.dialog.coachmark.CoachmarkNoWatchlistDialog
import com.bcasekuritas.mybest.app.feature.dialog.coachmark.CoachmarkPortfolioDialog
import com.bcasekuritas.mybest.app.feature.dialog.coachmark.CoachmarkWatchlistDialog
import com.bcasekuritas.mybest.app.feature.dialog.expandabletext.DialogExpandableTextFragment
import com.bcasekuritas.mybest.app.feature.dialog.forcedupdate.ForcedUpdateDialog
import com.bcasekuritas.mybest.app.feature.dialog.order.DialogConfirmEIPOOrderFragment
import com.bcasekuritas.mybest.app.feature.dialog.order.DialogExerciseOrderFragment
import com.bcasekuritas.mybest.app.feature.dialog.order.DialogOrderBuyFragment
import com.bcasekuritas.mybest.app.feature.dialog.order.DialogOrderSellFragment
import com.bcasekuritas.mybest.app.feature.dialog.rdnhistoryfilter.DialogRdnHistoryFilterFragment
import com.bcasekuritas.mybest.app.feature.dialog.share.DialogSharePortfolioDetailFragment
import com.bcasekuritas.mybest.app.feature.dialog.share.DialogSharePortfolioFragment
import com.bcasekuritas.mybest.app.feature.dialog.share.DialogShareStockFragment
import com.bcasekuritas.mybest.app.feature.dialog.searchrecyclerview.searchstock.SearchStockDialog
import com.bcasekuritas.mybest.app.feature.dialog.withdrawconfirm.DialogWithdrawConfirmFragment
import com.bcasekuritas.mybest.app.feature.dialog.withdrawinfo.DialogWithdrawInfoFragment
import com.bcasekuritas.mybest.app.feature.fastorder.setupvolume.SetupVolumeFragment
import com.bcasekuritas.mybest.app.feature.pin.PinFragment
import com.bcasekuritas.mybest.app.feature.pin.accountdisable.AccountDisableFragment
import com.bcasekuritas.mybest.app.feature.security.changepin.ChangePinFragment
import com.bcasekuritas.mybest.ext.listener.OnDialogDismissListener

/** Global Variable */
var dialogLoadingCenter: DialogLoadingCenter? = null
var dialogInfoCenter: DialogInfoCenter? = null
var dialogConfirmationCenter: DialogConfirmationCenter? = null
var dialogNumberPicker: DialogNumberPicker? = null
var dialogMonthPicker: DialogMonthPicker? = null
var dialogInfoBottom: DialogInfoBottom? = null
var dialogSelectionBottom: DialogSelectionBottom? = null
var dialogWithdrawInfo: DialogWithdrawInfoFragment? = null
var dialogWithdrawConfirm: DialogWithdrawConfirmFragment? = null
var dialogRdnHistoryFilter: DialogRdnHistoryFilterFragment? = null
var dialogShareStock: DialogShareStockFragment? = null
var dialogSharePortfolio: DialogSharePortfolioFragment? = null
var dialogSharePortfolioDetail: DialogSharePortfolioDetailFragment? = null
var dialogSortTabPortfolio: DialogSortPortfolio? = null
var dialogFilterPortfolioOrder: DialogFilterPortfolioOrder? = null
var dialogOrderBuy: DialogOrderBuyFragment? = null
var dialogOrderSell: DialogOrderSellFragment? = null
var dialogBuyingLimitDetail: DialogExpandableTextFragment? = null
var dialogWithdrawOrderConfirm: DialogWithdrawPortfolioOrder? = null
var dialogFilterPortfolioHistory: DialogFilterPortfolioHistory? = null
var dialogCreateRenameCategory: DialogCreateRenameCategory? = null
var dialogSelectCategory: DialogSelectCategory? = null
var dialogEditWatchlist: DialogEditWatchlist? = null
var dialogPin: PinFragment? = null
var dialogChangePin: ChangePinFragment? = null
var dialogSetupVolume: SetupVolumeFragment? = null
var dialogFastOrderSetting: DialogFastOrderSetting? = null
var dialogOrderFastOrder: DialogOrderFastOrder? = null
var dialogConfirmFastOrder: DialogConfirmFastOrder? = null
var dialogWithdrawAllFastOrder: DialogWithdrawAllFastOrder? = null
var dialogWithdrawFastOrder: DialogWithdrawFastOrder? = null
var dialogPreventFastOrder: DialogPreventFastOrder? = null
var dialogAmendFastOrder: DialogAmendFastOrder? = null
var dialogExerciseOrder: DialogExerciseOrderFragment? = null
var dialogFilterPortfolioRealized : DialogFilterPortfolioRealized? = null
var dialogPortfolioSummaryInfo: DialogPortfolioSummaryInfo? = null
var dialogOrderInfoOrderType: DialogOrderInfoOrderType? = null
var dialogOrderInfoSltp: DialogOrderInfoSltp? = null
var dialogOrderInfoSliceOrder: DialogOrderInfoSliceOrder? = null
var dialogOrderInfoGTC: DialogOrderInfoGTC? = null
var dialogSortSector: DialogSortSector? = null
var dialogAccountDisable: AccountDisableFragment? = null
var dialogRemovePriceAlert: DialogRemovePriceAlert? = null
var dialogWithdrawGtcOrder: DialogWithdrawGtcOrder? = null
var dialogAmendGtcOrder: DialogAmendGtcOrder? = null
var dialogInfoFibonacci: DialogInfoFibonacci? = null
var dialogForcedUpdate: ForcedUpdateDialog? = null
var dialogEipoOrderConfirm: DialogConfirmEIPOOrderFragment? = null
var dialogInfoTncEipoOrder: DialogTncEipoOrder? = null
var dialogFilterRunningTrade: DialogFilterRunningTrade? = null
var dialogSearchStock: SearchStockDialog? = null
var dialogAllocationEipo: DialogAllocationEipoBottom? = null
var dialogCoachmarkNoWatchlist: CoachmarkNoWatchlistDialog? = null
var dialogCoachmarkWatchlist: CoachmarkWatchlistDialog? = null
var dialogCoachmarkFastOrder: CoachmarkFastOrderDialog? = null
var dialogCoachmarkPortfolio: CoachmarkPortfolioDialog? = null
var dialogExerciseExpired: DialogExerciseExpiredBottom? = null
var dialogRealizedGainLossInfo: DialogInfoRealizedGainLoss? = null
var dialogRemoveTrustedDevice: DialogRemoveTrustedDevice? = null
var dialogInfoManyRequestOtp: DialogInfoManyRequestOtp? = null
var dialogInfoManyDevicesOtp: DialogInfoManyDevicesOtp? = null
var dialogExerciseNotAvailable: DialogExerciseNotAvailableBottom? = null

interface ShowDialog {

    /** Dialog Loading */
    fun showDialogLoadingCenter(
        isCancelable: Boolean,
        uiDialogModel: UIDialogModel,
        fragmentManager: FragmentManager,
    )

    fun dismissDialogLoadingCenter()

    /** Dialog Info */
    fun showDialogInfoCenter(
        isCancelable: Boolean,
        uiDialogModel: UIDialogModel,
        fragmentManager: FragmentManager,
    )

    fun showDialogInfoCenterCallBack(
        isCancelable: Boolean, fragmentManager: FragmentManager,
        uiDialogModel: UIDialogModel, onOkClicked: ((Boolean) -> Unit)?,
    )

    fun showDialogConfirmationCenterCallBack( fragmentManager: FragmentManager,
        uiDialogModel: UIDialogModel, isSelection: Boolean, onOkClicked: ((Boolean) -> Unit)?,
    )

    fun showDialogNumberPicker( fragmentManager: FragmentManager, title: String,
                                minVal: Int, maxVal: Int, currVal: Int, onOkClicked: ((Int) -> Unit)?,
    )

    fun showDialogMonthPicker( fragmentManager: FragmentManager, title: String,
                                currMonth: Int, onOkClicked: ((Int) -> Unit)?,
    )

    fun showDialogInfoBottom(
        fragmentManager: FragmentManager,
        isCancelable: Boolean,
        uiDialogModel: UIDialogModel,
    )

    fun showDialogInfoBottomCallBack(
        fragmentManager: FragmentManager,
        isCancelable: Boolean,
        uiDialogModel: UIDialogModel,
        onOkClicked: ((Boolean) -> Unit)?
    )

    fun showDialogSelectionBottom(
        isCancelable: Boolean, fragmentManager: FragmentManager, reqTargetCode: String?,
        resTargetCode: String?, dataList: ArrayList<SelectionCheckRes>, singleOrMultiple: Boolean,
    )  // single = true, multiple = false


    /** Dialog RDN Withdraw */
    fun showDialogWithdrawInfoBottom(
        strOne: String,
        strTwo: String,
        fragmentManager: FragmentManager,
    )

    fun showDialogWithdrawConfirmBottom(
        uiDialogWithdrawModel: UIDialogWithdrawModel,
        fragmentManager: FragmentManager,
    )

    fun showDialogRdnHistoryFilterBottom(filterList: List<String>, startDate: Long, endDate: Long, fragmentManager: FragmentManager)

    /** Dialog RunningTrade */
    fun showDialogFilterRunningTrade(
        model: UIDialogRunningTradeModel,
        defaultFilterModel: UIDialogRunningTradeModel,
        isFirstOpen: Boolean,
        fragmentManager: FragmentManager
    )

    /** Dialog Portfolio */
    fun showDialogSortPortfolioBottom(intOne: Int, fragmentManager: FragmentManager)
    fun showDialogFilterPortfolioOrderBottom(
        data: UIDialogPortfolioOrderModel,
        listStock: List<String>,
        fragmentManager: FragmentManager,
        currentTab: Int
    )

    fun showDialogFilterPortfolioHistoryBottom(
        data: UIDialogPortfolioHistoryModel,
        listStock: List<String>,
        fragmentManager: FragmentManager,
    )

    /** Dialog Share */
    fun showDialogShareStock(
        isProfit: Boolean,
        stockCode: String,
        company: String,
        price: String,
        percentage: String,
        fragmentManager: FragmentManager,
    )

    fun showDialogSharePortfolio(
        portfolioReturn: String,
        isProfit: Boolean,
        fragmentManager: FragmentManager
    )

    fun showDialogSharePortfolioDetail(
        stockCode: String,
        currPrice: String,
        avgPrice: String,
        portfolioReturn: String,
        isProfit: Boolean,
        fragmentManager: FragmentManager
    )

    /** Dialog Order*/
    fun showDialogBuyConfirm(
        uiDialogOrderConfirmationModel: UIDialogOrderConfirmationModel,
        fragmentManager: FragmentManager,
    )

    fun showDialogSellConfirm(
        uiDialogSellModel: UIDialogSellModel,
        fragmentManager: FragmentManager,
    )

    fun showDialogInfoBottomBuyingLimitOrder(fragmentManager: FragmentManager)

    fun showDialogWithdrawOrderBottom(fromLayout: String, fragmentManager: FragmentManager)

    fun showDialogWithdrawGTCOrderBottom(fromLayout: String, fragmentManager: FragmentManager)
    fun showDialogTncEipoOrderBottom(fragmentManager: FragmentManager)

    fun showDialogRemovePriceAlert(fromLayout: String, fragmentManager: FragmentManager)

    /** Dialog Watchlist*/
    fun showDialogCreateCategoryCallBack(
        fragmentManager: FragmentManager,
        listCategory: List<String>,
        isRename: Boolean,
        oldCategoryName: String,
        onOkClicked: ((String) -> Unit)?,
    )

    fun showDialogSelectCategoryCallBack(
        fragmentManager: FragmentManager,
        listCategory: List<WatchListCategory>,
        onOkClicked: ((String, Boolean) -> Unit)?,
    )

    fun showDialogEditCategoryCallBack(
        fragmentManager: FragmentManager,
        onOkClicked: ((Boolean) -> Unit)?,
    )

    fun showDialogPin(fragmentManager: FragmentManager, onSuccess: ((Boolean, Boolean) -> Unit)?)
    fun showDialogChangePin(fragmentManager: FragmentManager, onSuccess: ((Boolean) -> Unit)?)
    fun showDialogAccountDisable(fragmentManager: FragmentManager)
    fun showDialogForcedUpdate(fragmentManager: FragmentManager, isForceUpdate: Boolean, onClickUpdated: ((Boolean) -> Unit)?)

    /** Dialog Fast Order*/


    fun showDialogSetupVolume(
        fragmentManager: FragmentManager,
        lastPrice: Double,
        stockCode: String,
        maxLimit: String,
        maxCash: String,
        onConfirm: ((String) -> Unit)?,
    )

    fun showDialogFastOrderSetting(
        fragmentManager: FragmentManager,
        isShowOrder: Boolean,
        isOrderCount: Boolean,
        isPreventOrder: Boolean,
        onSave: ((Boolean, Boolean, Boolean) -> Unit)?,
    )

    fun showDialogOrderFastOrder(
        fragmentManager: FragmentManager,
        sendOrderReq: SendOrderReq,
        onAddOrder: ((SendOrderReq) -> Unit)?,
    )

    fun showDialogConfirmFastOrder(
        fragmentManager: FragmentManager,
        buySell: String?,
        sendOrderReq: SendOrderReq,
        onConfirm: ((SendOrderReq) -> Unit)?,
        commFee: Double
    )

    fun showDialogWithdrawFastOrder(
        fragmentManager: FragmentManager,
        cancelFastOrderReq: CancelFastOrderReq,
        onConfirm: ((CancelFastOrderReq) -> Unit)?
    )

    fun showDialogWithdrawAllFastOrder(
        fragmentManager: FragmentManager,
        listWtidhrawAll: List<QtyPriceItem>,
        cancelFastOrderReq: CancelFastOrderReq,
        onConfirm: ((CancelFastOrderReq) -> Unit)?,
    )

    fun showDialogPreventFastOrder(
        fragmentManager: FragmentManager,
        sendOrderReq: SendOrderReq,
        onConfirm: ((SendOrderReq, Boolean) -> Unit)?,
    )

    fun showDialogAmendFastOrder(
        fragmentManager: FragmentManager,
        amendFastOrderReq: AmendFastOrderReq,
        onConfirm: ((AmendFastOrderReq) -> Unit)?,
    )

    fun showDialogExerciseOrderConfirm(
        uiDialogOrderConfirmationModel: UIDialogExerciseOrderModel,
        fragmentManager: FragmentManager,
    )

    fun showDialogEIPOOrderConfirm(
        uiDialogOrderConfirmationModel: UIDialogExerciseOrderModel,
        fragmentManager: FragmentManager)

    fun showDialogAllocationEipoBottom(
        fragmentManager: FragmentManager
    )

    fun showDialogFilterPortfolioRealizedBottom(
        data: UIDialogPortfolioRealizedModel,
        fragmentManager: FragmentManager,
    )

    fun showDialogPortfolioSummaryInfoBottom(
        title: String,
        desc: String,
        footText: String,
        fragmentManager: FragmentManager,
    )

    fun showDialogOrderInfoOrderType(fragmentManager: FragmentManager)
    fun showDialogOrderInfoSltp(fragmentManager: FragmentManager)
    fun showDialogOrderInfoSliceOrder(fragmentManager: FragmentManager)
    fun showDialogOrderInfoGtc(fragmentManager: FragmentManager)
    fun showDialogSortSectorBottom(state: Int, fragmentManager: FragmentManager)
    fun showDialogAmendGtcOrderBottom(fromLayout: String, fragmentManager: FragmentManager)
    fun showDialogFibonacci(fragmentManager: FragmentManager)

    fun showDialogSearchStock(fragmentManager: FragmentManager, selectedStock: List<String>, onSelectedStock: ((Boolean, String) -> Unit)?)
    fun showDialogCoachmarkNoWatchlist(fragmentManager: FragmentManager)
    fun showDialogCoachmarkWatchlist(fragmentManager: FragmentManager)
    fun showDialogCoachmarkFastOrder(fragmentManager: FragmentManager)
    fun showDialogCoachmarkPortfolio(fragmentManager: FragmentManager)
    fun showDialogExerciseExpiredBottom(
        fragmentManager: FragmentManager
    )

    fun showDialogRealizeGainLossInfoBottom(
        fragmentManager: FragmentManager
    )
    fun showDialogRemoveTrustedDevice(
        fromLayout: String, fragmentManager: FragmentManager
    )
    fun showDialogInfoManyRequestOtp(fragmentManager: FragmentManager)
    fun showDialogInfoManyDevicesOtp(fragmentManager: FragmentManager)

    fun showDialogExerciseNotAvailable(fragmentManager: FragmentManager, startHour: String)
}

/** Dialog Info */

class ShowDialogImpl : ShowDialog {

    override fun showDialogLoadingCenter(
        isCancelable: Boolean,
        uiDialogModel: UIDialogModel, fragmentManager: FragmentManager,
    ) {
        if (dialogLoadingCenter == null) {
            dialogLoadingCenter = DialogLoadingCenter(uiDialogModel)
            dialogLoadingCenter?.dialog?.setOnDismissListener {
                // This code will be executed when the dialog is dismissed
                dialogLoadingCenter = null
            }
            dialogLoadingCenter?.isCancelable = isCancelable
            dialogLoadingCenter?.show(fragmentManager, null)
        }
    }

    override fun dismissDialogLoadingCenter() {
        if (dialogLoadingCenter != null) {
            dialogLoadingCenter?.dismiss()
            dialogLoadingCenter = null
        }
    }

    override fun showDialogInfoCenter(
        isCancelable: Boolean,
        uiDialogModel: UIDialogModel, fragmentManager: FragmentManager,
    ) {
        if (dialogInfoCenter == null) {
            dialogInfoCenter = DialogInfoCenter(uiDialogModel)
            dialogInfoCenter?.dialog?.setOnDismissListener {
                // This code will be executed when the dialog is dismissed
                dialogInfoCenter = null
            }
            dialogInfoCenter?.isCancelable = isCancelable
            dialogInfoCenter?.show(fragmentManager, null)
        }
    }

    override fun showDialogInfoCenterCallBack(
        isCancelable: Boolean,
        fragmentManager: FragmentManager,
        uiDialogModel: UIDialogModel,
        onOkClicked: ((Boolean) -> Unit)?,
    ) {
        if (dialogInfoCenter == null) {
            dialogInfoCenter = DialogInfoCenter(uiDialogModel)

            dialogInfoCenter?.setOkButtonClickListener { category ->
                onOkClicked?.invoke(category)
                dialogInfoCenter = null
            }

            dialogInfoCenter?.setOnDialogDismissListener(object : OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogInfoCenter = null
                }

                override fun onDialogDismissed() {
                    dialogInfoCenter = null
                }
            })

            dialogInfoCenter?.isCancelable = isCancelable
            dialogInfoCenter?.show(fragmentManager, null)
        }
    }

    override fun showDialogConfirmationCenterCallBack(
        fragmentManager: FragmentManager,
        uiDialogModel: UIDialogModel,
        isSelection: Boolean,
        onOkClicked: ((Boolean) -> Unit)?,
    ) {

        if (dialogConfirmationCenter == null) {
            dialogConfirmationCenter = DialogConfirmationCenter(isSelection,uiDialogModel)

            dialogConfirmationCenter?.setOkButtonClickListener { category ->
                onOkClicked?.invoke(category)
                dialogConfirmationCenter = null
            }

            dialogConfirmationCenter?.setOnDialogDismissListener(object : OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogConfirmationCenter = null
                }

                override fun onDialogDismissed() {
                    dialogConfirmationCenter = null
                }
            })

            dialogConfirmationCenter?.isCancelable = true
            dialogConfirmationCenter?.show(fragmentManager, null)
        }
    }

    override fun showDialogNumberPicker(
        fragmentManager: FragmentManager,
        title: String,
        minVal: Int,
        maxVal: Int,
        currVal: Int,
        onOkClicked: ((Int) -> Unit)?,
    ) {
        if (dialogNumberPicker == null) {
            dialogNumberPicker = DialogNumberPicker(title, minVal, maxVal, currVal)

            dialogNumberPicker?.setOkButtonClickListener { value ->
                onOkClicked?.invoke(value)
                dialogNumberPicker = null
            }

            dialogNumberPicker?.setOnDialogDismissListener(object : OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogNumberPicker = null
                }

                override fun onDialogDismissed() {
                    dialogNumberPicker = null
                }
            })

            dialogNumberPicker?.isCancelable = true
            dialogNumberPicker?.show(fragmentManager, null)
        }
    }

    override fun showDialogMonthPicker(
        fragmentManager: FragmentManager,
        title: String,
        currMonth: Int,
        onOkClicked: ((Int) -> Unit)?,
    ) {
        if (dialogMonthPicker == null) {
            dialogMonthPicker = DialogMonthPicker(title, currMonth)

            dialogMonthPicker?.setOkButtonClickListener { monthNumber ->
                onOkClicked?.invoke(monthNumber)
                dialogMonthPicker = null
            }

            dialogMonthPicker?.setOnDialogDismissListener(object : OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogMonthPicker = null
                }

                override fun onDialogDismissed() {
                    dialogMonthPicker = null
                }
            })

            dialogMonthPicker?.isCancelable = true
            dialogMonthPicker?.show(fragmentManager, null)
        }
    }

    override fun showDialogInfoBottom(
        fragmentManager: FragmentManager,
        isCancelable: Boolean,
        uiDialogModel: UIDialogModel,
    ) {
        if (dialogInfoBottom == null) {
            dialogInfoBottom = DialogInfoBottom(uiDialogModel)
            dialogInfoBottom?.setOnDialogDismissListener(object : OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogInfoBottom = null
                }

                override fun onDialogDismissed() {
                    dialogInfoBottom = null
                }
            })
            dialogInfoBottom?.isCancelable = isCancelable

            dialogInfoBottom?.show(fragmentManager, null)
        }
    }

    override fun showDialogInfoBottomCallBack(
        fragmentManager: FragmentManager,
        isCancelable: Boolean,
        uiDialogModel: UIDialogModel,
        onOkClicked: ((Boolean) -> Unit)?
    ) {
        if (dialogInfoBottom == null) {
            dialogInfoBottom = DialogInfoBottom(uiDialogModel)

            dialogInfoBottom?.setOkButtonClickListener { boolean ->
                onOkClicked?.invoke(boolean)
                dialogInfoBottom = null
            }

            dialogInfoBottom?.setOnDialogDismissListener(object : OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogInfoBottom = null
                }

                override fun onDialogDismissed() {
                    dialogInfoBottom = null
                }
            })
            dialogInfoBottom?.isCancelable = isCancelable
            dialogInfoBottom?.show(fragmentManager, null)
        }
    }

    override fun showDialogCreateCategoryCallBack(
        fragmentManager: FragmentManager,
        listCategory: List<String>,
        isRename: Boolean,
        oldCategoryName: String,
        onOkClicked: ((String) -> Unit)?,
    ) {
        if (dialogCreateRenameCategory == null) {
            dialogCreateRenameCategory =
                DialogCreateRenameCategory(listCategory, isRename, oldCategoryName)

            dialogCreateRenameCategory?.setOkButtonClickListener { category ->
                onOkClicked?.invoke(category)
                dialogCreateRenameCategory = null
            }

            dialogCreateRenameCategory?.setOnDialogDismissListener(object :
                OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogCreateRenameCategory = null
                }

                override fun onDialogDismissed() {
                    dialogCreateRenameCategory = null
                }
            })
            dialogCreateRenameCategory?.show(fragmentManager, null)
        }
    }

    override fun showDialogSelectCategoryCallBack(
        fragmentManager: FragmentManager,
        listCategory: List<WatchListCategory>,
        onOkClicked: ((String, Boolean) -> Unit)?,
    ) {
        if (dialogSelectCategory == null) {
            dialogSelectCategory = DialogSelectCategory(listCategory)

            dialogSelectCategory?.setOkButtonClickListener { category, isCreateCategory ->
                onOkClicked?.invoke(category, isCreateCategory)
                dialogSelectCategory = null
            }

            dialogSelectCategory?.setOnDialogDismissListener(object : OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogSelectCategory = null
                }

                override fun onDialogDismissed() {
                    dialogSelectCategory = null
                }
            })
            dialogSelectCategory?.show(fragmentManager, null)
        }
    }

    override fun showDialogEditCategoryCallBack(
        fragmentManager: FragmentManager,
        onOkClicked: ((Boolean) -> Unit)?,
    ) {
        if (dialogEditWatchlist == null) {
            dialogEditWatchlist = DialogEditWatchlist()

            dialogEditWatchlist?.setOkButtonClickListener { isDelete ->
                onOkClicked?.invoke(isDelete)
                dialogEditWatchlist = null
            }

            dialogEditWatchlist?.setOnDialogDismissListener(object : OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogEditWatchlist = null
                }

                override fun onDialogDismissed() {
                    dialogEditWatchlist = null
                }
            })
            dialogEditWatchlist?.show(fragmentManager, null)
        }
    }

    override fun showDialogForcedUpdate(fragmentManager: FragmentManager, isForceUpdate: Boolean, onClickUpdated: ((Boolean) -> Unit)?) {
        if (dialogForcedUpdate == null) {
            dialogForcedUpdate = ForcedUpdateDialog(isForceUpdate)

            dialogForcedUpdate?.setOnClickUpdated { isClickUpdated ->
                onClickUpdated?.invoke(isClickUpdated)
                dialogInfoCenter = null
            }

            dialogForcedUpdate?.setOnDialogDismissListener(object : OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogForcedUpdate = null
                }

                override fun onDialogDismissed() {
                    dialogForcedUpdate = null
                }
            })

            dialogForcedUpdate?.isCancelable = !isForceUpdate
            dialogForcedUpdate?.show(fragmentManager, null)
        }
    }

    override fun showDialogPin(
        fragmentManager: FragmentManager,
        onSuccess: ((Boolean, Boolean) -> Unit)?
    ) {
        if (dialogPin == null) {
            dialogPin = PinFragment()

            dialogPin?.setOnPinSuccess { isSuccess, isBlocked  ->
                onSuccess?.invoke(isSuccess, isBlocked)
            }

            dialogPin?.setOnDialogDismissListener(object : OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogPin?.setOnPinSuccess { isSuccess, isBlocked ->
                        onSuccess?.invoke(false, isBlocked)
                        dialogPin = null
                    }
                }

                override fun onDialogDismissed() {
                    dialogPin = null
                }
            })
            dialogPin?.show(fragmentManager, null)
        }
    }

    override fun showDialogAccountDisable(fragmentManager: FragmentManager) {
        if (dialogAccountDisable == null) {
            dialogAccountDisable = AccountDisableFragment()

            dialogAccountDisable?.setOnDialogDismissListener(object : OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogAccountDisable = null
                }

                override fun onDialogDismissed() {
                    dialogAccountDisable = null
                }
            })

            dialogAccountDisable?.show(fragmentManager, null)
        }
    }

    override fun showDialogChangePin(
        fragmentManager: FragmentManager,
        onSuccess: ((Boolean) -> Unit)?,
    ) {
        if (dialogChangePin == null) {
            dialogChangePin = ChangePinFragment()

            dialogChangePin?.setOnPinSuccess { isSuccess ->
                onSuccess?.invoke(isSuccess)
            }

            dialogChangePin?.setOnDialogDismissListener(object : OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogChangePin?.setOnPinSuccess { isSuccess ->
                        dialogChangePin = null
                    }
                }

                override fun onDialogDismissed() {
                    dialogChangePin = null
                }
            })
            dialogChangePin?.show(fragmentManager, null)
        }
    }

    override fun showDialogSetupVolume(
        fragmentManager: FragmentManager,
        lastPrice: Double,
        stockCode: String,
        maxLimit: String,
        maxCash: String,
        onConfirm: ((String) -> Unit)?,
    ) {
        dialogSetupVolume

        if (dialogSetupVolume == null) {
            dialogSetupVolume = SetupVolumeFragment(maxLimit, maxCash, stockCode, lastPrice)

            dialogSetupVolume?.setOnDialogDismissListener(object : OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogSetupVolume = null

                }

                override fun onDialogDismissed() {
                    dialogSetupVolume = null
                }
            })


            dialogSetupVolume?.setOnPinSuccess { volume ->
                onConfirm?.invoke(volume)
            }

            dialogSetupVolume?.show(fragmentManager, null)
        }

    }

    override fun showDialogSelectionBottom(
        isCancelable: Boolean,
        fragmentManager: FragmentManager,
        reqTargetCode: String?,
        resTargetCode: String?,
        dataList: ArrayList<SelectionCheckRes>,
        singleOrMultiple: Boolean,
    ) {
        if (dialogSelectionBottom == null) {
            dialogSelectionBottom =
                DialogSelectionBottom(reqTargetCode, dataList, singleOrMultiple, resTargetCode)
            dialogSelectionBottom?.setOnDialogDismissListener(object : OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogSelectionBottom = null
                }

                override fun onDialogDismissed() {
                    dialogSelectionBottom = null
                }
            })
            dialogSelectionBottom?.isCancelable = isCancelable
            dialogSelectionBottom?.show(fragmentManager, null)
        }
    }

    override fun showDialogWithdrawInfoBottom(
        strOne: String,
        strTwo: String,
        fragmentManager: FragmentManager,
    ) {
        if (dialogWithdrawInfo == null) {
            dialogWithdrawInfo = DialogWithdrawInfoFragment(strOne, strTwo)
            dialogWithdrawInfo?.setOnDialogDismissListener(object : OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogWithdrawInfo = null
                }

                override fun onDialogDismissed() {
                    dialogWithdrawInfo = null
                }
            })
            dialogWithdrawInfo?.isCancelable = false
            dialogWithdrawInfo?.show(fragmentManager, null)
        }
    }

    override fun showDialogWithdrawConfirmBottom(
        uiDialogWithdrawModel: UIDialogWithdrawModel,
        fragmentManager: FragmentManager,
    ) {
        if (dialogWithdrawConfirm == null) {
            dialogWithdrawConfirm = DialogWithdrawConfirmFragment(uiDialogWithdrawModel)
            dialogWithdrawConfirm?.setOnDialogDismissListener(object : OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogWithdrawConfirm = null
                }

                override fun onDialogDismissed() {
                    dialogWithdrawConfirm = null
                }
            })
            dialogWithdrawConfirm?.isCancelable = false
            dialogWithdrawConfirm?.show(fragmentManager, null)
        }
    }

    override fun showDialogRdnHistoryFilterBottom(
        filterList: List<String>,
        startDate: Long,
        endDate: Long,
        fragmentManager: FragmentManager,
    ) {
        if (dialogRdnHistoryFilter == null) {
            dialogRdnHistoryFilter = DialogRdnHistoryFilterFragment(filterList, startDate, endDate)
            dialogRdnHistoryFilter?.setOnDialogDismissListener(object : OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogRdnHistoryFilter = null
                }

                override fun onDialogDismissed() {
                    dialogRdnHistoryFilter = null
                }
            })
            dialogRdnHistoryFilter?.isCancelable = false
            dialogRdnHistoryFilter?.show(fragmentManager, null)
        }
    }

    override fun showDialogShareStock(
        isProfit: Boolean,
        stockCode: String,
        company: String,
        price: String,
        percentage: String,
        fragmentManager: FragmentManager,
    ) {
        if (dialogShareStock == null) {
            dialogShareStock = DialogShareStockFragment(isProfit, stockCode, company, price, percentage)
            dialogShareStock?.setOnDialogDismissListener(object : OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogShareStock = null
                }

                override fun onDialogDismissed() {
                    dialogShareStock = null
                }
            })
            dialogShareStock?.isCancelable = true
            dialogShareStock?.show(fragmentManager, null)
        }
    }

    override fun showDialogSharePortfolio(
        portfolioReturn: String,
        isProfit: Boolean,
        fragmentManager: FragmentManager,
    ) {

        if (dialogSharePortfolio == null) {
            dialogSharePortfolio = DialogSharePortfolioFragment(portfolioReturn, isProfit)
            dialogSharePortfolio?.setOnDialogDismissListener(object : OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogSharePortfolio = null
                }

                override fun onDialogDismissed() {
                    dialogSharePortfolio = null
                }
            })
            dialogSharePortfolio?.isCancelable = true
            dialogSharePortfolio?.show(fragmentManager, null)
        }
    }

    override fun showDialogSharePortfolioDetail(
        stockCode: String,
        currPrice: String,
        avgPrice: String,
        portfolioReturn: String,
        isProfit: Boolean,
        fragmentManager: FragmentManager,
    ) {

        if (dialogSharePortfolioDetail == null) {
            dialogSharePortfolioDetail = DialogSharePortfolioDetailFragment(stockCode, currPrice, avgPrice, portfolioReturn, isProfit)
            dialogSharePortfolioDetail?.setOnDialogDismissListener(object : OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogSharePortfolioDetail = null
                }

                override fun onDialogDismissed() {
                    dialogSharePortfolioDetail = null
                }
            })
            dialogSharePortfolioDetail?.isCancelable = true
            dialogSharePortfolioDetail?.show(fragmentManager, null)
        }
    }

    override fun showDialogSortPortfolioBottom(intOne: Int, fragmentManager: FragmentManager) {
        if (dialogSortTabPortfolio == null) {
            dialogSortTabPortfolio = DialogSortPortfolio(intOne)
            dialogSortTabPortfolio?.setOnDialogDismissListener(object : OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogSortTabPortfolio = null
                }

                override fun onDialogDismissed() {
                    dialogSortTabPortfolio = null
                }
            })
            dialogSortTabPortfolio?.isCancelable = true
            dialogSortTabPortfolio?.show(fragmentManager, null)
        }
    }

    override fun showDialogBuyConfirm(
        uiDialogOrderConfirmationModel: UIDialogOrderConfirmationModel,
        fragmentManager: FragmentManager,
    ) {
        if (dialogOrderBuy == null) {
            dialogOrderBuy = DialogOrderBuyFragment(uiDialogOrderConfirmationModel)
            dialogOrderBuy?.setOnDialogDismissListener(object : OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogOrderBuy = null
                }

                override fun onDialogDismissed() {
                    dialogOrderBuy = null
                }
            })
            dialogOrderBuy?.isCancelable = true
            dialogOrderBuy?.show(fragmentManager, null)
        }
    }

    override fun showDialogAllocationEipoBottom(fragmentManager: FragmentManager) {
        if (dialogAllocationEipo == null) {
            dialogAllocationEipo = DialogAllocationEipoBottom()
            dialogAllocationEipo?.setOnDialogDismissListener(object : OnDialogDismissListener {
                override fun onDialogDismissed() {
                    dialogAllocationEipo = null
                }

                override fun onDialogCanceled() {
                    dialogAllocationEipo = null
                }
            })
            dialogAllocationEipo?.isCancelable = false
            dialogAllocationEipo?.show(fragmentManager, null)
        }
    }

    override fun showDialogExerciseExpiredBottom(fragmentManager: FragmentManager) {
        if (dialogExerciseExpired == null) {
            dialogExerciseExpired = DialogExerciseExpiredBottom()
            dialogExerciseExpired?.setOnDialogDismissListener(object : OnDialogDismissListener {
                override fun onDialogDismissed() {
                    dialogExerciseExpired = null
                }

                override fun onDialogCanceled() {
                    dialogExerciseExpired = null
                }
            })
            dialogExerciseExpired?.isCancelable = false
            dialogExerciseExpired?.show(fragmentManager, null)
        }
    }

    override fun showDialogEIPOOrderConfirm(
        uiDialogOrderConfirmationModel: UIDialogExerciseOrderModel,
        fragmentManager: FragmentManager,
    ) {
        if (dialogEipoOrderConfirm == null) {
            dialogEipoOrderConfirm = DialogConfirmEIPOOrderFragment(uiDialogOrderConfirmationModel)
            dialogEipoOrderConfirm?.setOnDialogDismissListener(object : OnDialogDismissListener {
                override fun onDialogDismissed() {
                    dialogEipoOrderConfirm = null
                }

                override fun onDialogCanceled() {
                    dialogEipoOrderConfirm = null
                }
            })
            dialogEipoOrderConfirm?.isCancelable = true
            dialogEipoOrderConfirm?.show(fragmentManager, null)
        }
    }

    override fun showDialogExerciseOrderConfirm(
        uiDialogOrderConfirmationModel: UIDialogExerciseOrderModel,
        fragmentManager: FragmentManager,
    ) {
        if (dialogExerciseOrder == null) {
            dialogExerciseOrder = DialogExerciseOrderFragment(uiDialogOrderConfirmationModel)
            dialogExerciseOrder?.setOnDialogDismissListener(object : OnDialogDismissListener {
                override fun onDialogDismissed() {
                    dialogExerciseOrder = null
                }

                override fun onDialogCanceled() {
                    dialogExerciseOrder = null
                }
            })
            dialogExerciseOrder?.isCancelable = true
            dialogExerciseOrder?.show(fragmentManager, null)
        }
    }

    override fun showDialogSellConfirm(
        uiDialogSellModel: UIDialogSellModel,
        fragmentManager: FragmentManager,
    ) {
        if (dialogOrderSell == null) {
            dialogOrderSell = DialogOrderSellFragment(uiDialogSellModel)
            dialogOrderSell?.setOnDialogDismissListener(object : OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogOrderSell = null
                }

                override fun onDialogDismissed() {
                    dialogOrderSell = null
                }
            })
            dialogOrderSell?.isCancelable = true
            dialogOrderSell?.show(fragmentManager, null)
        }
    }

    override fun showDialogInfoBottomBuyingLimitOrder(fragmentManager: FragmentManager) {
        if (dialogBuyingLimitDetail == null) {
            dialogBuyingLimitDetail = DialogExpandableTextFragment()
            dialogBuyingLimitDetail?.setOnDialogDismissListener(object : OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogBuyingLimitDetail = null
                }

                override fun onDialogDismissed() {
                    dialogBuyingLimitDetail = null
                }
            })
            dialogBuyingLimitDetail?.isCancelable = true
            dialogBuyingLimitDetail?.show(fragmentManager, null)
        }
    }

    override fun showDialogFilterPortfolioOrderBottom(
        data: UIDialogPortfolioOrderModel,
        listStock: List<String>,
        fragmentManager: FragmentManager,
        currentTab: Int
    ) {
        if (dialogFilterPortfolioOrder == null) {
            dialogFilterPortfolioOrder = DialogFilterPortfolioOrder(data, listStock, currentTab)
            dialogFilterPortfolioOrder?.setOnDialogDismissListener(object :
                OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogFilterPortfolioOrder = null
                }

                override fun onDialogDismissed() {
                    dialogFilterPortfolioOrder = null
                }
            })
            dialogFilterPortfolioOrder?.isCancelable = true
            dialogFilterPortfolioOrder?.show(fragmentManager, null)
        }
    }

    override fun showDialogFilterRunningTrade(
        model: UIDialogRunningTradeModel,
        defaultFilterModel: UIDialogRunningTradeModel,
        isFirstOpen: Boolean,
        fragmentManager: FragmentManager,
    ) {
        if (dialogFilterRunningTrade == null) {
            dialogFilterRunningTrade = DialogFilterRunningTrade(model, defaultFilterModel, isFirstOpen)
            dialogFilterRunningTrade?.setOnDialogDismissListener(object : OnDialogDismissListener {
                override fun onDialogDismissed() {
                    dialogFilterRunningTrade = null
                }

                override fun onDialogCanceled() {
                    dialogFilterRunningTrade = null
                }
            })
            dialogFilterRunningTrade?.isCancelable = false
            dialogFilterRunningTrade?.show(fragmentManager, null)
        }
    }

    override fun showDialogWithdrawOrderBottom(
        fromLayout: String,
        fragmentManager: FragmentManager,
    ) {
        if (dialogWithdrawOrderConfirm == null) {
            dialogWithdrawOrderConfirm = DialogWithdrawPortfolioOrder(fromLayout)
            dialogWithdrawOrderConfirm?.setOnDialogDismissListener(object :
                OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogWithdrawOrderConfirm = null
                }

                override fun onDialogDismissed() {
                    dialogWithdrawOrderConfirm = null
                }
            })
            dialogWithdrawOrderConfirm?.isCancelable = true
            dialogWithdrawOrderConfirm?.show(fragmentManager, null)
        }
    }

    override fun showDialogWithdrawGTCOrderBottom(
        fromLayout: String,
        fragmentManager: FragmentManager,
    ) {
        if (dialogWithdrawGtcOrder == null) {
            dialogWithdrawGtcOrder = DialogWithdrawGtcOrder(fromLayout)
            dialogWithdrawGtcOrder?.setOnDialogDismissListener(object :
                OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogWithdrawGtcOrder = null
                }

                override fun onDialogDismissed() {
                    dialogWithdrawGtcOrder = null
                }
            })
            dialogWithdrawGtcOrder?.isCancelable = true
            dialogWithdrawGtcOrder?.show(fragmentManager, null)
        }
    }

    override fun showDialogFilterPortfolioHistoryBottom(
        data: UIDialogPortfolioHistoryModel,
        listStock: List<String>,
        fragmentManager: FragmentManager,
    ) {
        if (dialogFilterPortfolioHistory == null) {
            dialogFilterPortfolioHistory = DialogFilterPortfolioHistory(data, listStock)
            dialogFilterPortfolioHistory!!.setOnDialogDismissListener(object :
                OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogFilterPortfolioHistory = null
                }

                override fun onDialogDismissed() {
                    dialogFilterPortfolioHistory = null
                }
            })
            dialogFilterPortfolioHistory!!.isCancelable = true
            dialogFilterPortfolioHistory!!.show(fragmentManager, null)
        }
    }

    override fun showDialogFastOrderSetting(
        fragmentManager: FragmentManager,
        isShowOrder: Boolean,
        isOrderCount: Boolean,
        isPreventOrder: Boolean,
        onSave: ((Boolean, Boolean, Boolean) -> Unit)?,
    ) {
        if (dialogFastOrderSetting == null) {
            dialogFastOrderSetting =
                DialogFastOrderSetting(isShowOrder, isOrderCount, isPreventOrder)
            dialogFastOrderSetting?.setOnDialogDismissListener(object : OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogFastOrderSetting = null
                }

                override fun onDialogDismissed() {
                    dialogFastOrderSetting = null
                }
            })


            dialogFastOrderSetting?.setSaveButtonClickListener { isCheckShowOrder, isCheckOrderCount, isCheckPreventOrder ->
                onSave?.invoke(isCheckShowOrder, isCheckOrderCount, isCheckPreventOrder)
                dialogFastOrderSetting = null
            }

            dialogFastOrderSetting?.isCancelable = true
            dialogFastOrderSetting?.show(fragmentManager, null)
        }
    }

    override fun showDialogOrderFastOrder(
        fragmentManager: FragmentManager,
        sendOrderReq: SendOrderReq,
        onAddOrder: ((SendOrderReq) -> Unit)?,
    ) {
        if (dialogOrderFastOrder == null) {
            dialogOrderFastOrder = DialogOrderFastOrder(sendOrderReq)
            dialogOrderFastOrder?.setOnDialogDismissListener(object : OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogOrderFastOrder = null
                }

                override fun onDialogDismissed() {
                    dialogOrderFastOrder = null
                }
            })


            dialogOrderFastOrder?.setAddOrderBtnClickListener { sendOrder ->
                onAddOrder?.invoke(sendOrder)
                dialogOrderFastOrder = null
            }

            dialogOrderFastOrder?.isCancelable = true
            dialogOrderFastOrder?.show(fragmentManager, null)
        }
    }

    override fun showDialogConfirmFastOrder(
        fragmentManager: FragmentManager,
        buySell: String?,
        sendOrderReq: SendOrderReq,
        onConfirm: ((SendOrderReq) -> Unit)?,
        commFee: Double
    ) {

        val title: String = if (buySell == "B") {
            "Confirm Buy"
        } else {
            "Confirm Sell"
        }

        if (dialogConfirmFastOrder == null) {
            dialogConfirmFastOrder =
                DialogConfirmFastOrder(false, title, sendOrderReq, CancelFastOrderReq(), commFee)
            dialogConfirmFastOrder?.setOnDialogDismissListener(object : OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogConfirmFastOrder = null
                }

                override fun onDialogDismissed() {
                    dialogConfirmFastOrder = null
                }
            })


            dialogConfirmFastOrder?.setConfirmButtonClickListener { sendOrder, _ ->
                onConfirm?.invoke(sendOrder)
                dialogConfirmFastOrder = null
            }

            dialogConfirmFastOrder?.isCancelable = true
            dialogConfirmFastOrder?.show(fragmentManager, null)
        }

    }

    override fun showDialogWithdrawFastOrder(
        fragmentManager: FragmentManager,
        cancelFastOrderReq: CancelFastOrderReq,
        onConfirm: ((CancelFastOrderReq) -> Unit)?
    ) {

        if (dialogWithdrawFastOrder == null) {
            dialogWithdrawFastOrder = DialogWithdrawFastOrder(cancelFastOrderReq)
            dialogWithdrawFastOrder?.setOnDialogDismissListener(object : OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogWithdrawFastOrder = null
                }

                override fun onDialogDismissed() {
                    dialogWithdrawFastOrder = null
                }
            })


            dialogWithdrawFastOrder?.setConfirmButtonClickListener { cancelFastOrder ->
                onConfirm?.invoke(cancelFastOrder)
                dialogWithdrawFastOrder = null
            }

            dialogWithdrawFastOrder?.isCancelable = true
            dialogWithdrawFastOrder?.show(fragmentManager, null)
        }
    }

    override fun showDialogWithdrawAllFastOrder(
        fragmentManager: FragmentManager,
        listWtidhrawAll: List<QtyPriceItem>,
        cancelFastOrderReq: CancelFastOrderReq,
        onConfirm: ((CancelFastOrderReq) -> Unit)?,
    ) {
        if (dialogWithdrawAllFastOrder == null) {
            dialogWithdrawAllFastOrder =
                DialogWithdrawAllFastOrder(listWtidhrawAll, cancelFastOrderReq)
            dialogWithdrawAllFastOrder?.setOnDialogDismissListener(object :
                OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogWithdrawAllFastOrder = null
                }

                override fun onDialogDismissed() {
                    dialogWithdrawAllFastOrder = null
                }
            })

            dialogWithdrawAllFastOrder?.setConfirmButtonClickListener { cancelFastOrder ->
                onConfirm?.invoke(cancelFastOrder)
                dialogWithdrawAllFastOrder = null
            }

            dialogWithdrawAllFastOrder?.isCancelable = true
            dialogWithdrawAllFastOrder?.show(fragmentManager, null)
        }
    }

    override fun showDialogPreventFastOrder(
        fragmentManager: FragmentManager,
        sendOrderReq: SendOrderReq,
        onConfirm: ((SendOrderReq, Boolean) -> Unit)?,
    ) {


        if (dialogPreventFastOrder == null) {
            dialogPreventFastOrder =
                DialogPreventFastOrder(sendOrderReq)
            dialogPreventFastOrder?.setOnDialogDismissListener(object :
                OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogPreventFastOrder = null
                }

                override fun onDialogDismissed() {
                    dialogPreventFastOrder = null
                }
            })

            dialogPreventFastOrder?.setConfirmButtonClickListener { cancelFastOrder, isChecked ->
                onConfirm?.invoke(cancelFastOrder, isChecked)
                dialogPreventFastOrder = null
            }

            dialogPreventFastOrder?.isCancelable = true
            dialogPreventFastOrder?.show(fragmentManager, null)
        }
    }

    override fun showDialogAmendFastOrder(
        fragmentManager: FragmentManager,
        amendFastOrderReq: AmendFastOrderReq,
        onConfirm: ((AmendFastOrderReq) -> Unit)?,
    ) {

        if (dialogAmendFastOrder == null) {
            dialogAmendFastOrder = DialogAmendFastOrder(amendFastOrderReq)
            dialogAmendFastOrder?.setOnDialogDismissListener(object : OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogAmendFastOrder = null
                }

                override fun onDialogDismissed() {
                    dialogAmendFastOrder = null
                }
            })

            dialogAmendFastOrder?.setConfirmButtonClickListener { amendOrder ->
                onConfirm?.invoke(amendOrder)
                dialogAmendFastOrder = null
            }

            dialogAmendFastOrder?.isCancelable = true
            dialogAmendFastOrder?.show(fragmentManager, null)
        }
    }

    override fun showDialogFilterPortfolioRealizedBottom(
        data: UIDialogPortfolioRealizedModel,
        fragmentManager: FragmentManager,
    ) {
        if (dialogFilterPortfolioRealized == null) {
            dialogFilterPortfolioRealized = DialogFilterPortfolioRealized(data)
            dialogFilterPortfolioRealized?.setOnDialogDismissListener(object :
                OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogFilterPortfolioRealized = null
                }

                override fun onDialogDismissed() {
                    dialogFilterPortfolioRealized = null
                }
            })
            dialogFilterPortfolioRealized?.isCancelable = true
            dialogFilterPortfolioRealized?.show(fragmentManager, null)
        }
    }

    override fun showDialogPortfolioSummaryInfoBottom(
        title: String,
        desc: String,
        footText: String,
        fragmentManager: FragmentManager,
    ) {
        if (dialogPortfolioSummaryInfo == null) {
            dialogPortfolioSummaryInfo = DialogPortfolioSummaryInfo(title, desc, footText)
            dialogPortfolioSummaryInfo?.setOnDialogDismissListener(object : OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogPortfolioSummaryInfo = null
                }

                override fun onDialogDismissed() {
                    dialogPortfolioSummaryInfo = null
                }
            })
            dialogPortfolioSummaryInfo?.isCancelable = false
            dialogPortfolioSummaryInfo?.show(fragmentManager, null)
        }
    }

    override fun showDialogOrderInfoOrderType(fragmentManager: FragmentManager) {
        if (dialogOrderInfoOrderType == null) {
            dialogOrderInfoOrderType = DialogOrderInfoOrderType()
            dialogOrderInfoOrderType?.setOnDialogDismissListener(object : OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogOrderInfoOrderType = null
                }

                override fun onDialogDismissed() {
                    dialogOrderInfoOrderType = null
                }
            })
            dialogOrderInfoOrderType?.isCancelable = false
            dialogOrderInfoOrderType?.show(fragmentManager, null)
        }
    }

    override fun showDialogFibonacci(fragmentManager: FragmentManager) {
        if (dialogInfoFibonacci == null) {
            dialogInfoFibonacci = DialogInfoFibonacci()
            dialogInfoFibonacci?.setOnDialogDismissListener(object : OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogInfoFibonacci = null
                }

                override fun onDialogDismissed() {
                    dialogInfoFibonacci = null
                }
            })
            dialogInfoFibonacci?.isCancelable = false
            dialogInfoFibonacci?.show(fragmentManager, null)
        }
    }

    override fun showDialogOrderInfoSltp(fragmentManager: FragmentManager) {
        if (dialogOrderInfoSltp == null) {
            dialogOrderInfoSltp = DialogOrderInfoSltp()
            dialogOrderInfoSltp?.setOnDialogDismissListener(object : OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogOrderInfoSltp = null
                }

                override fun onDialogDismissed() {
                    dialogOrderInfoSltp = null
                }
            })
            dialogOrderInfoSltp?.isCancelable = false
            dialogOrderInfoSltp?.show(fragmentManager, null)
        }
    }

    override fun showDialogOrderInfoSliceOrder(fragmentManager: FragmentManager) {
        if (dialogOrderInfoSliceOrder == null) {
            dialogOrderInfoSliceOrder = DialogOrderInfoSliceOrder()
            dialogOrderInfoSliceOrder?.setOnDialogDismissListener(object : OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogOrderInfoSliceOrder = null
                }

                override fun onDialogDismissed() {
                    dialogOrderInfoSliceOrder = null
                }
            })
            dialogOrderInfoSliceOrder?.isCancelable = false
            dialogOrderInfoSliceOrder?.show(fragmentManager, null)
        }
    }

    override fun showDialogOrderInfoGtc(fragmentManager: FragmentManager) {
        if (dialogOrderInfoGTC == null) {
            dialogOrderInfoGTC = DialogOrderInfoGTC()
            dialogOrderInfoGTC?.setOnDialogDismissListener(object : OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogOrderInfoGTC = null
                }

                override fun onDialogDismissed() {
                    dialogOrderInfoGTC = null
                }
            })
            dialogOrderInfoGTC?.isCancelable = false
            dialogOrderInfoGTC?.show(fragmentManager, null)
        }
    }

    override fun showDialogSortSectorBottom(state: Int, fragmentManager: FragmentManager) {
        if (dialogSortSector == null) {
            dialogSortSector = DialogSortSector(state)
            dialogSortSector?.setOnDialogDismissListener(object : OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogSortSector = null
                }

                override fun onDialogDismissed() {
                    dialogSortSector = null
                }
            })
            dialogSortSector?.isCancelable = true
            dialogSortSector?.show(fragmentManager, null)
        }
    }

    override fun showDialogRemovePriceAlert(fromLayout: String, fragmentManager: FragmentManager) {
        if (dialogRemovePriceAlert == null) {
            dialogRemovePriceAlert = DialogRemovePriceAlert(fromLayout)
            dialogRemovePriceAlert?.setOnDialogDismissListener(object :
                OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogRemovePriceAlert = null
                }

                override fun onDialogDismissed() {
                    dialogRemovePriceAlert = null
                }
            })
            dialogRemovePriceAlert?.isCancelable = true
            dialogRemovePriceAlert?.show(fragmentManager, null)
        }
    }

    override fun showDialogAmendGtcOrderBottom(
        fromLayout: String,
        fragmentManager: FragmentManager,
    ) {
        if (dialogAmendGtcOrder == null) {
            dialogAmendGtcOrder = DialogAmendGtcOrder(fromLayout)
            dialogAmendGtcOrder?.setOnDialogDismissListener(object : OnDialogDismissListener {
                override fun onDialogDismissed() {
                    dialogAmendGtcOrder = null
                }

                override fun onDialogCanceled() {
                    dialogAmendGtcOrder = null
                }
            })
            dialogAmendGtcOrder?.isCancelable = false
            dialogAmendGtcOrder?.show(fragmentManager, null)
        }
    }

    override fun showDialogTncEipoOrderBottom(
        fragmentManager: FragmentManager,
    ) {
        if (dialogInfoTncEipoOrder == null) {
            dialogInfoTncEipoOrder = DialogTncEipoOrder()
            dialogInfoTncEipoOrder?.setOnDialogDismissListener(object : OnDialogDismissListener{
                override fun onDialogDismissed() {
                    dialogInfoTncEipoOrder = null
                }

                override fun onDialogCanceled() {
                    dialogInfoTncEipoOrder = null
                }
            })
            dialogInfoTncEipoOrder?.isCancelable = false
            dialogInfoTncEipoOrder?.show(fragmentManager, null)
        }
    }

    override fun showDialogSearchStock(
        fragmentManager: FragmentManager,
        selectedStock: List<String>,
        onSelectedStock: ((Boolean, String) -> Unit)?
    ) {
        if (dialogSearchStock == null) {
            dialogSearchStock = SearchStockDialog(selectedStock)

            dialogSearchStock?.setOnSelectedStock { isSuccess, stockCode  ->
                onSelectedStock?.invoke(isSuccess, stockCode)
            }

            dialogSearchStock?.setOnDialogDismissListener(object : OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogSearchStock?.setOnSelectedStock { isSuccess, stockCode ->
                        onSelectedStock?.invoke(isSuccess, stockCode)
                    }
                    dialogSearchStock = null

                }

                override fun onDialogDismissed() {
                    dialogSearchStock?.setOnSelectedStock { isSuccess, stockCode ->
                        onSelectedStock?.invoke(isSuccess, stockCode)
                    }
                    dialogSearchStock = null

                }
            })
            dialogSearchStock?.show(fragmentManager, null)
        }
    }

    override fun showDialogCoachmarkNoWatchlist(fragmentManager: FragmentManager) {
        if (dialogCoachmarkNoWatchlist == null) {
            dialogCoachmarkNoWatchlist = CoachmarkNoWatchlistDialog()

            dialogCoachmarkNoWatchlist?.setOnDialogDismissListener(object : OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogCoachmarkNoWatchlist = null

                }

                override fun onDialogDismissed() {
                    dialogCoachmarkNoWatchlist = null

                }
            })
            dialogCoachmarkNoWatchlist?.show(fragmentManager, null)
        }
    }

    override fun showDialogCoachmarkWatchlist(fragmentManager: FragmentManager) {
        if (dialogCoachmarkWatchlist == null) {
            dialogCoachmarkWatchlist = CoachmarkWatchlistDialog()

            dialogCoachmarkWatchlist?.setOnDialogDismissListener(object : OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogCoachmarkWatchlist = null

                }

                override fun onDialogDismissed() {
                    dialogCoachmarkWatchlist = null

                }
            })
            dialogCoachmarkWatchlist?.show(fragmentManager, null)
        }
    }

    override fun showDialogCoachmarkFastOrder(fragmentManager: FragmentManager) {
        if (dialogCoachmarkFastOrder == null) {
            dialogCoachmarkFastOrder = CoachmarkFastOrderDialog()

            dialogCoachmarkFastOrder?.setOnDialogDismissListener(object : OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogCoachmarkFastOrder = null

                }

                override fun onDialogDismissed() {
                    dialogCoachmarkFastOrder = null

                }
            })
            dialogCoachmarkFastOrder?.show(fragmentManager, null)
        }
    }

    override fun showDialogCoachmarkPortfolio(fragmentManager: FragmentManager) {
        if (dialogCoachmarkPortfolio == null) {
            dialogCoachmarkPortfolio = CoachmarkPortfolioDialog()

            dialogCoachmarkPortfolio?.setOnDialogDismissListener(object : OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogCoachmarkPortfolio = null

                }

                override fun onDialogDismissed() {
                    dialogCoachmarkPortfolio = null

                }
            })
            dialogCoachmarkPortfolio?.show(fragmentManager, null)
        }
    }

    override fun showDialogRealizeGainLossInfoBottom(fragmentManager: FragmentManager) {
        if (dialogRealizedGainLossInfo == null) {
            dialogRealizedGainLossInfo = DialogInfoRealizedGainLoss()

            dialogRealizedGainLossInfo?.setOnDialogDismissListener(object : OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogRealizedGainLossInfo = null

                }

                override fun onDialogDismissed() {
                    dialogRealizedGainLossInfo = null

                }
            })
            dialogRealizedGainLossInfo?.isCancelable = false
            dialogRealizedGainLossInfo?.show(fragmentManager, null)
        }
    }

    override fun showDialogRemoveTrustedDevice(
        fromLayout: String,
        fragmentManager: FragmentManager,
    ) {
        if (dialogRemoveTrustedDevice == null) {
            dialogRemoveTrustedDevice = DialogRemoveTrustedDevice(fromLayout)
            dialogRemoveTrustedDevice?.setOnDialogDismissListener(object :
                OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogRemoveTrustedDevice = null
                }

                override fun onDialogDismissed() {
                    dialogRemoveTrustedDevice = null
                }
            })
            dialogRemoveTrustedDevice?.isCancelable = false
            dialogRemoveTrustedDevice?.show(fragmentManager, null)
        }
    }

    override fun showDialogInfoManyRequestOtp(fragmentManager: FragmentManager) {
        if (dialogInfoManyRequestOtp == null) {
            dialogInfoManyRequestOtp = DialogInfoManyRequestOtp()
            dialogInfoManyRequestOtp?.setOnDialogDismissListener(object :
                OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogInfoManyRequestOtp = null
                }

                override fun onDialogDismissed() {
                    dialogInfoManyRequestOtp = null
                }
            })
            dialogInfoManyRequestOtp?.isCancelable = false
            dialogInfoManyRequestOtp?.show(fragmentManager, null)
        }
    }

    override fun showDialogInfoManyDevicesOtp(fragmentManager: FragmentManager) {
        if (dialogInfoManyDevicesOtp == null) {
            dialogInfoManyDevicesOtp = DialogInfoManyDevicesOtp()
            dialogInfoManyDevicesOtp?.setOnDialogDismissListener(object :
                OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogInfoManyDevicesOtp = null
                }

                override fun onDialogDismissed() {
                    dialogInfoManyDevicesOtp = null
                }
            })
            dialogInfoManyDevicesOtp?.isCancelable = false
            dialogInfoManyDevicesOtp?.show(fragmentManager, null)
        }
    }

    override fun showDialogExerciseNotAvailable(fragmentManager: FragmentManager, startHour: String) {
        if (dialogExerciseNotAvailable == null) {
            dialogExerciseNotAvailable = DialogExerciseNotAvailableBottom(startHour)

            dialogExerciseNotAvailable?.setOnDialogDismissListener(object : OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogExerciseNotAvailable = null

                }

                override fun onDialogDismissed() {
                    dialogExerciseNotAvailable = null

                }
            })
            dialogExerciseNotAvailable?.isCancelable = false
            dialogExerciseNotAvailable?.show(fragmentManager, null)
        }
    }
}