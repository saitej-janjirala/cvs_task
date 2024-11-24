package com.saitejajanjirala.cvs_task.domain.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.saitejajanjirala.cvs_task.domain.network.Media

object MediaConverter {

    @TypeConverter
    fun fromMedia(media: Media?): String {
        return Gson().toJson(media)
    }

    @TypeConverter
    fun toMedia(mediaJson: String?): Media? {
        return Gson().fromJson(mediaJson, object : TypeToken<Media>() {}.type)
    }

}