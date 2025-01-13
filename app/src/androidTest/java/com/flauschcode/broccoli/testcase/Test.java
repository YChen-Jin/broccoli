package com.flauschcode.broccoli.testcase;

import static androidx.fragment.app.FragmentManager.TAG;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import static mo.must.common.constants.TestConstants.modelMap;

import android.util.Log;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import mo.must.common.prompt.PromptConfiguration;
import mo.must.common.test.TestAssert;
import mo.must.common.util.FileUtils;
import mo.must.common.util.RobotiumUtils;
import mo.must.common.util.UIAutomatorUtils;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.UiDevice;

import com.flauschcode.broccoli.util.SpinnerAutomatorUtils;
import com.robotium.solo.Solo;


@SuppressWarnings({"rawtypes", "unchecked"})
public class Test {
    private static final String LAUNCHER_ACTIVITY_FULL_NAME = "com.flauschcode.broccoli.MainActivity";
    private static Class<?> launcherActivityClass;

    static {
        try {
            launcherActivityClass = Class.forName(LAUNCHER_ACTIVITY_FULL_NAME);
        } catch (ClassNotFoundException e) {
            e.getStackTrace();
        }
    }

    @Rule
    public ActivityTestRule<?> activityRule = new ActivityTestRule(launcherActivityClass, true, true);

    private Solo mSolo;
    private UiDevice uiDevice;

    @Before
    public void setUp() {
        uiDevice = UiDevice.getInstance(getInstrumentation());
        mSolo = new Solo(getInstrumentation(), activityRule.getActivity());
        RobotiumUtils.init(mSolo);
        SpinnerAutomatorUtils.init(uiDevice);

        // 记录测试开始的日志
        Log.d(TAG, "Initialization complete. Starting the test.");
    }

    @org.junit.Test
    public void testWithXmlFile() {

        // 使用 UI Automator 获取当前的UI层次结构并保存为xml文件，如果获取的xml文件为null，则断言测试失败
        File uiHierarchyFile = SpinnerAutomatorUtils.dumpHierarchy();
        TestAssert.assertNotNull(uiHierarchyFile);

        // 读取xml文件内容
        Log.d(TAG, "Reading the hierarchy tree...");
        String xmlContent = FileUtils.readXmlFileToString(uiHierarchyFile);

    }
}


