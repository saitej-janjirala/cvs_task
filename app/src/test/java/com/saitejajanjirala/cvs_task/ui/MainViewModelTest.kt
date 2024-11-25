package com.saitejajanjirala.cvs_task.ui

import androidx.lifecycle.SavedStateHandle
import org.junit.Assert.*

import app.cash.turbine.test
import app.cash.turbine.testIn
import com.saitejajanjirala.cvs_task.domain.network.Item
import com.saitejajanjirala.cvs_task.domain.network.Media
import com.saitejajanjirala.cvs_task.domain.repo.SearchRepository
import com.saitejajanjirala.cvs_task.domain.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import org.junit.Assert.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private lateinit var viewModel: MainViewModel
    private lateinit var repository: SearchRepository
    private lateinit var savedStateHandle: SavedStateHandle

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        savedStateHandle = SavedStateHandle()
        viewModel = MainViewModel(repository, savedStateHandle,)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() = runTest {
        assertEquals("", viewModel.searchQuery.value)
        assert( viewModel.searchResults.value is Result.Idle)
    }

    @Test
    fun `updateSearchQuery updates searchQuery state`() = runTest {
        viewModel.updateSearchQuery("test query")
        assertEquals("test query", viewModel.searchQuery.value)
    }

    @Test
    fun `searchQuery triggers repository search`() = runTest {
        val mockItems = listOf(Item("Matery", "robertGreen", "2000", "media", "https://www.google.com/books/edition/Mastery/vkCKDQAAQBAJ?hl=en&gbpv=1&printsec=frontcover", Media("url"), "2012"))
        coEvery { repository.searchImages("test query") } returns flow{
            emit(Result.Loading())
            emit(Result.Success(mockItems))
        }

        viewModel.updateSearchQuery("test query")


        viewModel.searchResults.test {
            val firstItem = awaitItem()
            assert(firstItem is Result.Idle)
            val secondItem = awaitItem()
            assert(secondItem is Result.Loading)

            val thirdItem = awaitItem()
            assert(thirdItem is Result.Success)
            assertEquals(mockItems, thirdItem.d)
            cancelAndIgnoreRemainingEvents()
        }


    }

    @Test
    fun `empty query sets Result_Idle`() = runTest {

        viewModel.updateSearchQuery("")

        viewModel.searchResults.test {
            assert(awaitItem() is Result.Idle)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `repository error sets Result_Error`() = runTest {
        val msg = "Error occured during test"
        val q = "test query"
        coEvery { repository.searchImages(q) } returns flow {
            emit(Result.Loading())
            emit(Result.Error(msg))
        }

        viewModel.updateSearchQuery(q)


        viewModel.searchResults.test {
            val first = awaitItem()
            assert(first is Result.Idle )
            val second = awaitItem()
            assert(second is Result.Loading)
            val third = awaitItem()
            assert(third is Result.Error)
            assert(third.m == msg)
            cancelAndIgnoreRemainingEvents()
        }
    }


}
