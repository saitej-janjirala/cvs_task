package com.saitejajanjirala.cvs_task.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.saitejajanjirala.cvs_task.domain.converters.ItemConverters
import com.saitejajanjirala.cvs_task.domain.converters.MediaConverter
import com.saitejajanjirala.cvs_task.domain.network.SearchResult

@Database(entities = [SearchResult::class], version = 1, exportSchema = false)
@TypeConverters(ItemConverters::class,MediaConverter::class)
abstract class DatabaseService : RoomDatabase() {
    abstract val searchDao: SearchDao
    companion object {
        const val DATABASE_NAME = "items.db"
    }
}