package dev.pegasus.admob.interstitial.interfaces

/**
 * @Author: SOHAIB AHMED
 * @Date: 9/5/2023
 * @Accounts
 *      -> https://github.com/epegasus
 *      -> https://stackoverflow.com/users/20440272/sohaib-ahmed
 */

interface InterstitialOnLoadCallBack {
    fun onAdFailedToLoad()
    fun onAdLoaded()
    fun onResponse()
}