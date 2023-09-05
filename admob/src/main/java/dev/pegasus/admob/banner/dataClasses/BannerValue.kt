package dev.pegasus.admob.banner.dataClasses

import android.view.ViewGroup
import com.google.android.gms.ads.AdView
import dev.pegasus.admob.banner.enums.BannerStatus

/**
 * @Author: SOHAIB AHMED
 * @Date: 9/5/2023
 * @Accounts
 *      -> https://github.com/epegasus
 *      -> https://stackoverflow.com/users/20440272/sohaib-ahmed
 */

data class BannerValue(
    var bannerStatus: BannerStatus,
    var viewGroup: ViewGroup?,
    var adView: AdView?
)
