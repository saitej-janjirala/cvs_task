package com.saitejajanjirala.cvs_task.domain.network


import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Entity(tableName = "search_results")

data class SearchResult(
    @Json(name = "items")
    val items: List<Item>? = null,
    val query:String="",
    @PrimaryKey(autoGenerate = true)
    val id : Int=0,
)