package com.saitejajanjirala.cvs_task.domain.network


import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Item(
    @Json(name = "author")
    val author: String? = null,
    @Json(name = "author_id")
    val authorId: String? = null,
    @Json(name = "date_taken")
    val dateTaken: String? = null,
    @Json(name = "description")
    val description: String? = null,
    @Json(name = "link")
    val link: String? = null,
    @Json(name = "media")
    val media: Media ?=null,
    @Json(name = "published")
    val published: String? = null,
    @Json(name = "tags")
    val tags: String? = null,
    @Json(name = "title")
    val title: String? = null,

): Parcelable