package dev.epegasus.googleadmobtemplate.interstitial

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import dev.epegasus.googleadmobtemplate.LogUtils.showAdsLog
import dev.epegasus.googleadmobtemplate.interstitial.interfaces.InterstitialOnLoadCallBack
import dev.epegasus.googleadmobtemplate.interstitial.interfaces.InterstitialOnShowCallBack
import dev.epegasus.googleadmobtemplate.interstitial.interfaces.OnInterstitialResponseListener

class InterstitialAdsConfig(private val context: Context) {

    private var adRequest: AdRequest = AdRequest.Builder().build()
    private var interstitialAd: InterstitialAd? = null
    private var isLoadingAd = false

    fun checkInterstitialAd(interstitialAdID: String, isInternetConnected: Boolean, isRemoteConfigActive: Boolean, isBillingRequired: Boolean, listener: InterstitialOnLoadCallBack, onResponseListener: OnInterstitialResponseListener) {
        if (isInternetConnected && isRemoteConfigActive && isBillingRequired) {
            if (!isLoadingAd && interstitialAd == null) {
                isLoadingAd = true
                InterstitialAd.load(context, interstitialAdID, adRequest, object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        super.onAdFailedToLoad(loadAdError)
                        showAdsLog(context, "checkInterstitialAd", "onAdFailedToLoad", loadAdError.message)
                        isLoadingAd = false
                        interstitialAd = null
                        listener.onAdFailedToLoad()
                    }

                    override fun onAdLoaded(interstitialAd1: InterstitialAd) {
                        super.onAdLoaded(interstitialAd1)
                        showAdsLog(context, "checkInterstitialAd", "onAdLoaded", "Successfully")
                        isLoadingAd = false
                        interstitialAd = interstitialAd1
                        listener.onAdLoaded()
                    }
                })
            } else {
                showAdsLog(context, "checkInterstitialAd", "Preloaded", "called")
                onResponseListener.onResponse()
            }
        } else {
            showAdsLog(context, "checkInterstitialAd", "else", "called")
            onResponseListener.onResponse()
        }
    }

    fun showInterstitialAd(listener: InterstitialOnShowCallBack) {
        if (interstitialAd != null) {
            interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    showAdsLog(context, "showInterstitialAd", "onAdDismissedFullScreenContent", "called")
                    listener.onAdDismissedFullScreenContent()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    showAdsLog(context, "showInterstitialAd", "onAdFailedToShowFullScreenContent", adError.message)
                    listener.onAdFailedToShowFullScreenContent()
                    interstitialAd = null
                }

                override fun onAdShowedFullScreenContent() {
                    showAdsLog(context, "showInterstitialAd", "onAdShowedFullScreenContent", "called")
                    listener.onAdShowedFullScreenContent()
                    interstitialAd = null
                }

                override fun onAdImpression() {
                    showAdsLog(context, "showInterstitialAd", "onAdImpression", "called")
                    listener.onAdImpression()
                }
            }
            interstitialAd?.show(context as Activity) ?: listener.onAdFailedToShowFullScreenContent()
        } else
            listener.onAdFailedToShowFullScreenContent()
    }

    fun isInterstitialLoaded(): Boolean {
        return interstitialAd != null
    }
}