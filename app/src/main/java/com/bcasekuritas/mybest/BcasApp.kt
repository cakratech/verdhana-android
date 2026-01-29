package com.bcasekuritas.mybest

import android.util.Log
import androidx.lifecycle.ProcessLifecycleOwner
import com.aheaditec.talsec_security.security.api.SuspiciousAppInfo
import com.aheaditec.talsec_security.security.api.Talsec
import com.aheaditec.talsec_security.security.api.TalsecConfig
import com.aheaditec.talsec_security.security.api.TalsecMode
import com.aheaditec.talsec_security.security.api.ThreatListener
import dagger.hilt.android.HiltAndroidApp
import com.bcasekuritas.mybest.app.base.BaseApplication
import com.bcasekuritas.mybest.ext.common.AppLifecycleObserver
import com.bcasekuritas.mybest.ext.common.NetworkMonitor
import com.bcasekuritas.mybest.ext.common.RabbitMQForegroundService
import com.bcasekuritas.mybest.ext.timber.CrashReportingTree
import timber.log.Timber
import javax.inject.Inject
import kotlin.system.exitProcess

@HiltAndroidApp
class BcasApp: BaseApplication(), ThreatListener.ThreatDetected {
    override fun getBaseUrl(): String = ""

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
        else Timber.plant(CrashReportingTree())

        val config = TalsecConfig.Builder(
            expectedPackageName,
            expectedSigningCertificateHashBase64)
            .watcherMail(watcherMail)
            .prod(isProd)
            .killOnBypass(killOnBypass)
            .build()

        Talsec.start(this, config, TalsecMode.FOREGROUND)
        ThreatListener(this).registerListener(this)
    }

    override fun onRootDetected() {
        killApp()
    }

    override fun onDebuggerDetected() {
        killApp()
    }

    override fun onEmulatorDetected() {
        killApp()
    }

    override fun onTamperDetected() {
    }

    override fun onUntrustedInstallationSourceDetected() {
    }

    override fun onHookDetected() {
        killApp()
    }

    override fun onDeviceBindingDetected() {
    }

    override fun onObfuscationIssuesDetected() {
    }

    override fun onMalwareDetected(p0: MutableList<SuspiciousAppInfo>) {
    }

    override fun onScreenshotDetected() {
    }

    override fun onScreenRecordingDetected() {
    }

    override fun onMultiInstanceDetected() {
    }

    override fun onUnsecureWifiDetected() {
    }

    override fun onTimeSpoofingDetected() {
    }

    override fun onLocationSpoofingDetected() {
    }

    private fun killApp() {
        if (isProd) {
            android.os.Process.killProcess(android.os.Process.myPid())
            exitProcess(0)
        }
    }

    companion object {
        private const val expectedPackageName = "com.bcasekuritas.mybest"
        private val expectedSigningCertificateHashBase64 = arrayOf("H3G7bLb6ZbQKU5Q3hZ+Z+YUXocAMbAS9O7w708lSfC8=")
        private const val watcherMail = "kalacakratechnology@gmail.com"
        private val isProd = !BuildConfig.DEBUG
        private val killOnBypass = true
    }

}