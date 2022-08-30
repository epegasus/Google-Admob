package dev.epegasus.googleadmob.helper.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast

object GeneralUtils {

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun withDelay(delay: Long = 300, block: () -> Unit) {
        Handler(Looper.getMainLooper()).postDelayed(Runnable(block), delay)
    }
}