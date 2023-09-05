package dev.pegasus.admob.banner

import android.content.Context
import android.hardware.display.DisplayManager
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.getSystemService
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import dev.pegasus.admob.banner.dataClasses.BannerValue
import dev.pegasus.admob.banner.enums.BannerStatus
import dev.pegasus.admob.banner.enums.CollapsiblePositionType
import dev.pegasus.admob.utils.GeneralUtils.addCleanView
import dev.pegasus.admob.utils.LogUtils.TAG_ADS

/**
 * @Author: SOHAIB AHMED
 * @Date: 9/5/2023
 * @Accounts
 *      -> https://github.com/epegasus
 *      -> https://stackoverflow.com/users/20440272/sohaib-ahmed
 */
class BannerAdsConfig(private val context: Context) {

    private val hashMap = HashMap<String, BannerValue>()
    private var isLoading = false

    @Suppress("DEPRECATION")
    private fun getAdSize(viewGroup: ViewGroup): AdSize {
        var adWidthPixels: Float = viewGroup.width.toFloat()
        val density = context.resources.displayMetrics.density

        if (adWidthPixels == 0f) {
            adWidthPixels = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val windowManager = context.getSystemService<WindowManager>()
                val bounds = windowManager?.currentWindowMetrics?.bounds
                bounds?.width()?.toFloat() ?: 380f
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

    fun loadBannerAd(globalContext: Context?, bannerKey: String, bannerAdID: String, isInternetConnected: Boolean, remoteConfigValue: Int, isBillingRequired: Boolean, viewGroup: ViewGroup, collapsiblePositionType: CollapsiblePositionType = CollapsiblePositionType.none) {
        if (hashMap[bannerKey] == null)
            hashMap[bannerKey] = BannerValue(BannerStatus.none, viewGroup, null)
        if (bannerAdID.trim().isEmpty()) {
            Log.e(TAG_ADS, "$bannerKey -> loadBannerAd: Ad Id should not be empty")
            cleanFrame(viewGroup)
            return
        }

        if (!isInternetConnected) {
            Log.e(TAG_ADS, "$bannerKey -> loadBannerAd: No Internet connection")
            viewGroup.visibility = View.GONE
            return
        }

        // Re-adding the banner
        if (hashMap[bannerKey]?.bannerStatus == BannerStatus.using) {
            Log.e(TAG_ADS, "$bannerKey -> loadBannerAd: An Ad is added again")
            viewGroup.addCleanView(hashMap[bannerKey]?.adView)
            return
        }

        // Checking for pre-loaded
        hashMap.forEach {
            if (it.value.bannerStatus == BannerStatus.free) {
                Log.e(TAG_ADS, "$bannerKey -> loadBannerAd: An Ad is available to use: Using now")

                // Shifted to New
                val freeAd = it.value.adView
                viewGroup.addCleanView(freeAd)
                viewGroup.visibility = View.VISIBLE
                hashMap[bannerKey]?.adView = freeAd
                hashMap[bannerKey]?.bannerStatus = BannerStatus.using

                // releasing old
                it.value.adView = null
                it.value.viewGroup = null
                it.value.bannerStatus = BannerStatus.none
                return
            }
        }

        if (isLoading) {
            Log.e(TAG_ADS, "$bannerKey -> loadBannerAd: An Ad is being loaded")
            return
        }

        if (!isBillingRequired) {
            Log.e(TAG_ADS, "$bannerKey -> loadBannerAd: User has premium access")
            cleanFrame(viewGroup)
            return
        }

        if (remoteConfigValue != 1) {
            Log.e(TAG_ADS, "$bannerKey -> loadBannerAd: Remote Configuration: Ad is off")
            cleanFrame(viewGroup)
            return
        }

        if (globalContext == null) {
            Log.e(TAG_ADS, "$bannerKey -> loadBannerAd: Context is null")
            viewGroup.visibility = View.GONE
            return
        }

        isLoading = true

        val adRequest = if (collapsiblePositionType == CollapsiblePositionType.none) {
            Log.d(TAG_ADS, "$bannerKey -> loadBannerAd: loading normal banner...")
            AdRequest.Builder().build()
        } else {
            Log.d(TAG_ADS, "$bannerKey -> loadBannerAd: loading collapsible banner: $collapsiblePositionType")
            val bundle = Bundle().apply {
                putString("collapsible", collapsiblePositionType.toString())
            }
            AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter::class.java, bundle).build()
        }
        val adSize = getAdSize(viewGroup)
        val adView = AdView(globalContext).also {
            it.descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
            it.adUnitId = bannerAdID
            it.setAdSize(adSize)
            it.adListener = object : AdListener() {
                override fun onAdOpened() {
                    Log.d(TAG_ADS, "$bannerKey -> loadBannerAd: onAdOpened: opened")
                }

                override fun onAdClosed() {
                    Log.d(TAG_ADS, "$bannerKey -> loadBannerAd: onAdClosed: Closed")
                }

                override fun onAdClicked() {
                    Log.d(TAG_ADS, "$bannerKey -> loadBannerAd: onAdClicked: Clicked")
                }

                override fun onAdImpression() {
                    Log.d(TAG_ADS, "$bannerKey -> loadBannerAd: onAdImpression: recorded")
                    hashMap[bannerKey]?.bannerStatus = BannerStatus.using
                }

                override fun onAdLoaded() {
                    Log.d(TAG_ADS, "$bannerKey -> loadBannerAd: onAdLoaded: loaded")
                    isLoading = false

                    hashMap[bannerKey]?.adView?.let { ad ->
                        hashMap[bannerKey]?.viewGroup?.visibility = View.VISIBLE
                        hashMap[bannerKey]?.viewGroup?.addCleanView(ad)
                    } ?: Log.e(TAG_ADS, "$bannerKey -> loadBannerAd: onAdLoaded: adView is null")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.e(TAG_ADS, "$bannerKey -> loadBannerAd: onAdFailedToLoad: ", Exception(loadAdError.message))
                    isLoading = false
                    cleanFrame(viewGroup)
                }
            }
        }
        adView.loadAd(adRequest)
        hashMap[bannerKey] = BannerValue(BannerStatus.free, viewGroup, adView)
    }

    private fun cleanFrame(viewGroup: ViewGroup) {
        viewGroup.removeAllViews()
        viewGroup.visibility = View.GONE
    }

    fun onPause(bannerKey: String) {
        hashMap[bannerKey]?.adView?.pause()
    }

    fun onResume(bannerKey: String) {
        hashMap[bannerKey]?.adView?.resume()
    }

    fun hide(bannerKey: String) {
        hashMap[bannerKey]?.viewGroup?.visibility = View.GONE
    }

    fun onDestroy(bannerKey: String) {
        Log.e(TAG_ADS, "$bannerKey -> loadBannerAd: onDestroy: destroyed")
        hashMap[bannerKey]?.bannerStatus = BannerStatus.none
        hashMap[bannerKey]?.viewGroup?.removeAllViews()
        hashMap[bannerKey]?.viewGroup = null
        hashMap[bannerKey]?.adView?.destroy()
        hashMap[bannerKey]?.adView = null
    }
}