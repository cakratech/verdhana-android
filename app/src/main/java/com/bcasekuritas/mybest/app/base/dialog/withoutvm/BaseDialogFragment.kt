package com.bcasekuritas.mybest.app.base.dialog.withoutvm

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.ext.listener.OnDialogDismissListener

abstract class BaseDialogFragment<VB : ViewDataBinding> :
    DialogFragment() {

    abstract val binding: VB
    private var dismissListener: OnDialogDismissListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.window?.setBackgroundDrawableResource(R.drawable.bg_transparent_16)
        setStyle(STYLE_NO_FRAME, R.style.NewDialog)

        dialog!!.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)  // Enable dimming
        dialog!!.window?.setDimAmount(0.5f)                                 // Optional: Adjust dim amount
        dialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return binding.root
    }

    private fun performDataBinding(){
        binding.lifecycleOwner = viewLifecycleOwner
        binding.executePendingBindings()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        performDataBinding()
        setupArguments()
        setupComponent()
        setupAdapter()
        setupViewPager()

        setupListener()
        setupObserver()

        initAPI()
        initOnClick()
    }

    protected open fun setupComponent() {}
    protected open fun setupArguments(){}
    protected open fun setupAdapter(){}
    protected open fun setupViewPager(){}

    protected open fun setupListener(){}
    protected open fun setupObserver(){}

    protected open fun initAPI(){}
    protected open fun initOnClick(){}

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dismissListener?.onDialogDismissed()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        dismissListener?.onDialogCanceled()
    }
    fun setOnDialogDismissListener(listener: OnDialogDismissListener){
        dismissListener = listener
    }

}