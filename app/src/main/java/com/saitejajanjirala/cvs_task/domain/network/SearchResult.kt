package com.saitejajanjirala.cvs_task.domain.network


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchResult(
    @Json(name = "description")
    val description: String? = null,
    @Json(name = "generator")
    val generator: String? = null,
    @Json(name = "items")
    val items: List<Item>? = null,
    @Json(name = "link")
    val link: String? = null,
    @Json(name = "modified")
    val modified: String? = null,
    @Json(name = "title")
    val title: String? = null
)