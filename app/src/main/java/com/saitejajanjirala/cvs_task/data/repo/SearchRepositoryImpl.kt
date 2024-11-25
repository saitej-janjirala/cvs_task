package com.saitejajanjirala.cvs_task.data.repo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.core.content.FileProvider
import coil.ImageLoader
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.saitejajanjirala.cvs_task.data.db.SearchDao
import com.saitejajanjirala.cvs_task.data.remote.ApiService
import com.saitejajanjirala.cvs_task.di.NoInternetException
import com.saitejajanjirala.cvs_task.domain.network.Item
import com.saitejajanjirala.cvs_task.domain.repo.SearchRepository

import javax.inject.Inject
import com.saitejajanjirala.cvs_task.domain.util.Result
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class SearchRepositoryImpl @Inject constructor(
    @ApplicationContext
    private val context: Context,
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

    override suspend fun getFileUri(url: String): Flow<Uri?> =flow{
        getBitMap(url).catch {
            emit(null)
        }.collect{bitmap->
            val uri = withContext(Dispatchers.IO) {
                val cacheDir = File(context.cacheDir, "shared_images")
                cacheDir.mkdirs()

                val file = File(cacheDir, "image${System.currentTimeMillis()}.png")
                FileOutputStream(file).use { fos ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 80, fos)
                }
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
            }
            emit(uri)
        }

    }

     suspend fun getBitMap(url: String): Flow<Bitmap> = flow{
            val loader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(url)
                .allowHardware(false)
                .build()
            val result = loader.execute(request)
            if (result is SuccessResult) {
                emit((result.drawable as BitmapDrawable).bitmap)
            } else if (result is ErrorResult) {
                throw Exception("Failed to load image")
            }

    }.catch {
        throw Exception("Failed to load image")
    }
}