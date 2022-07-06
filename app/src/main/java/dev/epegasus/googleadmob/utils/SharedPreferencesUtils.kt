package dev.epegasus.googleadmob.utils

import android.content.Context
import android.content.SharedPreferences


object SharedPreferencesUtils {

    fun getIsBillingPurchased(mContext: Context): Boolean {
        val sharedPreferences: SharedPreferences = mContext.getSharedPreferences("IsBillingPurchasedPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("IsBillingPurchasedValue", false)
    }

    fun setIsBillingPurchased(mContext: Context, isActive: Boolean) {
        val sharedPreferences: SharedPreferences = mContext.getSharedPreferences("IsBillingPurchasedPrefs", Context.MODE_PRIVATE)
        val sharedPreferencesEditor: SharedPreferences.Editor = sharedPreferences.edit()
        sharedPreferencesEditor.putBoolean("IsBillingPurchasedValue", isActive)
        sharedPreferencesEditor.apply()
    }

}