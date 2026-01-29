package com.bcasekuritas.mybest.ext.delegate

import androidx.fragment.app.FragmentManager
import com.bcasekuritas.mybest.app.domain.dto.response.CalEvent
import com.bcasekuritas.mybest.app.feature.dialog.bottom.calendar.DialogCalendarBonus
import com.bcasekuritas.mybest.app.feature.dialog.bottom.calendar.DialogCalendarDividend
import com.bcasekuritas.mybest.app.feature.dialog.bottom.calendar.DialogCalendarIPO
import com.bcasekuritas.mybest.app.feature.dialog.bottom.calendar.DialogCalendarPublic
import com.bcasekuritas.mybest.app.feature.dialog.bottom.calendar.DialogCalendarReverseSplit
import com.bcasekuritas.mybest.app.feature.dialog.bottom.calendar.DialogCalendarRightIssue
import com.bcasekuritas.mybest.app.feature.dialog.bottom.calendar.DialogCalendarRups
import com.bcasekuritas.mybest.app.feature.dialog.bottom.calendar.DialogCalendarStockSplit
import com.bcasekuritas.mybest.app.feature.dialog.bottom.calendar.DialogCalendarWarrant
import com.bcasekuritas.mybest.ext.listener.OnDialogDismissListener


private var dialogCalendarDividend           : DialogCalendarDividend? = null
private var dialogCalendarRups               : DialogCalendarRups? = null
private var dialogCalendarRightIssue         : DialogCalendarRightIssue? = null
private var dialogCalendarBonus              : DialogCalendarBonus? = null
private var dialogCalendarReverseSplit       : DialogCalendarReverseSplit? = null
private var dialogCalendarIPO                : DialogCalendarIPO? = null
private var dialogCalendarPublic             : DialogCalendarPublic? = null
private var dialogCalendarStockSplit         : DialogCalendarStockSplit? = null
private var dialogCalendarWarrant            : DialogCalendarWarrant? = null

interface ShowDialogCalendarDetailInterface {

    /** Dialog Info */
    fun showDialogCalendarDividend(fragmentManager: FragmentManager, calEvent: CalEvent, onClicked: ((String) -> Unit))
    fun showDialogCalendarRups(fragmentManager: FragmentManager, calEvent: CalEvent, onClicked: ((Boolean) -> Unit))
    fun showDialogCalendarIpo(fragmentManager: FragmentManager, calEvent: CalEvent, onClicked: ((Boolean) -> Unit))
    fun showDialogCalendarRightIssue(fragmentManager: FragmentManager, calEvent: CalEvent, onClicked: ((Boolean) -> Unit))
    fun showDialogCalendarBonus(fragmentManager: FragmentManager, calEvent: CalEvent, onClicked: ((Boolean) -> Unit))
    fun showDialogCalendarPublic(fragmentManager: FragmentManager, calEvent: CalEvent, onClicked: ((Boolean) -> Unit))
    fun showDialogCalendarStockSplit(fragmentManager: FragmentManager, calEvent: CalEvent, onClicked: ((Boolean) -> Unit))
    fun showDialogCalendarWarrant(fragmentManager: FragmentManager, calEvent: CalEvent, onClicked: ((Boolean) -> Unit))
    fun showDialogCalendarReverseSplit(fragmentManager: FragmentManager, calEvent: CalEvent, onClicked: ((Boolean) -> Unit))
}
class ShowDialogCalendarDetailImpl: ShowDialogCalendarDetailInterface {

    override fun showDialogCalendarDividend(
        fragmentManager: FragmentManager,
        calEvent: CalEvent,
        onClicked: (String) -> Unit,
    ) {
        if (dialogCalendarDividend == null) {
            dialogCalendarDividend = DialogCalendarDividend(calEvent)

            dialogCalendarDividend?.setOkButtonClickListener { string ->
                onClicked.invoke(string)
                dialogCalendarDividend = null
            }

            dialogCalendarDividend?.setOnDialogDismissListener(object : OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogCalendarDividend = null
                }

                override fun onDialogDismissed() {
                    dialogCalendarDividend = null
                }
            })

