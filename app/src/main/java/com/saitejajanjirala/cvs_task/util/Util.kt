package com.saitejajanjirala.cvs_task.util

import android.net.ConnectivityManager
import android.net.NetworkCapabilities

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

object Util {
    const val API_BASE_URL = "https://api.flickr.com/services/feeds/"


    fun ConnectivityManager.isInternetAvailable():Boolean{
        val network = this.activeNetwork ?: return false
        val capabilities = this.getNetworkCapabilities(network) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
    }


    fun formatPublishedDateLegacy(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC") // Ensure parsing is in UTC
            val outputFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
            val date = inputFormat.parse(dateString) // Parse input string
            outputFormat.format(date) // Format to desired output
        } catch (e: Exception) {
            "Invalid Date"
        }
    }

}