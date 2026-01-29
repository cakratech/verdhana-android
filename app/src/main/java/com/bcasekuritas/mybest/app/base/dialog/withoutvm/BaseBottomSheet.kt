package com.bcasekuritas.mybest.app.base.dialog.withoutvm

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.ext.listener.OnDialogDismissListener
import com.bcasekuritas.mybest.ext.prefs.SharedPreferenceManager
import javax.inject.Inject

abstract class BaseBottomSheet<VB : ViewDataBinding> :
    BottomSheetDialogFragment() {

    abstract val binding: VB
    private var dismissListener: OnDialogDismissListener? = null

    @Inject
    lateinit var prefManager: SharedPreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
    }

    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    private fun performDataBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.executePendingBindings()
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        performDataBinding()
        setupArguments()
        setupAdapter()
        setupViewPager()
        setupComponent()

        setupListener()
        setupObserver()

        initAPI()
        initOnClick()
    }

    protected open fun setupArguments(){}
    protected open fun setupComponent(){}
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