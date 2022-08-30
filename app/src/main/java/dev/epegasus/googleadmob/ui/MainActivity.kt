package dev.epegasus.googleadmob.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.nativead.NativeAd
import dev.epegasus.googleadmob.R
import dev.epegasus.googleadmob.databinding.ActivityMainBinding
import dev.epegasus.googleadmob.helper.managers.InternetHandler
import dev.epegasus.googleadmob.helper.utils.SharedPreferencesUtils
import dev.epegasus.googleadmobtemplate.interstitial.InterstitialAdsConfig
import dev.epegasus.googleadmobtemplate.interstitial.interfaces.InterstitialOnLoadCallBack
import dev.epegasus.googleadmobtemplate.interstitial.interfaces.InterstitialOnShowCallBack
import dev.epegasus.googleadmobtemplate.interstitial.interfaces.OnInterstitialResponseListener
import dev.epegasus.googleadmobtemplate.native.NativeAdsConfig
import dev.epegasus.googleadmobtemplate.native.interfaces.OnNativeAdLoad
import dev.epegasus.googleadmobtemplate.native.interfaces.OnNativeResponseListener

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val nativeAdsConfig by lazy { NativeAdsConfig(this) }
    private val interstitialAdsConfig by lazy { InterstitialAdsConfig(this) }
    private val sharedPreferences by lazy { SharedPreferencesUtils(this) }
    private val internetHandler by lazy { InternetHandler(this) }

    private var nativeAd: NativeAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        loadNativeAd()
        loadInterstitialAd()
    }

    private fun loadNativeAd() {
        val nativeAdID = resources.getString(R.string.admob_native_id)
        nativeAdsConfig.checkNativeAd(nativeAdID, internetHandler.isInternetConnected, true, sharedPreferences.isBillingRequired, binding.clNativeContainer, binding.incNativeLoadingSplash.root, object : OnNativeResponseListener {
            override fun onResponse() {

            }
        }, object : OnNativeAdLoad {
            override fun onNativeAdLoad(nativeAd: NativeAd) {
                this@MainActivity.nativeAd = nativeAd
            }
        })
    }

    private fun loadInterstitialAd() {
        val interAdId = resources.getString(R.string.admob_interstitial_id)
        val isRemoteConfig = true
        interstitialAdsConfig.checkInterstitialAd(interAdId, internetHandler.isInternetConnected, isRemoteConfig, sharedPreferences.isBillingRequired, object : InterstitialOnLoadCallBack {
            override fun onAdFailedToLoad() {

            }

            override fun onAdLoaded() {
                showInterstitial()
            }
        }, object : OnInterstitialResponseListener {
            override fun onResponse() {

            }
        })
    }

    private fun showInterstitial() {
        interstitialAdsConfig.showInterstitialAd(object : InterstitialOnShowCallBack {
            override fun onAdDismissedFullScreenContent() {
            }

            override fun onAdFailedToShowFullScreenContent() {

            }

            override fun onAdShowedFullScreenContent() {
            }

            override fun onAdImpression() {

            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        nativeAd?.destroy()
        nativeAd = null
    }
}