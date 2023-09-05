package dev.pegasus.admob.native.dataClasses

import android.view.ViewGroup
import com.google.android.gms.ads.nativead.NativeAd
import dev.pegasus.admob.native.enums.NativeStatus

/**
 * @Author: SOHAIB AHMED
 * @Date: 9/5/2023
 * @Accounts
 *      -> https://github.com/epegasus
 *      -> https://stackoverflow.com/users/20440272/sohaib-ahmed
 */

data class NativeValue(
    var nativeStatus: NativeStatus,
    var viewGroup: ViewGroup?,
    var nativeAd: NativeAd?
)
