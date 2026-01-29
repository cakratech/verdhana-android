package com.bcasekuritas.mybest.app.feature.dialog.share

import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.dialog.withoutvm.BaseBottomSheet
import com.bcasekuritas.mybest.app.data.layout.UIDialogShareModel
import com.bcasekuritas.mybest.databinding.FragmentDialogSharePortfolioBinding
import com.bcasekuritas.mybest.databinding.FragmentDialogSharePortfolioDetailBinding
import com.bcasekuritas.mybest.ext.activity.autoCleaned
import com.bcasekuritas.mybest.ext.converter.toBitmap
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import java.io.File

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@FragmentScoped
@AndroidEntryPoint
class DialogSharePortfolioDetailFragment(
    private val stockCode: String,
    private val currPrice: String,
    private val avgPrice: String,
    private val portfolioReturn: String,
    private val isProfit: Boolean
) : BaseBottomSheet<FragmentDialogSharePortfolioDetailBinding>() {

    @FragmentScoped
    override val binding: FragmentDialogSharePortfolioDetailBinding by autoCleaned { (FragmentDialogSharePortfolioDetailBinding.inflate(layoutInflater)) }

    companion object {
        private const val MORE_TAG = "MORE_TAG"
    }

    private val availableApps: MutableList<UIDialogShareModel> = ArrayList()
    private lateinit var sendIntent: Intent
    private val adapter = DialogShareAdapter { item -> onItemClicked(item) }
    private lateinit var imageShareBitmap: Bitmap

    override fun onPause() {
        super.onPause()
        dismiss()
    }

    override fun setupComponent() {
        super.setupComponent()

        loadData()
        setupRecycler()
        setBehavior()
    }

    override fun initOnClick() {
        super.initOnClick()
        binding.apply {
            ivClose.setOnClickListener {
                dismiss()
            }
        }
    }

    private fun setupRecycler() {

        binding.baseShare.background = if (isProfit){
            ContextCompat.getDrawable(requireContext(), R.drawable.bg_share_portfolio_up)
        } else {
            ContextCompat.getDrawable(requireContext(), R.drawable.bg_share_portfolio_down)

        }

        binding.tvReturnVal.setTextColor(
            ContextCompat.getColor(requireContext(), if (isProfit) R.color.textUpHeader else R.color.textDownHeader)
        )
        binding.tvReturnVal.text = when {
            portfolioReturn.length >= 12 -> portfolioReturn.take(6) + "..." + "%"
            portfolioReturn.length >= 11 -> portfolioReturn.dropLast(4) + "%"
            else -> portfolioReturn
        }

        binding.tvCurrentPriceVal.setCompoundDrawablesWithIntrinsicBounds(
            ContextCompat.getDrawable(requireContext(), if (isProfit) R.drawable.ic_arrow_diagonal_up else R.drawable.ic_arrow_diagonal_down),
            null,
            null,
            null
        )


        binding.tvStockcode.text = stockCode
        binding.tvCurrentPriceVal.text = currPrice
        binding.tvAveragePriceVal.text = avgPrice

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        binding.recyclerView.adapter = adapter
    }

    private fun setBehavior() {
        val dialog = requireView().parent as View
        dialog.setBackgroundColor(Color.TRANSPARENT)
        val behavior = BottomSheetBehavior.from(dialog)
        behavior.apply {
            state = BottomSheetBehavior.STATE_EXPANDED
            skipCollapsed = true
            behavior.isDraggable = true
        }
    }

    private fun isDesiredApp(appName: String): Boolean {
        return appName.contains("line", ignoreCase = true) ||
                appName.contains("whatsapp", ignoreCase = true) ||
                appName.contains("telegram", ignoreCase = true) ||
                appName.contains("facebook", ignoreCase = true) ||
                appName.contains("message", ignoreCase = true)
    }

    private fun loadData() {

//        imageShareBitmap = convertViewToImage(listOf(binding.baseShare, binding.tvReturn))
        binding.vwGroupPicture.toBitmap{
            imageShareBitmap = it

            val uri = saveBitmapToCache(requireContext(), imageShareBitmap)
            val profit = if (isProfit) {
                "up"
            } else {
                "down"
            }
            val txtCaption = "My return in $stockCode is $profit by $portfolioReturn. \n" +
                    "\n" +
                    "Download BCA Sekuritas Mobile to start your trading experience. \n" +
                    "- Andro: https://bit.ly/bcas-android" +
                    "\n" +
                    "- Ios: https://bit.ly/bcas-ios"

            val activity = requireActivity()
            sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_TEXT, txtCaption) // Caption text
            }

            val activities: List<ResolveInfo> = activity.packageManager.queryIntentActivities(
                sendIntent, 0
            )

            for (info in activities) {
                if (isDesiredApp(info.activityInfo.packageName)) {
                    availableApps.add(
                        UIDialogShareModel(
                            info.loadLabel(activity.packageManager).toString(),
                            info.loadIcon(activity.packageManager),
                            info.activityInfo.packageName
                        )
                    )
                }
            }

            val moreIcon = ResourcesCompat.getDrawable(
                resources,
                R.drawable.shape_action_more,
                null
            )

            moreIcon?.let { drawable ->
                availableApps.add(
                    UIDialogShareModel(
                        "More",
                        drawable,
                        MORE_TAG
                    )
                )
            }
            adapter.addItems(availableApps)
        }
    }

    private fun onItemClicked(itemEntity: UIDialogShareModel) {
        if (itemEntity.packageName == MORE_TAG) {
            val shareIntent = Intent.createChooser(
                sendIntent,
                "Share via"
            )
            ContextCompat.startActivity(requireContext(), shareIntent, null)
        } else {
            sendIntent.setPackage(itemEntity.packageName)
            startActivity(sendIntent)
        }
        dismiss()
    }

    fun convertViewToImage(list: List<View>) : Bitmap {
        return FrameLayout(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setBackgroundColor(Color.WHITE) // Optional: To make sure background is visible
            list.forEach {
                (it.parent as? ViewGroup)?.removeView(it)
                addView(it)
            }
        }.toBitmap()
    }

    private fun saveBitmapToCache(context: Context, bitmap: Bitmap): Uri? {
        val file = File(context.cacheDir, "shared_image.png")
        file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }
}
