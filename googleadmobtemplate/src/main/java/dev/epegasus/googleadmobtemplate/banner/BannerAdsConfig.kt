package dev.epegasus.googleadmobtemplate.banner

import android.app.Activity
import android.content.Context
import android.hardware.display.DisplayManager
import android.os.Build
import android.util.DisplayMetrics
import android.view.Display
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.getSystemService
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import dev.epegasus.googleadmobtemplate.LogUtils.showAdsLog

class BannerAdsConfig(private val context: Context) {

    private var adView: AdView? = null
    private var frameLayout: FrameLayout? = null
    private var isLoading = false

    @Suppress("DEPRECATION")
    private val adSize: AdSize
        get() {
            val activity = (context as Activity)
            var adWidthPixels: Float = frameLayout?.width?.toFloat() ?: 0f
            val density = activity.resources.displayMetrics.density

            if (adWidthPixels == 0f) {
                adWidthPixels = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val bounds = activity.windowManager.currentWindowMetrics.bounds
                    bounds.width().toFloat()
                } else {
                    val display: Display? = context.getSystemService<DisplayManager>()?.getDisplay(Display.DEFAULT_DISPLAY)
                    val outMetrics = DisplayMetrics()
                    display?.getMetrics(outMetrics)
                    outMetrics.widthPixels.toFloat()
                }
            }
            val adWidth = (adWidthPixels / density).toInt()
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth)
        }

    fun checkAndLoadBanner(bannerAdID: String, isInternetConnected: Boolean, isRemoteConfig: Boolean, isBillingRequired: Boolean, frameLayout: FrameLayout) {
        this.frameLayout = frameLayout

        if (isLoading)
            return

        if (isInternetConnected && isRemoteConfig && isBillingRequired) {
            showAdsLog(context, "checkAndLoadBanner", "BannerAd Loading", "Called")
            isLoading = true

            val adRequest = AdRequest.Builder().build()

            adView = AdView(context).also {
                it.adUnitId = bannerAdID
                it.setAdSize(adSize)
                it.adListener = bannerAdListener
                it.loadAd(adRequest)
            }
        } else {
            showAdsLog(context, "checkAndLoadBanner", "BannerAd Failed", "Resistance occurs")
            frameLayout.removeAllViews()
        }
    }

    private val bannerAdListener = object : AdListener() {
        override fun onAdClicked() {
            // Code to be executed when the user clicks on an ad.
            showAdsLog(context, "bannerAdListener", "onAdClicked", "Called")
        }

        override fun onAdClosed() {
            // Code to be executed when the user is about to return to the app after tapping on an ad.
            showAdsLog(context, "bannerAdListener", "onAdClosed", "Called")
        }

        override fun onAdFailedToLoad(adError: LoadAdError) {
            // Code to be executed when an ad request fails.
            showAdsLog(context, "bannerAdListener", "onAdFailedToLoad", "Called")
            frameLayout?.removeAllViews()
            isLoading = false
        }

        override fun onAdImpression() {
            // Code to be executed when an impression is recorded for an ad.
            showAdsLog(context, "bannerAdListener", "onAdImpression", "Called")
        }

        override fun onAdLoaded() {
            // Code to be executed when an ad finishes loading.
            showAdsLog(context, "bannerAdListener", "onAdLoaded", "Called")
            frameLayout?.removeAllViews()
            frameLayout?.addView(adView)
            isLoading = false
        }

        override fun onAdOpened() {
            // Code to be executed when an ad opens an overlay that covers the screen.
            showAdsLog(context, "bannerAdListener", "onAdOpened", "Called")
        }
    }

    fun hideAd(isHide: Boolean) {
        if (isHide)
            frameLayout?.visibility = View.GONE
        else
            frameLayout?.visibility = View.VISIBLE
    }

    fun onPause() {
        adView?.pause()
    }

    fun onResume() {
        adView?.resume()
    }

    fun onDestroy() {
        adView?.destroy()
    }
}