package com.example.parentalcontrol;


import androidx.test.core.app.ActivityScenario;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;


@RunWith(AndroidJUnit4ClassRunner.class)
public class SignInActivityTest {


    @Test
    public void test_isVisible(){
        ActivityScenario<SignInActivity> signInActivity =  ActivityScenario.launch(SignInActivity.class);
        onView(withId(R.id.sign_up_view)).check(matches(isDisplayed()));
    }

    @Test
    public void test_EmailFieldVisible(){
        ActivityScenario<SignInActivity> signInActivity =  ActivityScenario.launch(SignInActivity.class);
        onView(withId(R.id.etWriteEmail)).check(matches(isDisplayed()));
    }
    @Test
    public void test_PasswordFieldVisible(){
        ActivityScenario<SignInActivity> signInActivity =  ActivityScenario.launch(SignInActivity.class);
        onView(withId(R.id.etWritePassword)).check(matches(isDisplayed()));
    }

    @Test
    public void test_SignUpButtonVisible(){
        ActivityScenario<SignInActivity> signInActivity =  ActivityScenario.launch(SignInActivity.class);
        onView(withId(R.id.btnSignIn)).check(matches(isDisplayed()));
    }

    @Test
    public void test_NavigateToRegisterScreen(){
        ActivityScenario<SignInActivity> signInActivity =  ActivityScenario.launch(SignInActivity.class);
        onView(withId(R.id.tvGoToSignUp)).perform(click());


        onView(withId(R.id.main_view)).check(matches(isDisplayed()));
    }
}


