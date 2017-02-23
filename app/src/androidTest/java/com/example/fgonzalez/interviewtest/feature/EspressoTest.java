/*******************************************************************************
 * Copyright (c) 2017 Francisco Gonzalez-Armijo Riádigos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.example.fgonzalez.interviewtest.feature;

import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.test.filters.SdkSuppress;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.fgonzalez.domain.executor.ThreadExecutor;
import com.example.fgonzalez.interviewtest.AndroidApplication;
import com.example.fgonzalez.interviewtest.DispatcherActivity;
import com.example.fgonzalez.interviewtest.EspressoTestRunner;
import com.example.fgonzalez.interviewtest.IdlingResourceScheduler;
import com.example.fgonzalez.interviewtest.MockAndroidApplication;
import com.example.fgonzalez.interviewtest.R;
import com.example.fgonzalez.interviewtest.internal.di.components.ApplicationComponent;
import com.example.fgonzalez.interviewtest.internal.di.components.DaggerApplicationComponent;
import com.example.fgonzalez.interviewtest.internal.di.modules.ApplicationModule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Named;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Espresso Tests.
 */
@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion=17)
public class EspressoTest {

    private Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
    private EspressoTestRunner runner = (EspressoTestRunner) instrumentation;
    private CountingIdlingResource concurrentIdlingResource;

    /**
     * We override what is inside one of our Providers in order to handle ourselves the Threads
     * using the IdlingResource Espresso functionality.
     *
     * Even if this is a bad practice, from what Dagger Devs say (https://google.github.io/dagger/testing.html),
     * we are not really overriding any bindings, but just subclassing a module in order to get control over its content.
     * So we actually expect all dependencies to be loaded as usual.
     */
    private class MockApplicationModule extends ApplicationModule {

        MockApplicationModule(AndroidApplication application) {
            super(application);
        }

        @Override
        public ThreadExecutor provideConcurrentExecutor(@Named("AvailableProcessors") int threads) {
            ThreadExecutor threadExecutor = super.provideConcurrentExecutor(threads);

            IdlingResourceScheduler wrapped =
                    new IdlingResourceScheduler(threadExecutor.getScheduler(), "RxJava New ConcurrentExecutor Thread Scheduler");

            concurrentIdlingResource = wrapped.countingIdlingResource();
            Espresso.registerIdlingResources(concurrentIdlingResource);

            return () -> wrapped;
        }
    }

    /**
     * Since we are going to override DI modules on setup, we'll manage to launch the Activity ourselves.
     */
    @Rule
    public ActivityTestRule<DispatcherActivity> mActivityRule = new ActivityTestRule<>(DispatcherActivity.class,
            true,      // initialTouchMode
            false);    // launchActivity. False so we can customize the intent per test method

    /**
     * Overriding DI modules.
     */
    @Before
    public void setUp() {
        MockAndroidApplication app = (MockAndroidApplication) instrumentation.getTargetContext().getApplicationContext();
        ApplicationComponent applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new MockApplicationModule(app)) .build();
        app.setComponent(applicationComponent);

        runner.registerIdlingResources();
    }

    @After
    public void tearDown() {
        runner.unregisterIdlingResources();
        if(concurrentIdlingResource != null) {
            Espresso.unregisterIdlingResources(concurrentIdlingResource);
        }
    }

    /**
     * Checks whether the flow is working as expected.
     *
     * I'm not going to test data, I'm only testing whether Views are displayed or not,
     * which means there were loaded successfully.
     */
    @Test
    public void checkTheFlow() {
        mActivityRule.launchActivity(new Intent());
        // Check whether Loading view is displayed at first.
        onView(withId(R.id.loading)).check(matches(isDisplayed()));
        // Once is loaded, make sure the RecyclerView is displayed (meaning it has items).
        onView(withId(R.id.post_list)).check(matches(isDisplayed()));
        // Click on the first item (execution should wait).
        onView(withId(R.id.post_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        // Check Avatar is displayed (meaning details page is shown).
        onView(withId(R.id.avatar)).check(matches(isDisplayed()));
    }

}
