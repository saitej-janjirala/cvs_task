package com.saitejajanjirala.cvs_task.domain.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.saitejajanjirala.cvs_task.domain.network.Item

object ItemConverters {

    @TypeConverter
    fun fromItemList(items: List<Item>?): String {
        return Gson().toJson(items)
    }

    @TypeConverter
    fun toItemList(json: String?): List<Item>? {
        val type = object : TypeToken<List<Item>>() {}.type
        return Gson().fromJson(json, type)
    }
}

