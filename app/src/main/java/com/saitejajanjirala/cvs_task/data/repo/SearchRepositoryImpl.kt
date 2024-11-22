package com.saitejajanjirala.cvs_task.data.repo

import com.saitejajanjirala.cvs_task.data.remote.ApiService
import com.saitejajanjirala.cvs_task.di.NoInternetException
import com.saitejajanjirala.cvs_task.domain.network.Item
import com.saitejajanjirala.cvs_task.domain.repo.SearchRepository

import javax.inject.Inject
import com.saitejajanjirala.cvs_task.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class SearchRepositoryImpl @Inject constructor(private val apiService: ApiService): SearchRepository {
    override suspend fun searchImages(query: String): Flow<Result<List<Item>>> = flow{
        emit(Result.Loading())
        val res = apiService.searchImages(query)
        if (res.isSuccessful) {
            res.body()?.items?.let {
                emit(Result.Success(it))
            } ?: emit(Result.Error("No results found"))
        } else {
            emit(Result.Error(res.message()))
        }
    }.catch { e ->
        when (e) {
            is NoInternetException -> emit(Result.Error(e.message ?: "No Internet Connection"))
            else -> emit(Result.Error(e.message ?: "An unknown error occurred"))
        }
    }
}