            dialogCalendarDividend?.show(fragmentManager, null)
        }
    }

    override fun showDialogCalendarRups(
        fragmentManager: FragmentManager,
        calEvent: CalEvent,
        onClicked: (Boolean) -> Unit,
    ) {

        if (dialogCalendarRups == null) {
            dialogCalendarRups = DialogCalendarRups(calEvent)

            dialogCalendarRups?.setOkButtonClickListener { boolean ->
                onClicked.invoke(boolean)
                dialogCalendarRups = null
            }

            dialogCalendarRups?.setOnDialogDismissListener(object : OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogCalendarRups = null
                }

                override fun onDialogDismissed() {
                    dialogCalendarRups = null
                }
            })

            dialogCalendarRups?.show(fragmentManager, null)
        }
    }

    override fun showDialogCalendarIpo(
        fragmentManager: FragmentManager,
        calEvent: CalEvent,
        onClicked: (Boolean) -> Unit,
    ) {

        if (dialogCalendarIPO == null) {
            dialogCalendarIPO = DialogCalendarIPO(calEvent)

            dialogCalendarIPO?.setOkButtonClickListener { boolean ->
                onClicked.invoke(boolean)
                dialogCalendarIPO = null
            }

            dialogCalendarIPO?.setOnDialogDismissListener(object : OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogCalendarIPO = null
                }

                override fun onDialogDismissed() {
                    dialogCalendarIPO = null
                }
            })

            dialogCalendarIPO?.show(fragmentManager, null)
        }
    }

    override fun showDialogCalendarRightIssue(
        fragmentManager: FragmentManager,
        calEvent: CalEvent,
        onClicked: (Boolean) -> Unit,
    ) {

        if (dialogCalendarRightIssue == null) {
            dialogCalendarRightIssue = DialogCalendarRightIssue(calEvent)

            dialogCalendarRightIssue?.setOkButtonClickListener { boolean ->
                onClicked.invoke(boolean)
                dialogCalendarRightIssue = null
            }

            dialogCalendarRightIssue?.setOnDialogDismissListener(object : OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogCalendarRightIssue = null
                }

                override fun onDialogDismissed() {
                    dialogCalendarRightIssue = null
                }
            })

            dialogCalendarRightIssue?.show(fragmentManager, null)
        }
    }

    override fun showDialogCalendarBonus(
        fragmentManager: FragmentManager,
        calEvent: CalEvent,
        onClicked: (Boolean) -> Unit,
    ) {

        if (dialogCalendarBonus == null) {
            dialogCalendarBonus = DialogCalendarBonus(calEvent)

            dialogCalendarBonus?.setOkButtonClickListener { boolean ->
                onClicked.invoke(boolean)
                dialogCalendarBonus = null
            }

            dialogCalendarBonus?.setOnDialogDismissListener(object : OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogCalendarBonus = null
                }

                override fun onDialogDismissed() {
                    dialogCalendarBonus = null
                }
            })

            dialogCalendarBonus?.show(fragmentManager, null)
        }
    }

    override fun showDialogCalendarPublic(
        fragmentManager: FragmentManager,
        calEvent: CalEvent,
        onClicked: (Boolean) -> Unit,
    ) {

        if (dialogCalendarPublic == null) {
            dialogCalendarPublic = DialogCalendarPublic(calEvent)

            dialogCalendarPublic?.setOkButtonClickListener { boolean ->
                onClicked.invoke(boolean)
                dialogCalendarPublic = null
            }

            dialogCalendarPublic?.setOnDialogDismissListener(object : OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogCalendarPublic = null
                }

                override fun onDialogDismissed() {
                    dialogCalendarPublic = null
                }
            })

            dialogCalendarPublic?.show(fragmentManager, null)
        }
    }

    override fun showDialogCalendarStockSplit(
        fragmentManager: FragmentManager,
        calEvent: CalEvent,
        onClicked: (Boolean) -> Unit,
    ) {

        if (dialogCalendarStockSplit == null) {
            dialogCalendarStockSplit = DialogCalendarStockSplit(calEvent)

            dialogCalendarStockSplit?.setOkButtonClickListener { boolean ->
                onClicked.invoke(boolean)
                dialogCalendarStockSplit = null
            }

            dialogCalendarStockSplit?.setOnDialogDismissListener(object : OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogCalendarStockSplit = null
                }

                override fun onDialogDismissed() {
                    dialogCalendarStockSplit = null
                }
            })

            dialogCalendarStockSplit?.show(fragmentManager, null)
        }
    }

    override fun showDialogCalendarWarrant(
        fragmentManager: FragmentManager,
        calEvent: CalEvent,
        onClicked: (Boolean) -> Unit,
    ) {

        if (dialogCalendarWarrant == null) {
            dialogCalendarWarrant = DialogCalendarWarrant(calEvent)

            dialogCalendarWarrant?.setOkButtonClickListener { boolean ->
                onClicked.invoke(boolean)
                dialogCalendarWarrant = null
            }

            dialogCalendarWarrant?.setOnDialogDismissListener(object : OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogCalendarWarrant = null
                }

                override fun onDialogDismissed() {
                    dialogCalendarWarrant = null
                }
            })

            dialogCalendarWarrant?.show(fragmentManager, null)
        }
    }

    override fun showDialogCalendarReverseSplit(
        fragmentManager: FragmentManager,
        calEvent: CalEvent,
        onClicked: (Boolean) -> Unit,
    ) {
        if (dialogCalendarReverseSplit == null) {
            dialogCalendarReverseSplit = DialogCalendarReverseSplit(calEvent)

            dialogCalendarReverseSplit?.setOkButtonClickListener { boolean ->
                onClicked.invoke(boolean)
                dialogCalendarReverseSplit = null
            }

            dialogCalendarReverseSplit?.setOnDialogDismissListener(object : OnDialogDismissListener {
                override fun onDialogCanceled() {
                    dialogCalendarReverseSplit = null
                }

                override fun onDialogDismissed() {
                    dialogCalendarReverseSplit = null
                }
            })

            dialogCalendarReverseSplit?.show(fragmentManager, null)
        }
    }
}