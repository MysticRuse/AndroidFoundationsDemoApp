package com.sample.android.essentialcompose.testing

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class CounterScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun initialState_isZero() {
        // Start the app
        composeTestRule.setContent {
            CounterScreen()
        }

        // Check if initial value is "Count: 0"
        composeTestRule.onNodeWithTag("count_text")
            .assertTextEquals("Count: 0")
    }

    @Test
    fun incrementButton_incrementsCount() {
        composeTestRule.setContent {
            CounterScreen()
        }

        // Click increment button
        composeTestRule.onNodeWithTag("increment_btn").performClick()

        // Verify count is 1
        composeTestRule.onNodeWithTag("count_text")
            .assertTextEquals("Count: 1")
    }

    @Test
    fun multipleIncrements_workCorrectly() {
        composeTestRule.setContent {
            CounterScreen()
        }

        // Click increment button 5 times
        repeat(5) {
            composeTestRule.onNodeWithTag("increment_btn").performClick()
        }

        // Verify count is 5
        composeTestRule.onNodeWithTag("count_text")
            .assertTextEquals("Count: 5")
    }

    @Test
    fun resetButton_setsCountToZero() {
        composeTestRule.setContent {
            CounterScreen()
        }

        // Increment count first
        repeat(3) {
            composeTestRule.onNodeWithTag("increment_btn").performClick()
        }
        composeTestRule.onNodeWithTag("count_text").assertTextEquals("Count: 3")

        // Click reset button
        composeTestRule.onNodeWithTag("reset_btn").performClick()

        // Verify count is back to 0
        composeTestRule.onNodeWithTag("count_text")
            .assertTextEquals("Count: 0")
    }
}
