package dev.epegasus.googleadmob

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.nativead.NativeAd
import com.pdfapp.pdfreader.allpdf.pdfviewer.helper.interfaces.admob.InterstitialOnShowCallBack
import com.pdfapp.pdfreader.allpdf.pdfviewer.helper.interfaces.admob.OnNativeAdLoad
import dev.epegasus.googleadmob.adsconfig.InterstitialAdsConfig
import dev.epegasus.googleadmob.adsconfig.NativeAdsConfig
import dev.epegasus.googleadmob.databinding.ActivityMainBinding
import dev.epegasus.googleadmob.listeners.InterstitialOnLoadCallBack
import dev.epegasus.googleadmob.listeners.OnResponseListener

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var nativeAdsConfig: NativeAdsConfig
    private lateinit var interstitialAdsConfig: InterstitialAdsConfig

    private var nativeAd: NativeAd? = null

    private fun initializations() {
        nativeAdsConfig = NativeAdsConfig(this)
        interstitialAdsConfig = InterstitialAdsConfig(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initializations()
        loadNativeAd()
        loadInterstitialAd()
    }

    private fun loadNativeAd() {
        val nativeAdID = resources.getString(R.string.admob_native_id)
        nativeAdsConfig.checkNativeAd(nativeAdID, binding.clNativeContainer, binding.incNativeLoadingSplash.root, binding.tnvTemplate, object : OnResponseListener {
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
        interstitialAdsConfig.checkInterstitialAd(interAdId, isRemoteConfig, object : InterstitialOnLoadCallBack {
            override fun onAdFailedToLoad() {

            }

            override fun onAdLoaded() {
                showInterstitial()
            }
        }, object : OnResponseListener {
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