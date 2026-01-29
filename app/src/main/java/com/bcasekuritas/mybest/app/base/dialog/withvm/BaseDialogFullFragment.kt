package com.bcasekuritas.mybest.app.base.dialog.withvm

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import com.google.android.material.snackbar.Snackbar
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.viewmodel.BaseViewModel
import com.bcasekuritas.mybest.ext.listener.OnDialogDismissListener
import com.bcasekuritas.mybest.ext.prefs.SharedPreferenceManager
import com.bcasekuritas.mybest.widget.progressdialog.ProgressDialogHelper
import timber.log.Timber
import javax.inject.Inject

abstract class BaseDialogFullFragment<VB : ViewDataBinding, VM : BaseViewModel> :
    DialogFragment() {

    abstract val viewModel: VM
    abstract val binding: VB
    abstract val bindingVariable: Int
    private var dismissListener: OnDialogDismissListener? = null

    //
    @Inject lateinit var prefManager: SharedPreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
    }

    override fun getTheme(): Int {
        return R.style.FullScreenDialogStyle
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

    fun hideKeyboard() {
        try {
            val view = requireActivity().currentFocus
            if (view != null) {
                val imm =
                    (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?)
                imm!!.hideSoftInputFromWindow(view.windowToken, 0)
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    private fun showSnackBar(message: String?, spanned: Spanned?, color: String?) {
        var snackbar = Snackbar.make(requireContext(), this.requireView(), getString(R.string.default_error_message), Snackbar.LENGTH_SHORT)
        message?.let { snackbar = Snackbar.make(requireContext(), this.requireView(), it, Snackbar.LENGTH_SHORT) }
        spanned?.let { snackbar = Snackbar.make(requireContext(), this.requireView(), it, Snackbar.LENGTH_SHORT) }
        color?.let { snackbar.view.setBackgroundColor(Color.parseColor(it)) }
        snackbar.show()
    }

    fun setOnDialogDismissListener(listener: OnDialogDismissListener){
        dismissListener = listener
    }
}