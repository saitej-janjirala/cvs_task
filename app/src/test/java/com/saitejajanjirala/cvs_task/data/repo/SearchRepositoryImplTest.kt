package com.saitejajanjirala.cvs_task.data.repo

import app.cash.turbine.test
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

@OptIn(ExperimentalCoroutinesApi::class)
class SearchRepositoryImplTest{
    private lateinit var repository: SearchRepositoryImpl
    private lateinit var apiService: ApiService
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp(){
        Dispatchers.setMain(testDispatcher)
        apiService = mockk<ApiService>()
        repository = SearchRepositoryImpl(apiService)
    }

    @Test
    fun `searchImages emits loading then success when API call is successful`() = runTest {
        val query = "test query"
        val mockItems = listOf(Item("title", "description", "link", "author"))
        val mockResponse = SearchResult(items = mockItems)

        coEvery { apiService.searchImages(query) } returns Response.success(mockResponse)

        repository.searchImages(query).test {
            assert(awaitItem() is Result.Loading)
            val result = awaitItem()
            assert(result is Result.Success && result.d == mockItems)
            awaitComplete()
        }
    }

    @Test
    fun `searchImages emits loading then error when API call fails`() = runTest {
        val query = "test query"
        val m = "Api failed"
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
            awaitComplete()
        }
    }

    @Test
    fun `searchImages emits loading then error when API response body is null`() = runTest {
        val query = "test query"
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