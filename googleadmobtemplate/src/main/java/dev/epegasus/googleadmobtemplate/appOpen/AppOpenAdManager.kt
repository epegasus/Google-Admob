package dev.epegasus.googleadmobtemplate.appOpen

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.AdActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import dev.epegasus.googleadmobtemplate.LogUtils.showAdsLog

class AppOpenAdManager(private val app: Application) : LifecycleObserver, Application.ActivityLifecycleCallbacks {

    private var appOpenAd: AppOpenAd? = null
    private var isLoadingAd = false
    private var isFromAdActivity = false
    var isShowingAd = false

    private var currentActivity: Activity? = null

    init {
        app.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    // temporary variables
    val isInternetConnected = false
    val isSplashFragmentActive = false
    val isBillingRequired = true
    val isAdActive = 1

    /** LifecycleObserver method that shows the app open ad when the app moves to foreground. */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {

        if (!isSplashFragmentActive && isBillingRequired && (isAdActive == 1)) {
            showAdIfAvailable()
        } else {
            if (isSplashFragmentActive)
                showAdsLog(app, "AppOpenAdManager", "Lifecycle.Event.ON_START", "Splash Fragment is Active")
            else if (!isBillingRequired)
                showAdsLog(app, "AppOpenAdManager", "Lifecycle.Event.ON_START", "Premium Customer (Billing is not required)")
            else if (isAdActive != 1) {
                showAdsLog(app, "AppOpenAdManager", "Lifecycle.Event.ON_START", "Remote Configuration is off")
            }
        }
    }

    /* -------------------------------------------- for Open App -------------------------------------------- */

    override fun onActivityStarted(activity: Activity) {
        isFromAdActivity = currentActivity is AdActivity
        currentActivity = activity
    }

    override fun onActivityResumed(activity: Activity) {
        isFromAdActivity = currentActivity is AdActivity
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}


    /** Request an ad. */
    fun loadOpenAppAd(adId: String) {
        if (isLoadingAd || isAdAvailable()) {
            showAdsLog(app, "AppOpenAdManager", "loadOpenAppAd", "isLoadingAd / isAdAvailable")
            return
        }

        if (!isInternetConnected || !isBillingRequired || isAdActive != 1) {
            showAdsLog(app, "AppOpenAdManager", "loadOpenAppAd", "Either Internet / Billing / Remote Config")
            return
        }

        isLoadingAd = true

        val request = AdRequest.Builder().build()
        AppOpenAd.load(app.applicationContext, adId, request, AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, object : AppOpenAd.AppOpenAdLoadCallback() {

            override fun onAdLoaded(ad: AppOpenAd) {
                isLoadingAd = false
                appOpenAd = ad
                showAdsLog(app, "AppOpenAdManager", "onAdLoaded", "called : ${appOpenAd.hashCode()}")
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                isLoadingAd = false
                showAdsLog(app, "AppOpenAdManager", "onAdFailedToLoad", loadAdError.message)
            }
        })
    }

    /** Check if ad exists and can be shown. */
    private fun isAdAvailable(): Boolean {
        return appOpenAd != null
    }

    /** Shows the ad if one isn't already showing. */
    private fun showAdIfAvailable() {
        if (isShowingAd) {
            showAdsLog(app, "AppOpenAdManager", "showAdIfAvailable", "The app open ad is already showing.")
            return
        }
        if (!isAdAvailable()) {
            showAdsLog(app, "AppOpenAdManager", "showAdIfAvailable", "The app open ad is not ready yet.")
            loadOpenAppAd("")
            return
        }

        if (isAdActive != 1 || !isInternetConnected || !isBillingRequired) {
            showAdsLog(app, "AppOpenAdManager", "showAdIfAvailable : Remote", "called")
            return
        }

        if (isFromAdActivity) {
            showAdsLog(app, "AppOpenAdManager", "showAdIfAvailable", "Ad Activity is visible")
            return
        }

        isShowingAd = true
        appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {

            override fun onAdDismissedFullScreenContent() {
                showAdsLog(app, "AppOpenAdManager", "onAdDismissedFullScreenContent", "Ad dismissed fullscreen content.")
                appOpenAd = null
                isShowingAd = false
                loadOpenAppAd("")
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                showAdsLog(app, "AppOpenAdManager", "onAdFailedToShowFullScreenContent", adError.message)
                appOpenAd = null
                isShowingAd = false
                loadOpenAppAd("")
            }

            override fun onAdShowedFullScreenContent() {
                showAdsLog(app, "AppOpenAdManager", "onAdShowedFullScreenContent", "Ad showed fullscreen content.")
            }
        }
        currentActivity?.let { appOpenAd?.show(it) }
    }

    fun destroyOpenApp() {
        appOpenAd = null
    }
}