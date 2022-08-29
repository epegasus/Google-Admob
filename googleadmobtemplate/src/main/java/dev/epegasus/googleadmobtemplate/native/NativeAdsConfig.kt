package dev.epegasus.googleadmobtemplate.native

import android.content.Context
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import dev.epegasus.googleadmobtemplate.native.interfaces.OnNativeAdLoad
import dev.epegasus.googleadmobtemplate.native.interfaces.OnNativeResponseListener

class NativeAdsConfig(private val context: Context) {

    fun checkNativeAd(nativeAdID: String, cl_ad_container: ConstraintLayout, ll_loading: LinearLayout, templateView: String, onResponseListener: OnNativeResponseListener, onNativeAdLoad: OnNativeAdLoad) {
        /*val isRemoteConfig = true
        if (isInternetConnected(context) && isRemoteConfig && sharedPreferences.isBillingRequired) {
            val adLoader: AdLoader = AdLoader.Builder(context, nativeAdID)
                .forNativeAd { nativeAd ->
                    if ((context as Activity).isDestroyed) {
                        showAdsLog(context, "checkNativeAd", "isDestroyed", "Destroying Native, fragment not found")
                        nativeAd.destroy()
                        return@forNativeAd
                    }
                    templateView.setNativeAd(nativeAd)
                    onNativeAdLoad.onNativeAdLoad(nativeAd)
                }
                .withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        super.onAdFailedToLoad(loadAdError)
                        showAdsLog(context, "checkNativeAd", "onAdFailedToLoad", loadAdError.message)
                        cl_ad_container.removeAllViews()
                        cl_ad_container.visibility = View.GONE
                        onResponseListener.onResponse()
                    }

                    override fun onAdLoaded() {
                        super.onAdLoaded()
                        showAdsLog(context, "checkNativeAd", "onAdLoaded", "loaded")
                        ll_loading.visibility = View.GONE
                        templateView.visibility = View.VISIBLE
                        cl_ad_container.visibility = View.VISIBLE
                        onResponseListener.onResponse()
                    }
                })
                .build()
            adLoader.loadAd(AdManagerAdRequest.Builder().build())
        } else {
            showAdsLog(context, "checkNativeAd", "else", "called")
            cl_ad_container.removeAllViews()
            cl_ad_container.visibility = View.GONE
            onResponseListener.onResponse()
        }*/
    }
}
