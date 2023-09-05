package dev.pegasus.admob.native

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.Keep
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import dev.pegasus.admob.R
import dev.pegasus.admob.databinding.AdmobNativeOneBinding
import dev.pegasus.admob.databinding.AdmobNativeTwoBinding
import dev.pegasus.admob.native.dataClasses.NativeValue
import dev.pegasus.admob.native.enums.NativeStatus
import dev.pegasus.admob.native.enums.StatusAds
import dev.pegasus.admob.native.resources.NativeLoadResource
import dev.pegasus.admob.utils.GeneralUtils.addCleanView
import dev.pegasus.admob.utils.GeneralUtils.withDelay
import dev.pegasus.admob.utils.LogUtils.TAG_ADS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @Author: SOHAIB AHMED
 * @Date: 9/5/2023
 * @Accounts
 *      -> https://github.com/epegasus
 *      -> https://stackoverflow.com/users/20440272/sohaib-ahmed
 */


@Keep
class NativeAdsConfig(private val context: Context) {

    companion object {
        private val hashMap = HashMap<String, NativeValue>()
    }

    private var isLoading = false

    private val _nativeLoadResponse = MutableLiveData<NativeLoadResource>()
    val nativeLoadResponse: LiveData<NativeLoadResource> get() = _nativeLoadResponse

