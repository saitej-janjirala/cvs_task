package com.saitejajanjirala.cvs_task.domain.repo

import android.graphics.Bitmap
import android.net.Uri
import com.saitejajanjirala.cvs_task.domain.network.Item
import kotlinx.coroutines.flow.Flow
import com.saitejajanjirala.cvs_task.domain.util.Result

interface SearchRepository {
    suspend fun searchImages(query: String): Flow<Result<List<Item>>>
    suspend fun getFileUri(url: String): Flow<Uri?>
}