package com.example.simpleuitest;

import android.content.pm.ActivityInfo;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class EspressoTest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void rotateFromPortraitToLandscape() {
        activityRule.getScenario().onActivity(activity ->
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT));
        onView(withId(R.id.button)).check(matches(withText("Tap on me")));
        onView(withId(R.id.button)).perform(click());
        onView(withId(R.id.button)).check(matches(withText("1 tap")));
        onView(withId(R.id.button)).perform(click());
        onView(withId(R.id.button)).check(matches(withText("2 tap")));
        onView(withId(R.id.editText)).perform(clearText());
        onView(withId(R.id.editText)).perform(typeText("Espresso Test"));
        activityRule.getScenario().onActivity(activity ->
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE));
        onView(withId(R.id.button)).check(matches(withText("Tap on me")));
        onView(withId(R.id.editText)).check(matches(withText("Espresso Test")));
    }

    @Test
    public void rotateFromLandscapeToPortrait() {
        activityRule.getScenario().onActivity(activity ->
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE));
        onView(withId(R.id.button)).check(matches(withText("Tap on me")));
        onView(withId(R.id.button)).perform(click());
        onView(withId(R.id.button)).check(matches(withText("1 tap")));
        onView(withId(R.id.button)).perform(click());
        onView(withId(R.id.button)).check(matches(withText("2 tap")));
        onView(withId(R.id.editText)).perform(clearText());
        onView(withId(R.id.editText)).perform(typeText("Espresso Test"));
        activityRule.getScenario().onActivity(activity ->
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT));
        onView(withId(R.id.button)).check(matches(withText("Tap on me")));
        onView(withId(R.id.editText)).check(matches(withText("Espresso Test")));
    }
}
