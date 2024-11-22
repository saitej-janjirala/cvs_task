package com.saitejajanjirala.cvs_task.data.remote

import com.saitejajanjirala.cvs_task.domain.network.SearchResult
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("photos_public.gne")
    suspend fun searchImages(@Query("tags") query: String): Response<SearchResult>
}