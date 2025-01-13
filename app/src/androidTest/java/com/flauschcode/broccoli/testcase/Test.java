package com.flauschcode.broccoli.testcase;

import static androidx.fragment.app.FragmentManager.TAG;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;


import android.util.Log;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;

import java.io.File;
import mo.must.common.test.TestAssert;
import mo.must.common.util.FileUtils;
import mo.must.common.util.RobotiumUtils;

import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiSelector;

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

        // 截取当前界面截图并保存
        captureScreenshot("step1_initial_screen");

        // 读取xml文件内容
        Log.d(TAG, "Reading the hierarchy tree...");
//        String xmlContent = FileUtils.readXmlFileToString(uiHierarchyFile);

        // 查找目标控件（通过 content-desc="打开抽屉式导航栏"）
        try {
            Log.d(TAG, "Finding and clicking the target control with content-desc='打开抽屉式导航栏'...");
            uiDevice.findObject(new UiSelector().description("打开抽屉式导航栏")).click();
            captureScreenshot("step2_after_click_drawer");
        } catch (Exception e) {
            Log.e(TAG, "Failed to click the control: " + e.getMessage());
            Assert.fail("Control click failed.");
        }

        // 等待新界面加载
        Log.d(TAG, "Waiting for the new screen to load...");
        uiDevice.waitForIdle(2000);

        // 获取新界面的 UI 层次结构并保存为 XML 文件
        Log.d(TAG, "Dumping the new hierarchy tree...");
        File newUiHierarchyFile = SpinnerAutomatorUtils.dumpHierarchy();
        TestAssert.assertNotNull(newUiHierarchyFile);

        // 截取新界面截图
        captureScreenshot("step3_new_screen");
    }

    /**
     * 截取屏幕截图并保存到指定文件名
     *
     * @param fileName 截图文件名（无扩展名）
     */
    private void captureScreenshot(String fileName) {
        String screenshotDir = "/sdcard/Download/Screenshots/";
        File dir = new File(screenshotDir);
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                Log.d(TAG, "Created directory: " + screenshotDir);
            } else {
                Log.e(TAG, "Failed to create directory: " + screenshotDir);
                return;
            }
        }


        // 截图文件路径
        File screenshotFile = new File(screenshotDir, fileName + ".png");
        boolean success = uiDevice.takeScreenshot(screenshotFile);

        if (success) {
            Log.d(TAG, "Screenshot saved on device: " + screenshotFile.getAbsolutePath());
        } else {
            Log.e(TAG, "Failed to capture screenshot: " + screenshotFile.getAbsolutePath());
        }
    }
}


