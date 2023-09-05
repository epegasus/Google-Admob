package dev.pegasus.admob.native.enums

import androidx.annotation.Keep

/**
 * @Author: SOHAIB AHMED
 * @Date: 9/5/2023
 * @Accounts
 *      -> https://github.com/epegasus
 *      -> https://stackoverflow.com/users/20440272/sohaib-ahmed
 */

@Keep
enum class StatusAds {
    LOADING,
    RESPONSE,
    SUCCESS,
    FAILURE,
    TIMEOUT,
    AD_CLICK
}