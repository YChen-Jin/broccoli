package com.flauschcode.broccoli.testcase;

import static androidx.fragment.app.FragmentManager.TAG;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
        // 使用 UI Automator 获取当前的 UI 层次结构并保存为 XML 文件
        saveCurrentUiHierarchy("step1_initial_hierarchy");

        // 截取当前界面截图并保存
        captureScreenshot("step1_initial_screen");

        // 查找目标控件（通过 content-desc="打开抽屉式导航栏"）
        try {
            Log.d(TAG, "Finding and clicking the target control with content-desc='打开抽屉式导航栏'...");
            uiDevice.findObject(new UiSelector().description("打开抽屉式导航栏")).click();
            captureScreenshot("step2_after_click_drawer");

            // 保存点击后的 XML 层次结构
            saveCurrentUiHierarchy("step2_after_click_drawer_hierarchy");
        } catch (Exception e) {
            Log.e(TAG, "Failed to click the control: " + e.getMessage());
            Assert.fail("Control click failed.");
        }

        // 等待新界面加载
        Log.d(TAG, "Waiting for the new screen to load...");
        uiDevice.waitForIdle(2000);

        // 获取新界面的 UI 层次结构并保存为 XML 文件
        Log.d(TAG, "Dumping the new hierarchy tree...");
        saveCurrentUiHierarchy("step3_new_screen_hierarchy");

        // 截取新界面截图
        captureScreenshot("step3_new_screen");
    }



    /**
     * 保存当前的 UI 层次结构到外部存储的文件夹
     *
     * @param fileName 保存的文件名（无扩展名）
     */
    private void saveCurrentUiHierarchy(String fileName) {
        // 使用 UI Automator 获取当前的 UI 层次结构
        File uiHierarchyFile = SpinnerAutomatorUtils.dumpHierarchy();
        if (uiHierarchyFile == null || !uiHierarchyFile.exists()) {
            Log.e(TAG, "Failed to dump UI hierarchy.");
            Assert.fail("Failed to dump UI hierarchy.");
        }

        // 读取 XML 文件内容
        Log.d(TAG, "Reading the hierarchy tree...");
        String xmlContent = FileUtils.readXmlFileToString(uiHierarchyFile);

        // 保存到外部存储
        saveXmlToExternalFile(xmlContent, fileName);
    }

    /**
     * 保存 XML 内容到外部存储文件夹
     *
     * @param xmlContent 保存的 XML 内容
     * @param fileName   保存的文件名（无扩展名）
     */
    private void saveXmlToExternalFile(String xmlContent, String fileName) {
        // 获取外部存储的文件夹
        File externalDir = getInstrumentation().getTargetContext().getExternalFilesDir(null);
        if (externalDir == null) {
            Log.e(TAG, "无法访问外部存储目录");
            return;
        }

        // 创建 UI 层次结构文件夹
        File uiHierarchyDir = new File(externalDir, "UI_Hierarchy");
        if (!uiHierarchyDir.exists() && !uiHierarchyDir.mkdirs()) {
            Log.e(TAG, "无法创建 UI 层次结构目录: " + uiHierarchyDir.getAbsolutePath());
            return;
        }

        // 创建 XML 文件
        File xmlFile = new File(uiHierarchyDir, fileName + ".xml");

        // 写入 XML 内容
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(xmlFile))) {
            writer.write(xmlContent);
            Log.d(TAG, "UI 层次结构已成功保存到外部存储: " + xmlFile.getAbsolutePath());
        } catch (IOException e) {
            Log.e(TAG, "保存 XML 文件时出错: " + e.getMessage());
        }
    }


    /**
     * 截取屏幕截图并保存到指定文件名
     *
     * @param fileName 截图文件名（无扩展名）
     */
    private void captureScreenshot(String fileName) {
        // 获取外部存储的文件夹（可以用于保存截图等文件）
        File externalDir = getInstrumentation().getTargetContext().getExternalFilesDir(null);
        if (externalDir == null) {
            Log.e(TAG, "无法访问外部存储目录");
            return;
        }

        // 创建 Screenshots 文件夹
        File screenshotDir = new File(externalDir, "Screenshots");
        if (!screenshotDir.exists()) {
            if (screenshotDir.mkdirs()) {
                Log.d(TAG, "成功创建截图目录: " + screenshotDir.getAbsolutePath());
            } else {
                Log.e(TAG, "无法创建截图目录: " + screenshotDir.getAbsolutePath());
                return;
            }
        } else {
            Log.d(TAG, "截图目录已存在: " + screenshotDir.getAbsolutePath());
        }

        // 生成截图文件路径
        File screenshotFile = new File(screenshotDir, fileName + ".png");

        try {
            // 检查是否可以写入目标文件
            if (!screenshotFile.exists()) {
                if (!screenshotFile.createNewFile()) {
                    Log.e(TAG, "无法创建截图文件: " + screenshotFile.getAbsolutePath());
                    return;
                }
            }

            // 使用 UiDevice 截取屏幕并保存
            boolean success = uiDevice.takeScreenshot(screenshotFile);
            if (success) {
                Log.d(TAG, "截图成功保存到: " + screenshotFile.getAbsolutePath());
            } else {
                Log.e(TAG, "截图失败: " + screenshotFile.getAbsolutePath());
            }
        } catch (IOException e) {
            Log.e(TAG, "保存截图时发生错误: " + e.getMessage(), e);
        } catch (Exception e) {
            Log.e(TAG, "未知错误: " + e.getMessage(), e);
        }
    }
}


