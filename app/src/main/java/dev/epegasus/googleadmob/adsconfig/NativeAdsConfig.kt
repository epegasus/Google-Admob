package dev.epegasus.googleadmob.adsconfig

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.pdfapp.pdfreader.allpdf.pdfviewer.helper.interfaces.admob.OnNativeAdLoad
import dev.epegasus.googleadmob.listeners.OnResponseListener
import dev.epegasus.googleadmob.utils.GeneralUtils.isInternetConnected
import dev.epegasus.googleadmob.utils.LogUtils.showAdsLog
import dev.epegasus.googleadmob.utils.SharedPreferencesUtils.getIsBillingPurchased

class NativeAdsConfig(private val context: Context) {

    fun checkNativeAd(nativeAdID: String, cl_ad_container: ConstraintLayout, ll_loading: LinearLayout, templateView: TemplateView, onResponseListener: OnResponseListener, onNativeAdLoad: OnNativeAdLoad) {
        val isRemoteConfig = true
        if (isInternetConnected(context) && isRemoteConfig && !getIsBillingPurchased(context)) {
            val adLoader: AdLoader = AdLoader.Builder(context, nativeAdID)
                .forNativeAd { nativeAd ->
                    if ((context as Activity).isDestroyed) {
                        showAdsLog(context, "checkSplashNativeAd", "isDestroyed", "Destroying Native, fragment not found")
                        nativeAd.destroy()
                        return@forNativeAd
                    }
                    templateView.setNativeAd(nativeAd)
                    onNativeAdLoad.onNativeAdLoad(nativeAd)
                }
                .withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        super.onAdFailedToLoad(loadAdError)
                        showAdsLog(context, "checkSplashNativeAd", "onAdFailedToLoad", loadAdError.message)
                        cl_ad_container.removeAllViews()
                        cl_ad_container.visibility = View.GONE
                        onResponseListener.onResponse()
                    }

                    override fun onAdLoaded() {
                        super.onAdLoaded()
                        showAdsLog(context, "checkSplashNativeAd", "onAdLoaded", "loaded")
                        ll_loading.visibility = View.GONE
                        templateView.visibility = View.VISIBLE
                        cl_ad_container.visibility = View.VISIBLE
                        onResponseListener.onResponse()
                    }
                })
                .build()
            adLoader.loadAd(AdManagerAdRequest.Builder().build())
        } else {
            showAdsLog(context, "checkSplashNativeAd", "else", "called")
            cl_ad_container.removeAllViews()
            cl_ad_container.visibility = View.GONE
            onResponseListener.onResponse()
        }
    }
}