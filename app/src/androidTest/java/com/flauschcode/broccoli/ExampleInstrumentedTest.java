package com.flauschcode.broccoli;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    public ActivityScenarioRule<?> activityScenarioRule;

    @Rule
    public ActivityScenarioRule<?> setRule() {
        try {
            String targetActivity = "com.flauschcode.broccoli.MainActivity";

            // 初始化ActivityScenarioRule
            activityScenarioRule = new ActivityScenarioRule(Class.forName(targetActivity));
            return activityScenarioRule;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testAppContext() throws InterruptedException {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Thread.sleep(10000);
        assertEquals("com.flauschcode.broccoli", appContext.getPackageName());
    }
}