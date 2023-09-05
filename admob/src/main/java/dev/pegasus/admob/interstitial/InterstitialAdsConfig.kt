package dev.pegasus.admob.interstitial

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import dev.pegasus.admob.interstitial.interfaces.InterstitialOnLoadCallBack
import dev.pegasus.admob.interstitial.interfaces.InterstitialOnShowCallBack
import dev.pegasus.admob.utils.LogUtils.TAG_ADS

/**
 * @Author: SOHAIB AHMED
 * @Date: 9/5/2023
 * @Accounts
 *      -> https://github.com/epegasus
 *      -> https://stackoverflow.com/users/20440272/sohaib-ahmed
 */

class InterstitialAdsConfig(private val context: Context) {

    companion object {
        private var interstitialAd: InterstitialAd? = null
    }

    private var adRequest: AdRequest = AdRequest.Builder().build()
    private var isLoading = false

    fun loadInterstitialAd(interstitialAdID: String, isInternetConnected: Boolean, remoteConfigValue: Int, isBillingRequired: Boolean, listener: InterstitialOnLoadCallBack) {

        if (interstitialAdID.trim().isEmpty()) {
            Log.e(TAG_ADS, "loadInterstitialAd: Ad Id should not be empty")
            listener.onResponse()
            return
        }

        if (!isInternetConnected) {
            Log.e(TAG_ADS, "loadInterstitialAd: No Internet connection")
            listener.onResponse()
            return
        }

        if (interstitialAd != null) {
            Log.e(TAG_ADS, "loadInterstitialAd: An Ad is already available")
            listener.onResponse()
            return
        }

        if (isLoading) {
            Log.e(TAG_ADS, "loadInterstitialAd: An Ad is being loaded")
            listener.onResponse()
            return
        }

        if (!isBillingRequired) {
            Log.e(TAG_ADS, "loadInterstitialAd: User has premium access")
            listener.onResponse()
            return
        }

        if (remoteConfigValue != 1) {
            Log.e(TAG_ADS, "loadInterstitialAd: Remote Configuration: Ad is off")
            listener.onResponse()
            return
        }

        isLoading = true
        Log.d(TAG_ADS, "loadInterstitialAd: loading...")

        try {
            InterstitialAd.load(context, interstitialAdID, adRequest, object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                    Log.e(TAG_ADS, "loadInterstitialAd: onAdFailedToLoad: ", Exception(loadAdError.message))
                    isLoading = false
                    interstitialAd = null
                    listener.onAdFailedToLoad()
                }

                override fun onAdLoaded(interstitialAd1: InterstitialAd) {
                    super.onAdLoaded(interstitialAd1)
                    Log.d(TAG_ADS, "loadInterstitialAd: onAdLoaded: loaded")
                    isLoading = false
                    interstitialAd = interstitialAd1
                    listener.onAdLoaded()
                }
            })
        } catch (ex: OutOfMemoryError) {
            Log.e(TAG_ADS, "OutOfMemoryError: ", ex)
        }
    }

    fun showInterstitialAd(context: Context?, listener: InterstitialOnShowCallBack) {
        if (interstitialAd != null) {
            interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG_ADS, "showInterstitialAd: onAdDismissedFullScreenContent: dismissed")
                    listener.onAdDismissedFullScreenContent()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.e(TAG_ADS, "showInterstitialAd: onAdFailedToShowFullScreenContent: ${adError.message}")
                    listener.onAdFailedToShowFullScreenContent()
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d(TAG_ADS, "showInterstitialAd: onAdShowedFullScreenContent: shown")
                    listener.onAdShowedFullScreenContent()
                    interstitialAd = null
                }

                override fun onAdImpression() {
                    Log.d(TAG_ADS, "showInterstitialAd: onAdImpression: recorded")
                    listener.onAdImpression()
                }
            }
            if (context != null) {
                interstitialAd?.show((context as Activity))
                return
            }
        }
        listener.onAdFailedToShowFullScreenContent()
    }

    fun isInterstitialLoaded(): Boolean {
        return interstitialAd != null
    }
}