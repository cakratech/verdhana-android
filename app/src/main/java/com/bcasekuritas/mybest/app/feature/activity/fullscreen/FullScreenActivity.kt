package com.bcasekuritas.mybest.app.feature.activity.fullscreen

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import dagger.hilt.android.AndroidEntryPoint
import com.bcasekuritas.mybest.BR
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.base.BaseActivity
import com.bcasekuritas.mybest.databinding.ActivityFullScreenBinding
import com.bcasekuritas.mybest.ext.constant.NavKeys

@AndroidEntryPoint
class FullScreenActivity: BaseActivity<ActivityFullScreenBinding, FullScreenViewmodel>() {

    override val bindingVariable: Int = BR.vmActFullScreen
    override val viewModel: FullScreenViewmodel by viewModels()
    override val binding: ActivityFullScreenBinding by lazy { ActivityFullScreenBinding.inflate(layoutInflater) }

    //
    private var activeNavigation: Int = 0

    companion object {
        fun startIntent(activity: Activity, keyNavigate: String) {
            val starter = Intent(activity, FullScreenActivity::class.java)
                .putExtra(NavKeys.KEY_NAV_FULL_SCREEN, keyNavigate)
            activity.startActivity(starter)
        }

        fun startIntentWithFinish(activity: Activity, keyNavigate: String) {
            val starter = Intent(activity, FullScreenActivity::class.java)
                .putExtra(NavKeys.KEY_NAV_FULL_SCREEN, keyNavigate)
            activity.startActivity(starter)
            activity.finish()
        }
    }

    init {
//        setTheme(R.style.AppTheme_Transparent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.FullScreenTheme)
        super.onCreate(savedInstanceState)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val navBarInset = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom

            // Tambahkan padding agar tidak ketumpuk sistem UI
            view.setPadding(0, 0, 0, navBarInset)

            insets
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun setupComponent() {
        super.setupComponent()

    }

    private fun changeScreenSystemUiController(isFullScreen: Boolean) {
        window?.also {
            WindowCompat.setDecorFitsSystemWindows(it, !isFullScreen)
            WindowCompat.getInsetsController(it, it.decorView).apply {
                systemBarsBehavior =
                    if (isFullScreen)
                        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                    else
                        WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
                if (isFullScreen)
                    hide(WindowInsetsCompat.Type.systemBars())
                else
                    show(WindowInsetsCompat.Type.systemBars())
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                it.attributes.layoutInDisplayCutoutMode =
                    if (isFullScreen)
                        WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                    else
                        WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT
            }
        }
    }

    override fun onInitViews() {
        setContentView(binding.root)
    }

    override fun setupArguments() {
        super.setupArguments()
//        navigationMain.navigateHome()
    }

}