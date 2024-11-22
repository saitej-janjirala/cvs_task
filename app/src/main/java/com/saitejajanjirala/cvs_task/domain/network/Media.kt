package com.saitejajanjirala.cvs_task.domain.network


import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Media(
    @Json(name = "m")
    val m: String? = null
) : Parcelable