    fun loadNativeAd(nativeKey: String, nativeAdID: String, isInternetConnected: Boolean, remoteConfigValue: Int, isBillingRequired: Boolean, timeOut: Long = 0) {

        if (nativeAdID.trim().isEmpty()) {
            Log.e(TAG_ADS, "$nativeKey -> loadNativeAd: Ad Id should not be empty: $nativeAdID")
            _nativeLoadResponse.postValue(NativeLoadResource.response())
            withDelay(0) {
                _nativeLoadResponse.postValue(NativeLoadResource.failure(Exception("Ad Id should not be empty")))
            }
            return
        }

        if (!isInternetConnected) {
            Log.e(TAG_ADS, "$nativeKey -> loadNativeAd: No Internet connection")
            withDelay(2000) {
                _nativeLoadResponse.postValue(NativeLoadResource.response())
                withDelay(0) {
                    _nativeLoadResponse.postValue(NativeLoadResource.failure(Exception("No Internet connection")))
                }
            }
            return
        }

        // Re-adding the banner
        if (hashMap[nativeKey]?.nativeStatus == NativeStatus.using) {
            Log.e(TAG_ADS, "$nativeKey -> loadNativeAd: An Ad is added again")
            _nativeLoadResponse.postValue(NativeLoadResource.response())
            return
        }

        // Checking for pre-loaded
        hashMap.forEach {
            if (it.value.nativeStatus == NativeStatus.free) {
                Log.e(TAG_ADS, "$nativeKey -> loadBannerAd: An Ad is available to use: Using now")
                _nativeLoadResponse.postValue(NativeLoadResource.response())
                return
            }
        }

        if (isLoading) {
            Log.e(TAG_ADS, "$nativeKey -> loadNativeAd: An Ad is getting load")
            _nativeLoadResponse.postValue(NativeLoadResource.response())
            return
        }

        if (!isBillingRequired) {
            Log.e(TAG_ADS, "$nativeKey -> loadNativeAd: User has premium access")
            _nativeLoadResponse.postValue(NativeLoadResource.response())
            withDelay(0) {
                _nativeLoadResponse.postValue(NativeLoadResource.failure(Exception("User has premium access")))
            }
            return
        }

        if (remoteConfigValue != 1) {
            Log.e(TAG_ADS, "$nativeKey -> loadNativeAd: Remote Configuration: Ad is off")
            _nativeLoadResponse.postValue(NativeLoadResource.response())
            withDelay(0) {
                _nativeLoadResponse.postValue(NativeLoadResource.failure(Exception("Remote Configuration: Ad is off")))
            }
            return
        }

        isLoading = true
        Log.d(TAG_ADS, "$nativeKey -> loadNativeAd: loading...")

        // Timeout
        if (timeOut > 0) {
            withDelay(timeOut) {
                val temp = nativeLoadResponse.value
                Log.e(TAG_ADS, "$nativeKey -> loadNativeAd: Time out : $timeOut milliseconds")
                _nativeLoadResponse.postValue(NativeLoadResource.timeout())
                withDelay(0) {
                    temp?.let { _nativeLoadResponse.postValue(it) }
                }
            }
        } else {
            _nativeLoadResponse.postValue(NativeLoadResource.loading())
        }

        CoroutineScope(Dispatchers.Default).launch {
            val adLoader: AdLoader = AdLoader.Builder(context, nativeAdID.trim()).forNativeAd { nativeAd ->
                /*if (context is Activity && !context.isFinishing) {
                    Log.d(TAG_ADS, "$nativeKey -> loadNativeAd: Destroying Native, fragment not found")
                    nativeAd.destroy()
                    return@forNativeAd
                }*/
                hashMap[nativeKey] = NativeValue(NativeStatus.free, viewGroup = null, nativeAd = nativeAd)
            }.withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                    Log.e(TAG_ADS, "$nativeKey -> loadNativeAd: onAdFailedToLoad: ", Exception(loadAdError.message))
                    isLoading = false
                    _nativeLoadResponse.postValue(NativeLoadResource.response())
                    withDelay(0) {
                        _nativeLoadResponse.postValue(NativeLoadResource.failure(Exception(loadAdError.message)))
                    }
                }

                override fun onAdLoaded() {
                    super.onAdLoaded()
                    Log.d(TAG_ADS, "$nativeKey -> loadNativeAd: onAdLoaded: loaded")
                    isLoading = false
                    _nativeLoadResponse.postValue(NativeLoadResource.response())
                    withDelay(0) {
                        _nativeLoadResponse.postValue(NativeLoadResource.success())
                    }
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                    Log.d(TAG_ADS, "$nativeKey -> loadNativeAd: onAdImpression: recorded")
                    hashMap[nativeKey]?.nativeStatus = NativeStatus.using
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    if (nativeKey == "appLanguage")
                        _nativeLoadResponse.postValue(NativeLoadResource.adClick())
                }
            }).withNativeAdOptions(NativeAdOptions.Builder().setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_RIGHT).build()).build()
            adLoader.loadAd(AdManagerAdRequest.Builder().build())
        }
    }

    fun showNativeAd(nativeKey: String, viewGroup: ViewGroup, viewLifecycleOwner: LifecycleOwner, allowRemovingView: Boolean = true, reloadAdCallback: (ex: Exception?) -> Unit, adClick: (() -> Unit)? = null) {
        hashMap[nativeKey]?.nativeAd?.let {
            populateLayout(nativeKey, viewGroup, it)
        } ?: kotlin.run {
            nativeLoadResponse.observe(viewLifecycleOwner) { resource ->
                try {
                    Log.d(TAG_ADS, "showNativeAd: $resource")
                    when (resource.statusAds) {
                        StatusAds.SUCCESS -> {
                            hashMap[nativeKey]?.nativeAd?.let {
                                showNativeAd(nativeKey, viewGroup, viewLifecycleOwner, allowRemovingView, reloadAdCallback)
                            } ?: reloadAdCallback.invoke(null)
                        }

                        StatusAds.FAILURE -> {
                            if (allowRemovingView) {
                                viewGroup.removeAllViews()
                                viewGroup.visibility = View.GONE
                            }
                        }

                        StatusAds.AD_CLICK -> {
                            adClick?.invoke()
                        }

                        else -> {}
                    }
                } catch (ex: Exception) {
                    Log.e(TAG_ADS, "showNativeAd: Exception: ", ex)
                    reloadAdCallback.invoke(ex)
                }
            }
        }
    }

    private fun populateLayout(nativeKey: String, container: ViewGroup, nativeAd: NativeAd) {
        Log.d(TAG_ADS, "populateLayout: $nativeAd")

        val binding = when (nativeKey == "appLanguage" || nativeKey == "intro") {
            true -> DataBindingUtil.inflate<AdmobNativeOneBinding>(LayoutInflater.from(context), R.layout.admob_native_one, null, false)
            false -> DataBindingUtil.inflate<AdmobNativeTwoBinding>(LayoutInflater.from(context), R.layout.admob_native_two, null, false)
        }
        when (binding) {
            is AdmobNativeOneBinding -> {
                binding.apply {
                    nativeAdView.apply {
                        mediaView = mvMedia

                        // refer all our views to NativeAdView
                        headlineView = mtvTitle
                        bodyView = mtvBody
                        iconView = ifvAdIcon
                        advertiserView = mtvAd
                        callToActionView = mbAction
                        starRatingView = rbRatings

                        mediaView?.setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                        setNativeAd(nativeAd)
                    }
                    // Title & Body
                    mtvTitle.text = nativeAd.headline
                    mtvBody.text = nativeAd.body

                    // ratings
                    rbRatings.rating = (nativeAd.starRating?.toFloat()) ?: 0F

                    // Ad
                    //nativeAd.advertiser?.let { tvAd.text = it }

                    // Icon
                    nativeAd.icon?.let {
                        ifvAdIcon.setImageDrawable(it.drawable)
                    } ?: kotlin.run {
                        ifvAdIcon.visibility = View.INVISIBLE
                    }

                    // Button
                    nativeAd.callToAction?.let {
                        mbAction.text = it
                    } ?: kotlin.run {
                        mbAction.visibility = View.GONE
                    }
                }
            }

            is AdmobNativeTwoBinding -> {
                binding.apply {
                    nativeAdView.apply {
                        mediaView = mvMedia

                        /*if (nativeKey == "exit") {
                            mvMedia.visibility = View.GONE
                        } else {
                            mediaView = mvMedia
                        }*/

                        // refer all our views to NativeAdView
                        headlineView = mtvTitle
                        bodyView = mtvBody
                        iconView = ifvAdIcon
                        advertiserView = mtvAd
                        callToActionView = mbAction
                        starRatingView = rbRatings

                        mediaView?.setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                        setNativeAd(nativeAd)
                    }
                    // Title & Body
                    mtvTitle.text = nativeAd.headline
                    mtvBody.text = nativeAd.body

                    // ratings
                    rbRatings.rating = (nativeAd.starRating?.toFloat()) ?: 0F

                    // Ad
                    //nativeAd.advertiser?.let { tvAd.text = it }

                    // Icon
                    nativeAd.icon?.let {
                        ifvAdIcon.setImageDrawable(it.drawable)
                    } ?: kotlin.run {
                        ifvAdIcon.visibility = View.INVISIBLE
                    }

                    // Button
                    nativeAd.callToAction?.let {
                        mbAction.text = it
                    } ?: kotlin.run {
                        mbAction.visibility = View.GONE
                    }
                }
            }
        }
        container.visibility = View.VISIBLE
        container.addCleanView(binding.root)
    }

    fun onDestroy(nativeKey: String) {
        Log.e(TAG_ADS, "$nativeKey -> loadNativeAd: onDestroy: destroyed")
        hashMap[nativeKey]?.nativeStatus = NativeStatus.none
        hashMap[nativeKey]?.viewGroup?.removeAllViews()
        hashMap[nativeKey]?.viewGroup = null
        hashMap[nativeKey]?.nativeAd?.destroy()
        hashMap[nativeKey]?.nativeAd = null
    }
}