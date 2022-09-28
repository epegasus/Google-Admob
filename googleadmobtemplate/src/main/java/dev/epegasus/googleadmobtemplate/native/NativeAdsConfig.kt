package dev.epegasus.googleadmobtemplate.native

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.nativead.NativeAd
import dev.epegasus.googleadmobtemplate.LogUtils.showAdsLog
import dev.epegasus.googleadmobtemplate.databinding.AdmobNativeMediumBinding
import dev.epegasus.googleadmobtemplate.databinding.AdmobNativeNormalBinding
import dev.epegasus.googleadmobtemplate.native.interfaces.OnNativeAdLoad
import dev.epegasus.googleadmobtemplate.native.interfaces.OnNativeResponseListener

class NativeAdsConfig(private val context: Context) {

    fun checkNativeAd(
        nativeAdID: String, isInternetConnected: Boolean, isRemoteConfig: Boolean, isBillingRequired: Boolean,
        flAdContainer: FrameLayout, llLoading: LinearLayout,
        onResponseListener: OnNativeResponseListener,
        onNativeAdLoad: OnNativeAdLoad,
    ) {
        val binding = AdmobNativeMediumBinding.inflate(LayoutInflater.from(context))
        if (isInternetConnected && isRemoteConfig && isBillingRequired) {
            val adLoader: AdLoader = AdLoader.Builder(context, nativeAdID)
                .forNativeAd { nativeAd ->
                    if ((context as Activity).isDestroyed) {
                        showAdsLog(context, "checkNativeAd", "isDestroyed", "Destroying Native, fragment not found")
                        nativeAd.destroy()
                        return@forNativeAd
                    }
                    populateLayout(flAdContainer, binding, nativeAd)
                    onNativeAdLoad.onNativeAdLoad(nativeAd)
                }
                .withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        super.onAdFailedToLoad(loadAdError)
                        showAdsLog(context, "checkNativeAd", "onAdFailedToLoad", loadAdError.message)
                        flAdContainer.removeAllViews()
                        flAdContainer.visibility = View.GONE
                        onResponseListener.onResponse()
                    }

                    override fun onAdLoaded() {
                        super.onAdLoaded()
                        showAdsLog(context, "checkNativeAd", "onAdLoaded", "loaded")
                        llLoading.visibility = View.GONE
                        onResponseListener.onResponse()
                    }
                })
                .build()
            adLoader.loadAd(AdManagerAdRequest.Builder().build())
        } else {
            showAdsLog(context, "checkNativeAd", "else", "called")
            flAdContainer.removeAllViews()
            flAdContainer.visibility = View.GONE
            onResponseListener.onResponse()
        }
    }

    private fun populateLayout(clAdContainer: FrameLayout, binding: AdmobNativeMediumBinding, nativeAd: NativeAd) {
        binding.apply {
            root.apply {
                // refer all our views to NativeAdView
                mediaView = mvMedia
                headlineView = tvTitle
                bodyView = tvBody
                iconView = ifvAdIcon
                advertiserView = tvAd
                callToActionView = btnAction

                setNativeAd(nativeAd)
            }

            // Title & Body
            tvTitle.text = nativeAd.headline
            tvBody.text = nativeAd.body

            // Ad
            nativeAd.advertiser?.let { tvAd.text = it }

            // Icon
            nativeAd.icon?.let {
                ifvAdIcon.setImageDrawable(it.drawable)
            } ?: kotlin.run {
                ifvAdIcon.visibility = View.GONE
            }

            // Button
            nativeAd.callToAction?.let {
                btnAction.text = it
            } ?: kotlin.run {
                btnAction.visibility = View.GONE
            }
        }
        clAdContainer.removeAllViews()
        clAdContainer.addView(binding.root)
    }

    fun checkNativeAdNormal(
        nativeAdID: String, isInternetConnected: Boolean, isRemoteConfig: Int, isBillingRequired: Boolean,
        cl_ad_container: FrameLayout, ll_loading: LinearLayout,
        onResponseListener: OnNativeResponseListener,
        onNativeAdLoad: OnNativeAdLoad,
    ) {
        val binding = AdmobNativeNormalBinding.inflate(LayoutInflater.from(context))
        if (isInternetConnected && (isRemoteConfig == 1) && isBillingRequired) {
            val adLoader: AdLoader = AdLoader.Builder(context, nativeAdID)
                .forNativeAd { nativeAd ->
                    if ((context as Activity).isDestroyed) {
                        showAdsLog(context, "checkNativeAdNormal", "isDestroyed", "Destroying Native, fragment not found")
                        nativeAd.destroy()
                        return@forNativeAd
                    }
                    populateNormalLayout(cl_ad_container, binding, nativeAd)
                    onNativeAdLoad.onNativeAdLoad(nativeAd)
                }
                .withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        super.onAdFailedToLoad(loadAdError)
                        showAdsLog(context, "checkNativeAdNormal", "onAdFailedToLoad", loadAdError.message)
                        cl_ad_container.removeAllViews()
                        cl_ad_container.visibility = View.GONE
                        onResponseListener.onResponse()
                    }

                    override fun onAdLoaded() {
                        super.onAdLoaded()
                        showAdsLog(context, "checkNativeAdNormal", "onAdLoaded", "loaded")
                        ll_loading.visibility = View.GONE
                        onResponseListener.onResponse()
                    }
                })
                .build()
            adLoader.loadAd(AdManagerAdRequest.Builder().build())
        } else {
            showAdsLog(context, "checkNativeAdNormal", "else", "called")
            cl_ad_container.removeAllViews()
            cl_ad_container.visibility = View.GONE
            onResponseListener.onResponse()
        }
    }

    private fun populateNormalLayout(clAdContainer: FrameLayout, binding: AdmobNativeNormalBinding, nativeAd: NativeAd) {
        binding.apply {
            root.apply {
                // refer all our views to NativeAdView
                mediaView = mvMedia
                headlineView = tvTitle
                bodyView = tvBody
                iconView = ifvAdIcon
                advertiserView = tvAd
                callToActionView = btnAction

                setNativeAd(nativeAd)
            }

            // Title & Body
            tvTitle.text = nativeAd.headline
            tvBody.text = nativeAd.body

            // Ad
            nativeAd.advertiser?.let { tvAd.text = it }

            // Icon
            nativeAd.icon?.let {
                ifvAdIcon.setImageDrawable(it.drawable)
            } ?: kotlin.run {
                ifvAdIcon.visibility = View.GONE
            }

            // Button
            nativeAd.callToAction?.let {
                btnAction.text = it
            } ?: kotlin.run {
                btnAction.visibility = View.GONE
            }
        }
        clAdContainer.removeAllViews()
        clAdContainer.addView(binding.root)
    }

}
