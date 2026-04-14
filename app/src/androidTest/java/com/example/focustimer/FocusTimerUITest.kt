package com.example.focustimer

import android.view.View
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.focustimer.ui.MainActivity
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.Matcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FocusTimerUITest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    private fun waitForFocus(timeout: Long = 5000) {
        val startTime = System.currentTimeMillis()
        while (System.currentTimeMillis() - startTime < timeout) {
            var hasFocus = false
            activityRule.scenario.onActivity { activity ->
                hasFocus = activity.hasWindowFocus()
            }
            if (hasFocus) return
            Thread.sleep(200)
        }
    }

    private fun waitForView(viewMatcher: Matcher<View>, timeout: Long = 5000) {
        val startTime = System.currentTimeMillis()
        while (System.currentTimeMillis() - startTime < timeout) {
            try {
                onView(viewMatcher).check(matches(isDisplayed()))
                return
            } catch (_: Exception) {
                Thread.sleep(200)
            }
        }
        // Final check to throw original exception if it fails
        onView(viewMatcher).check(matches(isDisplayed()))
    }

    @Test
    fun testAuthenticationFlow() {
        waitForFocus()
        
        val testUser = "user_${System.currentTimeMillis()}"
        val testPass = "password123"

        // Navigate to Sign Up
        onView(withId(R.id.sign_up_nav_button)).perform(click())
        waitForFocus()

        // Fill Sign Up details
        onView(withId(R.id.username_box)).perform(replaceText(testUser))
        onView(withId(R.id.name_box)).perform(replaceText("Test User"))
        onView(withId(R.id.password_box)).perform(replaceText(testPass))

        Espresso.closeSoftKeyboard()
        
        Thread.sleep(500)
        onView(withId(R.id.sign_up_button)).perform(click())

        // Verify Home Screen reached
        waitForView(withId(R.id.welcome_text))
        onView(withId(R.id.welcome_text)).check(matches(withText(containsString("Test"))))
    }

    @Test
    fun testTaskCreationAndList() {
        waitForFocus()
        
        val taskTitle = "UI Test Task ${System.currentTimeMillis()}"
        
        // Login first
        performTestLogin()

        // Wait for Home screen
        waitForView(withId(R.id.to_do_list_button))

        // Navigate to To-Do List
        onView(withId(R.id.to_do_list_button)).perform(click())
        waitForFocus()

        // Click Add Task
        onView(withId(R.id.add_task_button)).perform(click())
        waitForFocus()

        // Fill Task details
        onView(withId(R.id.titleEditText)).perform(replaceText(taskTitle))
        onView(withId(R.id.descriptionEditText)).perform(replaceText("Detailed description for UI test"))
        
        Espresso.closeSoftKeyboard()
        Thread.sleep(500)

        // Select Date
        onView(withId(R.id.datePickerButton)).perform(click())
        Thread.sleep(1000)
        onView(withId(android.R.id.button1)).perform(click())

        waitForFocus()

        // Save
        onView(withId(R.id.saveTaskButton)).perform(click())

        // Verify task appears in list
        waitForView(withText(taskTitle))
    }

    @Test
    fun testTimerMethodSelection() {
        waitForFocus()
        performTestLogin()
        waitForView(withId(R.id.start_a_focus_session_button))

        // Click Start Focus Session
        onView(withId(R.id.start_a_focus_session_button)).perform(click())
        waitForFocus()

        // Select Pomodoro
        onView(withId(R.id.pomodoro_button)).perform(click())
        waitForFocus()

        // Verify Timer screen loaded
        onView(withId(R.id.timerTypeLabel)).check(matches(withText(containsString("Pomodoro"))))
        onView(withId(R.id.timerDisplay)).check(matches(withText("25:00")))
    }

    private fun performTestLogin() {
        val user = "login_test_${System.currentTimeMillis()}"
        onView(withId(R.id.sign_up_nav_button)).perform(click())
        waitForFocus()
        
        onView(withId(R.id.username_box)).perform(replaceText(user))
        onView(withId(R.id.name_box)).perform(replaceText("Tester"))
        onView(withId(R.id.password_box)).perform(replaceText("pass"))
        
        Espresso.closeSoftKeyboard()
        Thread.sleep(500)
        onView(withId(R.id.sign_up_button)).perform(click())
    }
}
