package dev.epegasus.googleadmob.utils

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import dev.epegasus.googleadmob.utils.LogUtils.showLog
import java.io.File
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log10
import kotlin.math.pow

object GeneralUtils {

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun withDelay(delay: Long = 300, block: () -> Unit) {
        Handler(Looper.getMainLooper()).postDelayed(Runnable(block), delay)
    }

    /*fun loadGlide(context: Context, bitmap: Bitmap?, progressBar: ProgressBar?, imageView: ShapeableImageView) {
        progressBar?.visibility = View.VISIBLE
        bitmap?.let {
            Glide
                .with(context)
                .load(it)
                .addListener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        progressBar?.visibility = View.GONE
                        return true
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        progressBar?.visibility = View.GONE
                        return false
                    }
                })
                .into(imageView)
        } ?: showLog(context, "loadGlide", "Failed to load Glide", "Bitmap is null")
    }*/

    fun getFileSize(size: Long): String {
        if (size <= 0) return "0"
        val units = arrayOf("B", "kB", "MB", "GB", "TB")
        val digitGroups = (log10(size.toDouble()) / log10(1024.0)).toInt()
        return DecimalFormat("#,##0.#").format(size / 1024.0.pow(digitGroups.toDouble()))
            .toString() + " " + units[digitGroups]
    }

    fun getDate(millis: Long): String {
        if (millis == 0L) {
            return "Jan 01, 1970"
        }
        val lastModDate = Date(millis)
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return dateFormat.format(lastModDate)
    }

    fun getLastModifiedDate(pdfFilePath: File): String {
        val lastModDate = Date(pdfFilePath.lastModified())
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return dateFormat.format(lastModDate)
    }

    fun isInternetConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nw = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    fun hideKeyboard(activity: Activity) {
        try {
            val inputMethodManager: InputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            var view: View? = activity.currentFocus
            if (view == null) {
                view = View(activity)
            }
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        } catch (ex: Exception) {
            showLog(activity, "hideKeyboard", "FragmentHome", ex.toString())
        }
    }

    fun showStatusBar(activity: Activity) {
        activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
    }

    fun hideStatusBar(activity: Activity) {
        activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
    }
}