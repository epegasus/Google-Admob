package dev.epegasus.googleadmob.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.nativead.NativeAd
import dev.epegasus.googleadmob.R
import dev.epegasus.googleadmob.databinding.ActivityMainBinding
import dev.epegasus.googleadmob.helper.managers.InternetHandler
import dev.epegasus.googleadmob.helper.utils.GeneralUtils.withDelay
import dev.epegasus.googleadmob.helper.utils.SharedPreferencesUtils
import dev.pegasus.admob.banner.BannerAdsConfig
import dev.pegasus.admob.banner.enums.CollapsiblePositionType
import dev.pegasus.admob.interstitial.InterstitialAdsConfig
import dev.pegasus.admob.interstitial.interfaces.InterstitialOnLoadCallBack
import dev.pegasus.admob.interstitial.interfaces.InterstitialOnShowCallBack
import dev.pegasus.admob.native.NativeAdsConfig

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val sharedPreferences by lazy { SharedPreferencesUtils(this) }
    private val internetHandler by lazy { InternetHandler(this) }

    private val bannerAdsConfig by lazy { BannerAdsConfig(this) }
    private val nativeAdsConfig by lazy { NativeAdsConfig(this) }
    private val interstitialAdsConfig by lazy { InterstitialAdsConfig(this) }

    private var nativeAd: NativeAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        diComponent.bannerAdsConfig.onDestroy(BannerAdKey.featured.toString())
    }

    private fun loadNative() = with(diComponent) {
        val nativeAdId = when (sharedPreferenceUtils.showLanguageScreen) {
            true -> getResString(R.string.admob_native_language_id)
            false -> getResString(R.string.admob_native_intro_id)
        }

        val remoteConfig = when (sharedPreferenceUtils.showLanguageScreen) {
            true -> sharedPreferenceUtils.rcNativeLanguage
            false -> sharedPreferenceUtils.rcNativeIntro
        }

        val nativeKey = when (sharedPreferenceUtils.showLanguageScreen) {
            true -> NativeAdKey.appLanguage.toString()
            false -> NativeAdKey.intro.toString()
        }

        nativeAdsConfig.loadNativeAd(
            nativeKey = nativeKey,
            nativeAdID = nativeAdId,
            isInternetConnected = internetManager.isInternetConnected,
            remoteConfigValue = remoteConfig,
            isBillingRequired = sharedPreferenceUtils.isBillingRequired,
            timeOut = 8000
        )
    }

    private fun showNative() = diComponent.apply {
        nativeAdsConfig.showNativeAd(
            nativeKey = NativeAdKey.appLanguage.toString(),
            viewGroup = binding.flNativeContainerAppLanguage,
            viewLifecycleOwner = viewLifecycleOwner,
            reloadAdCallback = { exception ->
                exception?.let {
                    it.recordException("FragmentAppLanguage")
                    return@showNativeAd
                }
                nativeAdsConfig.loadNativeAd(
                    nativeKey = NativeAdKey.appLanguage.toString(),
                    nativeAdID = getResString(R.string.admob_native_language_id),
                    isInternetConnected = internetManager.isInternetConnected,
                    remoteConfigValue = sharedPreferenceUtils.rcNativeLanguage,
                    isBillingRequired = sharedPreferenceUtils.isBillingRequired
                )
            },
            adClick = {
                EventsProvider.EXP_AD_CLICK.postFirebaseEvent()
            }
        )
    }

    private fun loadInterstitial() {
        with(diComponent) {
            val interAdId = globalContext.resources.getString(R.string.admob_interstitial_splash_id)
            val remotePermission = when (sharedPreferenceUtils.showLanguageScreen) {
                true -> sharedPreferenceUtils.rcInterLanguage
                false -> sharedPreferenceUtils.rcInterSplash
            }
            interstitialAdsConfig.loadInterstitialAd(
                interstitialAdID = interAdId,
                isInternetConnected = internetManager.isInternetConnected,
                remoteConfigValue = remotePermission,
                isBillingRequired = sharedPreferenceUtils.isBillingRequired,
                object : InterstitialOnLoadCallBack {
                    override fun onAdFailedToLoad() = onAdResponse(1)
                    override fun onAdLoaded() = onAdResponse(1)
                    override fun onResponse() = onAdResponse(1)
                })
        }
    }
    private fun showInterstitial() {
        diComponent.interstitialAdsConfig.showInterstitialAd(this, object : InterstitialOnShowCallBack {
            override fun onAdShowedFullScreenContent() {}
            override fun onAdDismissedFullScreenContent() {
                if (isCurrentDestination(R.id.fragmentSplash)) {
                    navigateScreen()
                }
            }

            override fun onAdFailedToShowFullScreenContent() {
                navigateScreen()
            }

            override fun onAdImpression() {
                withDelay { navigateScreen() }
            }
        })
    }


    private fun loadBannerAd() = diComponent.apply {
        binding.flAdViewBannerMain.visibility = View.VISIBLE
        binding.flAdViewBannerMain.addCleanView(binding.incLoadingMain.root)
        bannerAdsConfig.loadBannerAd(
            this@MainActivity,
            bannerKey = BannerAdKey.featured.toString(),
            bannerAdID = resources.getString(R.string.admob_banner_features_id),
            isInternetConnected = internetManager.isInternetConnected,
            remoteConfigValue = sharedPreferenceUtils.rcBannerCollage,
            isBillingRequired = sharedPreferenceUtils.isBillingRequired,
            viewGroup = binding.flAdViewBannerMain,
            collapsiblePositionType = CollapsiblePositionType.bottom
        )
    }
}