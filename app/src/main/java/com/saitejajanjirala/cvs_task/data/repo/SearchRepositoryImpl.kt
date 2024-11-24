package com.saitejajanjirala.cvs_task.data.repo

import com.saitejajanjirala.cvs_task.data.db.SearchDao
import com.saitejajanjirala.cvs_task.data.remote.ApiService
import com.saitejajanjirala.cvs_task.di.NoInternetException
import com.saitejajanjirala.cvs_task.domain.network.Item
import com.saitejajanjirala.cvs_task.domain.repo.SearchRepository

import javax.inject.Inject
import com.saitejajanjirala.cvs_task.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class SearchRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val searchDao: SearchDao
    ): SearchRepository {
    override suspend fun searchImages(query: String): Flow<Result<List<Item>>> = flow{
        emit(Result.Loading())
        val cachedResult = searchDao.getSearchResult(query)
        if(cachedResult?.items != null){
            emit(Result.Success(cachedResult.items))
        } else{
            val res = apiService.searchImages(query)
            if (res.isSuccessful) {
                val searchResult = res.body()
                if(searchResult!=null){
                    searchDao.insertSearchResult(searchResult.copy(query = query))
                    searchResult.items?.let {
                            it -> emit(Result.Success(it))
                    }?:emit(Result.Error("No results found"))
                }
                else{
                    emit(Result.Error("No results found"))
                }
            } else {
                emit(Result.Error(res.message()))
            }
        }
    }.catch { e ->
        when (e) {
            is NoInternetException -> emit(Result.Error(e.message ?: "No Internet Connection"))
            else -> emit(Result.Error(e.message ?: "An unknown error occurred"))
        }
    }
}