package com.bcasekuritas.mybest.app.base.navigation

import android.app.Activity
import androidx.fragment.app.FragmentManager
import com.bcasekuritas.mybest.R
import com.bcasekuritas.mybest.app.feature.activity.main.MainActivity
import javax.inject.Inject

open class NavigationMain @Inject constructor(mainActivity: Activity) {

    private var containerId: Int = R.id.f_main_container
    private var fragmentManager: FragmentManager =
        (mainActivity as MainActivity).supportFragmentManager

}