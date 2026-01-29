package com.bcasekuritas.mybest.app.feature.dialog.bottom

import android.content.Intent
import android.net.Uri
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.dialog.withoutvm.BaseBottomSheet
import com.bcasekuritas.mybest.databinding.DialogTermAndConditionEipoBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned

class DialogTncEipoOrder: BaseBottomSheet<DialogTermAndConditionEipoBinding>() {

    override val binding: DialogTermAndConditionEipoBinding by autoCleaned { (DialogTermAndConditionEipoBinding.inflate(layoutInflater)) }
    private val webView = CustomTabsIntent.Builder().build()

    override fun setupComponent() {
        super.setupComponent()
        binding.apply {
            // for number 8 desc B
            setLink()

            // for number seven
            setMailAddress()
        }
    }

    private fun setMailAddress() {
        val desc = getString(R.string.tnc_eipo_seven)
        val spannable = SpannableString(HtmlCompat.fromHtml(desc, HtmlCompat.FROM_HTML_MODE_LEGACY))

        val email = "e-ipo@bcasekuritas.co.id"
        val start = spannable.indexOf(email)

        if (start != -1) {
            spannable.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:$email")
                    }
                    widget.context.startActivity(intent)
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = ContextCompat.getColor(requireContext(), R.color.alwaysBlue) // Set your custom color here
                }
            }, start, start + email.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        binding.tvNumberSevenDesc.text = spannable
        binding.tvNumberSevenDesc.movementMethod = LinkMovementMethod.getInstance()
        binding.tvNumberSevenDesc.highlightColor = ContextCompat.getColor(requireContext(), R.color.alwaysBlue)
    }

    private fun setLink() {
        val desc = getString(R.string.tnc_eipo_eight_b)

        val spannable = SpannableString(HtmlCompat.fromHtml(desc, HtmlCompat.FROM_HTML_MODE_LEGACY))

        val url = "https://www.e-ipo.co.id/id"
        val startIndex = spannable.indexOf(url)

        if (startIndex != -1) {
            spannable.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    // Handle click
                    webView.launchUrl(requireContext(), Uri.parse(url))
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = ContextCompat.getColor(requireContext(), R.color.alwaysBlue) // Your custom link color
                }
            }, startIndex, startIndex + url.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        binding.tvNumberEightDescB.text = spannable
        binding.tvNumberEightDescB.movementMethod = LinkMovementMethod.getInstance()
        binding.tvNumberEightDescB.highlightColor = ContextCompat.getColor(requireContext(), R.color.alwaysBlue)
    }

    override fun initOnClick() {
        super.initOnClick()

        binding.apply {

            btnClose.setOnClickListener {
                dismiss()
            }
        }
    }
}