package com.sample.android.composebasics.testing

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private lateinit var viewModel: LoginViewModel
    private val authRepository: AuthRepository = mockk()
    
    // Use StandardTestDispatcher for fine-grained control over execution
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = LoginViewModel(authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login with empty fields should emit Error state`() = runTest {
        viewModel.uiState.test {
            assertEquals(LoginUiState.Idle, awaitItem())
            
            viewModel.login("", "")
            
            val state = awaitItem()
            assert(state is LoginUiState.Error)
            assertEquals("Fields cannot be empty", (state as LoginUiState.Error).message)
        }
    }

    @Test
    fun `successful login should emit Loading and then Success state`() = runTest {
        val user = User("1", "test@example.com")
        // No delay here, but StandardTestDispatcher will let us capture Loading
        coEvery { authRepository.login("test@example.com", "password") } returns user

        viewModel.uiState.test {
            assertEquals(LoginUiState.Idle, awaitItem())

            viewModel.login("test@example.com", "password")

            // Advance the dispatcher to allow the coroutine to start and set Loading
            runCurrent()
            assertEquals(LoginUiState.Loading, awaitItem())

            // Advance the dispatcher again to complete the login call and set Success
            advanceUntilIdle()
            assertEquals(LoginUiState.Success(user), awaitItem())
        }
    }

    @Test
    fun `failed login should emit Loading and then Error state`() = runTest {
        val errorMessage = "Invalid credentials"
        coEvery { authRepository.login("test@example.com", "wrong") } throws Exception(errorMessage)

        viewModel.uiState.test {
            assertEquals(LoginUiState.Idle, awaitItem())

            viewModel.login("test@example.com", "wrong")

            runCurrent()
            assertEquals(LoginUiState.Loading, awaitItem())

            advanceUntilIdle()
            val state = awaitItem()
            assert(state is LoginUiState.Error)
            assertEquals(errorMessage, (state as LoginUiState.Error).message)
        }
    }

    @Test
    fun `loading state should be emitted during login process`() = runTest {
        coEvery { authRepository.login(any(), any()) } returns User("1", "test")

        viewModel.uiState.test {
            assertEquals(LoginUiState.Idle, awaitItem())
            
            viewModel.login("test@example.com", "password")
            
            runCurrent()
            assertEquals(LoginUiState.Loading, awaitItem())
            
            advanceUntilIdle()
            assert(awaitItem() is LoginUiState.Success)
        }
    }
}
