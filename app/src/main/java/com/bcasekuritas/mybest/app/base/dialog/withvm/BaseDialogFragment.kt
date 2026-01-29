package com.bcasekuritas.mybest.app.base.dialog.withvm

import android.content.DialogInterface
import android.R as RAndroid
import com.bcasekuritas.mybest.R as RBcas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import com.google.android.material.snackbar.Snackbar
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.ext.listener.OnDialogDismissListener

abstract class BaseDialogFragment<VB : ViewDataBinding, VM : BaseViewModel> :
    DialogFragment() {

    abstract val viewModel: VM
    abstract val binding: VB
    abstract val bindingVariable: Int
    private var dismissListener: OnDialogDismissListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
    }

    override fun onStart() {
        super.onStart()
        viewModel.showToast.observe(
            this) { Toast.makeText(activity, it, Toast.LENGTH_LONG).show() }
        viewModel.showSnack.observe(
            this) { showSnackBar(it.message, it.spanned, it.color) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setStyle(STYLE_NO_FRAME, RAndroid.style.Theme)
        return binding.root
    }

    private fun performDataBinding(){
        binding.setVariable(bindingVariable, viewModel)
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


    private fun showSnackBar(message: String?, spanned: Spanned?, color: String?) {
        var snackbar = Snackbar.make(requireContext(), this.requireView(), getString(RBcas.string.default_error_message), Snackbar.LENGTH_SHORT)
        message?.let { snackbar = Snackbar.make(requireContext(), this.requireView(), it, Snackbar.LENGTH_SHORT) }
        spanned?.let { snackbar = Snackbar.make(requireContext(), this.requireView(), it, Snackbar.LENGTH_SHORT) }
        color?.let { snackbar.view.setBackgroundColor(Color.parseColor(it)) }
        snackbar.show()
    }

    fun setOnDialogDismissListener(listener: OnDialogDismissListener){
        dismissListener = listener
    }
}