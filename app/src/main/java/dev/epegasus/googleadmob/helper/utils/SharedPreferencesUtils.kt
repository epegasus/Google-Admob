package dev.epegasus.googleadmob.helper.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesUtils(context: Context) {

    // Preference Name
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("google_admob_preferences", Context.MODE_PRIVATE)

    // Keys
    private val isBillingRequireKey = "isBillingRequire"

    var isBillingRequired: Boolean
        get() = sharedPreferences.getBoolean(isBillingRequireKey, true)
        set(value) {
            sharedPreferences.edit().apply {
                putBoolean(isBillingRequireKey, value)
                apply()
            }
        }
}