/*
package com.bcasekuritas.mybest.ext.common

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.ViewGroup
import android.view.Window
import android.widget.DatePicker
import android.widget.EditText
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.github.chrisbanes.photoview.PhotoView
import com.bcasekuritas.mybest.R
import java.util.*

*/
/** Global Variable **//*

var dialogConfirmBottom         : DialogConfirmBottom? = null
var dialogConfirmCenter         : DialogConfirmationCenter? = null
var dialogMinimConfirmation     : DialogMinimConfirmation? = null

var dialogInfoCenter            : DialogInfoCenter? = null
var dialogInfoBottom            : DialogInfoBottom? = null

//var dialogListSearch            : DialogListSearch? = null
//var dialogListSearchQuery       : DialogListSearchQuery? = null

*/
/** Function **//*

fun showDialogNetworkErrorSheet(
    isCallback: Boolean, isCancelable: Boolean, fragmentManager: FragmentManager,
    resTargetCode: String?, failure: FailureData
) {
    val uiDialogModel = getErrorDetail(failure)
    val dialog = DialogInfoBottom.newInstance(uiDialogModel)
    dialog.isCancelable = isCancelable
    if (isCallback) {
        dialog.show(fragmentManager, resTargetCode)
    } else {
        dialog.show(fragmentManager, null)
    }
}

fun showDialogDatePicker(
    context: Context, editText: EditText
) {
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        Objects.requireNonNull(context),
        R.style.DatePickerStyle,
        { _: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
            val newDate = Calendar.getInstance()
            newDate[year, monthOfYear] = dayOfMonth
            editText.setText(
                newDate.time.getStringFormatDate(Const.LOCAL_DATE_FORMAT)
            )
        },
        calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH]
    )
    datePickerDialog.show()
}

fun showDialogConfirmBottom(
    isCallback: Boolean, isCancelable: Boolean, fragmentManager: FragmentManager,
    codeFromLayout: Int?, paramValue: Int?, reqTargetCode: String?,
    resTargetCode: String?, uiDialogModel: UIDialogModel
) {
    if (dialogConfirmBottom == null) {
        dialogConfirmBottom = DialogConfirmBottom.newInstance(reqTargetCode, codeFromLayout, paramValue, uiDialogModel)
        dialogConfirmBottom!!.onDismiss(object : DialogInterface {
            override fun cancel() { dialogConfirmBottom = null }
            override fun dismiss() { dialogConfirmBottom = null }
        })
        dialogConfirmBottom!!.isCancelable = isCancelable
        if (isCallback) {
            dialogConfirmBottom!!.show(fragmentManager, resTargetCode)
        } else {
            dialogConfirmBottom!!.show(fragmentManager, null)
        }
    }
}

fun showDialogConfirmCenter(
    isCallback: Boolean, isCancelable: Boolean, fragmentManager: FragmentManager,
    codeFromLayout: Int?, paramValue: Int?, reqTargetCode: String?,
    resTargetCode: String?
) {
    if (dialogConfirmCenter == null) {
        dialogConfirmCenter = DialogConfirmationCenter.newInstance()
        dialogConfirmCenter!!.onDismiss(object : DialogInterface {
            override fun cancel() { dialogConfirmCenter = null }
            override fun dismiss() { dialogConfirmCenter = null }
        })
        dialogConfirmCenter!!.isCancelable = isCancelable
        if (isCallback) {
            dialogConfirmCenter!!.show(fragmentManager, resTargetCode)
        } else {
            dialogConfirmCenter!!.show(fragmentManager, null)
        }
    }
}

fun showDialogConfirmMinimCenter(
    isCallback: Boolean, isCancelable: Boolean, fragmentManager: FragmentManager,
    codeFromLayout: Int?, paramValue: Int?, reqTargetCode: String?,
    resTargetCode: String?, uiDialogModel: UIDialogModel
) {
    if (dialogMinimConfirmation == null) {
        dialogMinimConfirmation = DialogMinimConfirmation.newInstance(reqTargetCode, codeFromLayout, paramValue, uiDialogModel)
        dialogMinimConfirmation!!.onDismiss(object : DialogInterface {
            override fun cancel() { dialogMinimConfirmation = null }
            override fun dismiss() { dialogMinimConfirmation = null }
        })
        dialogMinimConfirmation!!.isCancelable = isCancelable
        if (isCallback) {
            dialogMinimConfirmation!!.show(fragmentManager, resTargetCode)
        } else {
            dialogMinimConfirmation!!.show(fragmentManager, null)
        }
    }
}

fun showDialogInfoCenter(
    isCallback: Boolean, isCancelable: Boolean, fragmentManager: FragmentManager,
    codeFromLayout: Int?, paramValue: Int?, reqTargetCode: String?,
    resTargetCode: String?, uiDialogModel: UIDialogModel
) {
    if (dialogInfoCenter == null) {
        dialogInfoCenter = DialogInfoCenter.newInstance(reqTargetCode, codeFromLayout, paramValue, uiDialogModel)
        dialogInfoCenter!!.onDismiss(object : DialogInterface {
            override fun cancel() { dialogInfoCenter = null }
            override fun dismiss() { dialogInfoCenter = null }
        })
        dialogInfoCenter!!.isCancelable = isCancelable
        if (isCallback) {
            dialogInfoCenter!!.show(fragmentManager, resTargetCode)
        } else {
            dialogInfoCenter!!.show(fragmentManager, null)
        }
    }
}

fun showDialogInfoBottom(
    isCallback: Boolean, isCancelable: Boolean, fragmentManager: FragmentManager,
    codeFromLayout: Int?, reqTargetCode: String?, resTargetCode: String?,
    uiDialogModel: UIDialogModel
) {
    if (dialogInfoBottom == null) {
        dialogInfoBottom = DialogInfoBottom.newInstance(reqTargetCode, codeFromLayout, uiDialogModel)
        dialogInfoBottom!!.onDismiss(object : DialogInterface {
            override fun cancel() { dialogInfoBottom = null }
            override fun dismiss() { dialogInfoBottom = null }
        })
        dialogInfoBottom!!.isCancelable = isCancelable
        if (isCallback) {
            dialogInfoBottom!!.show(fragmentManager, resTargetCode)
        } else {
            dialogInfoBottom!!.show(fragmentManager, null)
        }
    }
}

fun showDialogListSearch(
    fragmentManager: FragmentManager, codeFromLayout: Int?, paramValue: Int?,
    reqTargetCode: String?, resTargetCode: String?
) {
//    if (dialogListSearch == null) {
//        dialogListSearch = DialogListSearch.newInstance(reqTargetCode, codeFromLayout, paramValue)
//        dialogListSearch!!.onDismiss(object : DialogInterface {
//            override fun cancel() { dialogListSearch = null }
//            override fun dismiss() { dialogListSearch = null }
//        })
//        dialogListSearch!!.show(fragmentManager, resTargetCode)
//    }
}

fun showDialogListSearchQuery(
    fragmentManager: FragmentManager, codeFromLayout: Int?, paramValue: Int?,
    reqTargetCode: String?, resTargetCode: String?
) {
//    if (dialogListSearchQuery == null) {
//        dialogListSearchQuery = DialogListSearchQuery.newInstance(reqTargetCode, codeFromLayout, paramValue)
//        dialogListSearchQuery!!.onDismiss(object : DialogInterface {
//            override fun cancel() { dialogListSearchQuery = null }
//            override fun dismiss() { dialogListSearchQuery = null }
//        })
//        dialogListSearchQuery!!.show(fragmentManager, resTargetCode)
//    }
}

fun showImageDialog (imageUri: String, context: Context, fileName: String) {
    val imageView = PhotoView(context)
    val dialog = Dialog(context)
    imageView.setOnOutsidePhotoTapListener { dialog.dismiss() }
    try {
        Glide.with(context)
            .asBitmap()
            .load(imageUri)
            .into(object : CustomTarget<Bitmap?>() {
                override fun onLoadCleared(placeholder: Drawable?) {}
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap?>?) {
                    saveImage(context, resource, fileName)
                    imageView.setImageBitmap(resource)
                }
            })
    } catch (e: Exception) {
        Glide.with(context).load(imageUri).into(imageView)
    }

    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setOnDismissListener { obj: DialogInterface -> obj.dismiss() }
    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.setContentView(imageView)
    dialog.show()
    dialog.window!!.setLayout(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    )
}
*/
