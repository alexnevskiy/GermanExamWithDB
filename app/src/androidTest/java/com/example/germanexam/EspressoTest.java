package com.example.germanexam;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
public class EspressoTest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    private void homeScreenCheck() {
        onView(withId(R.id.buttonStart)).check(matches(isDisplayed()));
        onView(withId(R.id.editTextTextPersonName)).check(matches(isDisplayed()));
        onView(withId(R.id.editTextTextPersonSurname)).check(matches(isDisplayed()));
        onView(withId(R.id.editTextTextPersonClass)).check(matches(isDisplayed()));
    }

    private void menuCheck() {
        onView(withId(R.id.button_exam)).check(matches(isDisplayed()));
        onView(withId(R.id.button_variants)).check(matches(isDisplayed()));
        onView(withId(R.id.button_settings)).check(matches(isDisplayed()));
        onView(withId(R.id.bottom_navigation)).check(matches(isDisplayed()));
    }

    private void settingsCheck() {
        onView(withId(R.id.microphone_test)).check(matches(isDisplayed()));
        onView(withId(R.id.change_interface)).check(matches(isDisplayed()));
        onView(withId(R.id.about_application)).check(matches(isDisplayed()));
        onView(withId(R.id.settings)).check(matches(isDisplayed()));
        onView(withId(R.id.student_name)).check(matches(isDisplayed()));
        onView(withId(R.id.person_class)).check(matches(isDisplayed()));
    }

    private void variantsCheck() {
        for (int i = 0; i < 25; i++) {
            onView(withText(Integer.toString(i + 1))).check(matches(isDisplayed()));
        }
    }

    private void variantStartPageCheck() {
        onView(withId(R.id.start_test)).check(matches(isDisplayed()));
        onView(withId(R.id.button_start_test)).check(matches(isDisplayed()));
    }

    private void taskOneCheck() {
        onView(allOf(withId(R.id.Task1), withText("Aufgabe 1"))).check(matches(isDisplayed()));
        onView(withId(R.id.clock)).check(matches(isDisplayed()));
        onView(withId(R.id.prep_ans)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.task1_logo), withText("1"))).check(matches(isDisplayed()));
        onView(withId(R.id.task1_text)).check(matches(isDisplayed()));
        onView(withId(R.id.text1)).check(matches(isDisplayed()));
        onView(withId(R.id.preparation)).check(matches(isDisplayed()));
        onView(withId(R.id.timeline)).check(matches(isDisplayed()));
        onView(withId(R.id.time_remaining)).check(matches(isDisplayed()));
    }

    private void dialogDataChangeCheck() {
        onView(withId(R.id.editTextTextPersonName))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
        onView(withId(R.id.editTextTextPersonSurname))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
        onView(withId(R.id.editTextTextPersonClass))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
        onView(withText("Сохранить"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
    }

    private void pressBack() {
        onView(isRoot()).perform(ViewActions.pressBack());
    }

    private void dialogExamCheck() {
        onView(withText("Куда Вы хотите перейти?"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
        onView(withText("Выбор варианта"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
        onView(withText("Главное меню"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
        onView(withText("Рабочий стол"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
    }

    /**
     Тестирование навигации "вперёд"
     **/

    @Test
    public void fromHomeScreenToSettings() {
        homeScreenCheck();
        onView(withId(R.id.buttonStart)).perform(click());
        menuCheck();
        onView(withId(R.id.button_settings)).perform(click());
        settingsCheck();
    }

    @Test
    public void fromHomeScreenToVariants() {
        homeScreenCheck();
        onView(withId(R.id.buttonStart)).perform(click());
        menuCheck();
        onView(withId(R.id.button_variants)).perform(click());
        variantsCheck();
    }

    @Test
    public void fromHomeScreenToExamThroughMenu() {
        homeScreenCheck();
        onView(withId(R.id.buttonStart)).perform(click());
        menuCheck();
        onView(withId(R.id.button_exam)).perform(click());
        variantStartPageCheck();
        onView(withId(R.id.button_start_test)).perform(click());
        taskOneCheck();
    }

    @Test
    public void fromHomeScreenToExamThroughVariants() {
        homeScreenCheck();
        onView(withId(R.id.buttonStart)).perform(click());
        menuCheck();
        onView(withId(R.id.button_variants)).perform(click());
        variantsCheck();
        onView(withText("1")).perform(click());
        variantStartPageCheck();
        onView(withId(R.id.button_start_test)).perform(click());
        taskOneCheck();
    }

    /**
     Проверка глубины BackStack
     **/

    @Test
    public void simpleBackStackTest() {
        homeScreenCheck();
        onView(withId(R.id.buttonStart)).perform(click());
        menuCheck();
        onView(withId(R.id.button_variants)).perform(click());
        variantsCheck();
        pressBack();
        menuCheck();
    }

    @Test
    public void mediumBackStackTest() {
        homeScreenCheck();
        onView(withId(R.id.buttonStart)).perform(click());
        menuCheck();
        onView(withId(R.id.button_exam)).perform(click());
        variantStartPageCheck();
        onView(withId(R.id.button_start_test)).perform(click());
        taskOneCheck();
        pressBack();
        dialogExamCheck();
        onView(withText("Главное меню"))
                .inRoot(isDialog())
                .perform(click());
        menuCheck();
    }

    @Test
    public void fullBackStackTest() {
        homeScreenCheck();
        onView(withId(R.id.buttonStart)).perform(click());
        menuCheck();
        onView(withId(R.id.button_exam)).perform(click());
        variantStartPageCheck();
        onView(withId(R.id.button_start_test)).perform(click());
        taskOneCheck();
        pressBack();
        dialogExamCheck();
        onView(withText("Выбор варианта"))
                .inRoot(isDialog())
                .perform(click());
        variantsCheck();
        pressBack();
        menuCheck();
        onView(withId(R.id.button_variants)).perform(click());
        variantsCheck();
        onView(withText("1")).perform(click());
        variantStartPageCheck();
        onView(withId(R.id.button_start_test)).perform(click());
        taskOneCheck();
        pressBack();
        dialogExamCheck();
        onView(withText("Главное меню"))
                .inRoot(isDialog())
                .perform(click());
        menuCheck();
        onView(withId(R.id.button_settings)).perform(click());
        settingsCheck();
        pressBack();
        menuCheck();
    }

    /**
     Проверка нижнего меню навигации
     **/

    @Test
    public void bottomNavigationTest() {
        homeScreenCheck();
        onView(withId(R.id.buttonStart)).perform(click());
        menuCheck();
        onView(withId(R.id.bottom_navigation)).perform(click());
        dialogDataChangeCheck();
        onView(withId(R.id.editTextTextPersonName))
                .inRoot(isDialog())
                .perform(typeText("Alexander"));
        onView(withId(R.id.editTextTextPersonSurname))
                .inRoot(isDialog())
                .perform(typeText("Kobyzhev"));
        onView(withId(R.id.editTextTextPersonClass))
                .inRoot(isDialog())
                .perform(typeText("3530901/80202"));
        onView(withText("Сохранить"))
                .inRoot(isDialog())
                .perform(click());
        menuCheck();
        onView(withId(R.id.button_settings)).perform(click());
        settingsCheck();
        onView(withText("Ученик: Alexander Kobyzhev")).check(matches(isDisplayed()));
        onView(withText("Класс: 3530901/80202")).check(matches(isDisplayed()));
        onView(withId(R.id.bottom_navigation)).perform(click());
        dialogDataChangeCheck();
        onView(withId(R.id.editTextTextPersonName))
                .inRoot(isDialog())
                .perform(typeText("Ivan"));
        onView(withId(R.id.editTextTextPersonSurname))
                .inRoot(isDialog())
                .perform(typeText("Ivanov"));
        onView(withId(R.id.editTextTextPersonClass))
                .inRoot(isDialog())
                .perform(typeText("11A"));
        pressBack();
        pressBack();
        settingsCheck();
        onView(withText("Ученик: Alexander Kobyzhev")).check(matches(isDisplayed()));
        onView(withText("Класс: 3530901/80202")).check(matches(isDisplayed()));
    }
}
