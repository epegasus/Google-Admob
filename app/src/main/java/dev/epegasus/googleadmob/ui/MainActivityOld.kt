package dev.epegasus.googleadmob.ui

import androidx.appcompat.app.AppCompatActivity

class MainActivityOld : AppCompatActivity() {

    /* private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
     private val bannerAdsConfig by lazy { BannerAdsConfig(this) }
     private val nativeAdsConfig by lazy { NativeAdsConfig(this) }
     private val interstitialAdsConfig by lazy { InterstitialAdsConfig(this) }
     private val sharedPreferences by lazy { SharedPreferencesUtils(this) }
     private val internetHandler by lazy { InternetHandler(this) }

     private var nativeAd: NativeAd? = null

     override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         setContentView(binding.root)

         //loadBannerAd()
         loadCollapsibleBannerAd()
         loadNativeAd()
         loadInterstitialAd()
     }

     override fun onPause() {
         bannerAdsConfig.onPause()
         super.onPause()
     }

     override fun onResume() {
         super.onResume()
         bannerAdsConfig.onResume()
     }

     private fun loadBannerAd() {
         bannerAdsConfig.checkAndLoadBanner(
             resources.getString(R.string.admob_banner_id),
             internetHandler.isInternetConnected,
             true,
             sharedPreferences.isBillingRequired,
             binding.flBannerContainer
         )
     }

     private fun loadCollapsibleBannerAd() {
         bannerAdsConfig.checkAndLoadCollapsibleBanner(
             resources.getString(R.string.admob_banner_collapsible_id),
             internetHandler.isInternetConnected,
             true,
             sharedPreferences.isBillingRequired,
             binding.flBannerContainer,
             CollapsiblePositionType.bottom
         )
     }

     private fun loadNativeAd() {
         val nativeAdID = resources.getString(R.string.admob_native_id)
         nativeAdsConfig.checkNativeAd(nativeAdID, internetHandler.isInternetConnected, true, sharedPreferences.isBillingRequired, binding.flNativeContainer, binding.incNativeLoading.root, object : OnNativeResponseListener {
             override fun onResponse() {

             }
         }, object : OnNativeAdLoad {
             override fun onNativeAdLoad(nativeAd: NativeAd) {
                 this@MainActivityOld.nativeAd = nativeAd
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
         bannerAdsConfig.onDestroy()
         super.onDestroy()
         nativeAd?.destroy()
         nativeAd = null
     }*/
}