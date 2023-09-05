package dev.pegasus.admob.native.resources

import androidx.annotation.Keep
import dev.pegasus.admob.native.enums.StatusAds

/**
 * @Author: SOHAIB AHMED
 * @Date: 9/5/2023
 * @Accounts
 *      -> https://github.com/epegasus
 *      -> https://stackoverflow.com/users/20440272/sohaib-ahmed
 */

@Keep
data class NativeLoadResource(val statusAds: StatusAds, val exception: Exception?) {

    companion object {
        fun loading(): NativeLoadResource {
            return NativeLoadResource(statusAds = StatusAds.LOADING, exception = null)
        }

        fun response(): NativeLoadResource {
            return NativeLoadResource(statusAds = StatusAds.RESPONSE, exception = null)
        }

        fun success(): NativeLoadResource {
            return NativeLoadResource(statusAds = StatusAds.SUCCESS, exception = null)
        }

        fun failure(exception: Exception): NativeLoadResource {
            return NativeLoadResource(statusAds = StatusAds.FAILURE, exception = exception)
        }

        fun timeout(): NativeLoadResource {
            return NativeLoadResource(statusAds = StatusAds.TIMEOUT, exception = null)
        }

        fun adClick(): NativeLoadResource {
            return NativeLoadResource(statusAds = StatusAds.AD_CLICK, exception = null)
        }
    }
}