package com.saitejajanjirala.cvs_task.data.repo

import android.content.Context
import android.graphics.Bitmap
import app.cash.turbine.test
import com.saitejajanjirala.cvs_task.data.db.SearchDao
import com.saitejajanjirala.cvs_task.data.remote.ApiService
import com.saitejajanjirala.cvs_task.di.NoInternetException
import com.saitejajanjirala.cvs_task.domain.network.Item
import com.saitejajanjirala.cvs_task.domain.network.SearchResult
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import com.saitejajanjirala.cvs_task.domain.util.Result
import com.saitejajanjirala.cvs_task.util.Util
import io.mockk.coEvery
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import kotlin.contracts.Returns

@OptIn(ExperimentalCoroutinesApi::class)
class SearchRepositoryImplTest{
    private lateinit var repository: SearchRepositoryImpl
    private lateinit var apiService: ApiService
    private lateinit var searchDao : SearchDao
    private lateinit var context: Context
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp(){
        Dispatchers.setMain(testDispatcher)
        context = mockk<Context>()
        apiService = mockk<ApiService>()
        searchDao = mockk<SearchDao>()
        repository = SearchRepositoryImpl(context,apiService,searchDao)
    }

    @Test
    fun `searchImages emits loading then success when db returns null API call is successful`() = runTest {
        val query = "test query"
        val mockItems = listOf(Item("title", "description", "link", "author"))
        val mockResponse = SearchResult(items = mockItems)
        coEvery { searchDao.getSearchResult(query) } returns null
        coEvery { searchDao.insertSearchResult(SearchResult(query = query, items = mockItems)) } returns Unit
        coEvery { apiService.searchImages(query) } returns Response.success(mockResponse)

        repository.searchImages(query).test {
            assert(awaitItem() is Result.Loading)
            val result = awaitItem()
            assert(result is Result.Success && result.d == mockItems)
            cancelAndIgnoreRemainingEvents()
        }
    }
    @Test
    fun `searchImages emits loading then success when db call is successful`() = runTest {
        val query = "test query"
        val mockItems = listOf(Item("title", "description", "link", "author"))
        coEvery { searchDao.getSearchResult(query) } returns SearchResult(query = query, items = mockItems)

        repository.searchImages(query).test {
            assert(awaitItem() is Result.Loading)
            val result = awaitItem()
            assert(result is Result.Success && result.d == mockItems)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `searchImages emits loading then error when API call fails`() = runTest {
        val query = "test query"
        val m = "Api failed"
        coEvery { searchDao.getSearchResult(query) } returns null

        coEvery { apiService.searchImages(query) } returns Response.error(
            m.toResponseBody("text/plain".toMediaType()),
                okhttp3.Response.Builder()
                    .request(
                        okhttp3.Request.Builder()
                            .url(Util.API_BASE_URL)
                            .build()
                    )
                    .protocol(okhttp3.Protocol.HTTP_1_1)
                    .code(404)
                    .message(m)
                    .build()
        )


        repository.searchImages(query).test {
            val first = awaitItem()
            assert(first is Result.Loading)
            val result = awaitItem()
            assert(result is Result.Error && result.m == m)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `searchImages emits loading then error when API response body is null`() = runTest {
        val query = "test query"
        coEvery { searchDao.getSearchResult(query) } returns null
        coEvery { apiService.searchImages(query) } returns Response.success(null)

        repository.searchImages(query).test {
            assert(awaitItem() is Result.Loading)
            val result = awaitItem()
            assert(result is Result.Error && result.m == "No results found")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `searchImages emits loading then error when exception occurs`() = runTest {
        val query = "test query"
        val exception = Exception("Network error")
        coEvery { searchDao.getSearchResult(query) } returns null

        coEvery { apiService.searchImages(query) } throws exception

        repository.searchImages(query).test {
            assert(awaitItem() is Result.Loading)
            val result = awaitItem()
            assert(result is Result.Error && result.m == "Network error")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `searchImages emits loading then error for NoInternetException`() = runTest {
        val query = "test query"
        val exception = NoInternetException("No Internet Connection")
        coEvery { searchDao.getSearchResult(query) } returns null

        coEvery { apiService.searchImages(query) } throws exception

        repository.searchImages(query).test {
            assert(awaitItem() is Result.Loading)
            val result = awaitItem()
            assert(result is Result.Error && result.m == "No Internet Connection")
            cancelAndIgnoreRemainingEvents()
        }
    }



    @After
    fun tearDown(){
        Dispatchers.resetMain()
    }
}