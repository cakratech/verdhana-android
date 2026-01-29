package com.bcasekuritas.mybest.widget.progressdialog

import android.app.ProgressDialog
import android.content.Context
import com.bcasekuritas.mybest.R

class ProgressDialogHelper {

    private var progressDialog: ProgressDialog? = null

    fun show(context: Context?, messageResourceId: String?) {
        try {
            if (progressDialog != null) {
                progressDialog!!.dismiss()
            }
            val style: Int = R.style.dialogTransparentTheme
            progressDialog = ProgressDialog(context, style)
            progressDialog!!.setContentView(R.layout.progress_dialog)
            progressDialog!!.setMessage(messageResourceId)
            progressDialog!!.isIndeterminate = false
            progressDialog!!.setCancelable(false)
            progressDialog!!.setCanceledOnTouchOutside(false)
            progressDialog!!.show()
        } catch (ignore: Exception) {}
    }

    fun dismiss() {
        try {
            if (progressDialog?.isShowing == true) {
                progressDialog?.dismiss()
                progressDialog = null
            }
        } catch (ignore: Exception) {}
    }

}