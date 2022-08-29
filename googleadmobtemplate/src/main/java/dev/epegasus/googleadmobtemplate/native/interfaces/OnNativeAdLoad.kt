package dev.epegasus.googleadmobtemplate.native.interfaces

import com.google.android.gms.ads.nativead.NativeAd

interface OnNativeAdLoad {
    fun onNativeAdLoad(nativeAd: NativeAd)
}