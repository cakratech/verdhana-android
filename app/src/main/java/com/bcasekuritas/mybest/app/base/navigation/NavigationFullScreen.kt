package com.bcasekuritas.mybest.app.base.navigation

import android.app.Activity
import androidx.fragment.app.FragmentManager
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.feature.activity.fullscreen.FullScreenActivity
import com.bcasekuritas.mybest.app.feature.activity.main.MainActivity
import javax.inject.Inject

class NavigationFullScreen @Inject constructor(fullScreenActivity: Activity) {

    private var containerId: Int = R.id.f_full_screen_container
    private var fragmentManager: FragmentManager =
        (fullScreenActivity as FullScreenActivity).supportFragmentManager
}