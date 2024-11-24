package com.saitejajanjirala.cvs_task.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.saitejajanjirala.cvs_task.domain.network.SearchResult

@Dao
interface SearchDao {
    @Query("SELECT * FROM search_results WHERE `query`=:query")
    suspend fun getSearchResult(query: String): SearchResult?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchResult(searchResult: SearchResult)
}