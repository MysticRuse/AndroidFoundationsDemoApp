package com.sample.android.essentialcompose.testing

import app.cash.turbine.test
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class StockPriceViewModelTest {

    private lateinit var viewModel: StockPriceViewModel
    private val repository: StockRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = StockPriceViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     * This is a complete test - why?
     * •Initial Value: Confirms the StateFlow starts at 0.0.
     * •Reactive Updates: Confirms that when the repository pushes a new value, the ViewModel
     * updates its state.
     * •Conflation/Distinctness: Confirms that StateFlow behaves as expected by not triggering UI
     * updates for the same data (which is a major performance benefit of using StateFlow over
     * SharedFlow or LiveData).
     * •Synchronization: Using runCurrent() ensures that we don't have "flaky" tests caused by the
     * background coroutine in viewModelScope not finishing its work before the assertion runs.
     */

    @Test
    fun `price should emit updates from repository over time`() = runTest {
        // Use a SharedFlow to emit values on demand
        val priceFlow = MutableSharedFlow<Double>()
        every { repository.priceUpdates() } returns priceFlow

        viewModel.price.test {
            // 1. Initial state
            assertEquals(0.0, awaitItem(), 0.0)

            viewModel.startTracking()
            runCurrent() // Allow collection to start

            // 2. First update
            priceFlow.emit(150.0)
            runCurrent() // Allow VM to process collection
            assertEquals(150.0, awaitItem(), 0.0)

            // 3. Second update
            priceFlow.emit(155.5)
            runCurrent()
            assertEquals(155.5, awaitItem(), 0.0)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `price should not emit duplicate consecutive values`() = runTest {
        val priceFlow = MutableSharedFlow<Double>()
        every { repository.priceUpdates() } returns priceFlow

        viewModel.price.test {
            awaitItem() // Skip initial 0.0
            viewModel.startTracking()
            runCurrent()

            // Emit 100.0
            priceFlow.emit(100.0)
            runCurrent()
            assertEquals(100.0, awaitItem(), 0.0)

            // Emit 100.0 again - StateFlow should ignore duplicates
            priceFlow.emit(100.0)
            runCurrent()
            expectNoEvents()

            // Emit 101.0 - Should emit now
            priceFlow.emit(101.0)
            runCurrent()
            assertEquals(101.0, awaitItem(), 0.0)
        }
    }
}
