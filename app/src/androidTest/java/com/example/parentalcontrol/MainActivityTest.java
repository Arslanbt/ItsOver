package com.example.parentalcontrol;


import androidx.test.core.app.ActivityScenario;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4ClassRunner.class)
public class MainActivityTest {




    @Test
    public void test_isVisible(){
        ActivityScenario<MainActivity> mainActivity =  ActivityScenario.launch(MainActivity.class);
        onView(withId(R.id.main_view)).check(matches(isDisplayed()));
    }


    @Test
    public void test_NameFieldVisible(){
        ActivityScenario<MainActivity> mainActivity =  ActivityScenario.launch(MainActivity.class);
        onView(withId(R.id.etWriteName)).check(matches(isDisplayed()));
    }

    @Test
    public void test_EmailFieldVisible(){
        ActivityScenario<MainActivity> mainActivity =  ActivityScenario.launch(MainActivity.class);
        onView(withId(R.id.etWriteEmail)).check(matches(isDisplayed()));
    }
    @Test
    public void test_PasswordFieldVisible(){
        ActivityScenario<MainActivity> mainActivity =  ActivityScenario.launch(MainActivity.class);
        onView(withId(R.id.etWritePassword)).check(matches(isDisplayed()));
    }


}
