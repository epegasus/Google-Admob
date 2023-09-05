package dev.pegasus.admob.utils

import android.app.Activity
import android.content.Context
import android.util.Log

/**
 * @Author: SOHAIB AHMED
 * @Date: 9/5/2023
 * @Accounts
 *      -> https://github.com/epegasus
 *      -> https://stackoverflow.com/users/20440272/sohaib-ahmed
 */

internal object LogUtils {

    private const val TAG = "MyTag"
    const val TAG_ADS = "ADS_INFO_TAG"
    private const val REMOTE_TAG = "REMOTE_CONFIG_INFO_TAG"

    /**
     * @param context (this will show class/file name e.g MainActivity, FragmentHome)
     * @param functionName (this will show from where log has been called e.g. onBindViewHolder)
     * @param title (Exception, Error, Result, etc)
     */

    fun showLog(context: Context, functionName: String, title: String, message: String) {
        var className = "Unknown Class"
        try {
            //className = (context as Activity).findNavController(R.id.fcv_nav_Main).currentDestination?.let { it.displayName.split("/")[1] }.toString()
        } catch (ex: IllegalStateException) {
            className = (context as Activity).javaClass.simpleName
        } catch (ex: RuntimeException) {
            className = context.javaClass.simpleName
        } catch (ex: Exception) {
            className = "Unknown"
        } finally {
            Log.d(TAG, "$className: $functionName: $title: $message")
        }
    }

    fun showRemoteConfigLog(context: Context, functionName: String, title: String, message: String) {
        var className = "Unknown Class"
        try {
            //className = (context as Activity).findNavController(R.id.fcv_nav_Main).currentDestination?.let { it.displayName.split("/")[1] }.toString()
        } catch (ex: IllegalStateException) {
            className = (context as Activity).javaClass.simpleName
        } finally {
            Log.d(REMOTE_TAG, "$className: $functionName: $title: $message")
        }
    }

    fun showAdsLog(context: Context, functionName: String, title: String, message: String) {
        var className = "Unknown Class"
        className = try {
            context.javaClass.simpleName
        } catch (ex: IllegalStateException) {
            (context as Activity).javaClass.simpleName
        } catch (ex: RuntimeException) {
            context.javaClass.simpleName
        } catch (ex: Exception) {
            "Unknown"
        } finally {
            Log.d(TAG_ADS, "$className: $functionName: $title: $message")
        }
    }
}