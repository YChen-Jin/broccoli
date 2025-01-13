package com.flauschcode.broccoli.util;

import static androidx.fragment.app.FragmentManager.TAG;

import android.graphics.Rect;
import android.util.Log;

import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiCollection;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import mo.must.common.util.FileUtils;
import mo.must.common.util.UIAutomatorUtils;

/**
 * UIAutomator 基础 Android UI 测试的实用工具类。
 * 该类提供了使用 UI Automator 与 UI 元素交互的方法。
 *
 * @author chenhui cui
 * @date 2023-10-15
 */
public class SpinnerAutomatorUtils {
    private static UiDevice uiDevice;

    /**
     * 初始化 UIAutomatorUtils，使用提供的 UiDevice 实例。
     *
     * @param uiDeviceInstance 用于 UI 交互的 UiDevice 实例。
     */
    public static void init(UiDevice uiDeviceInstance) {
        uiDevice = uiDeviceInstance;
    }

    /**
     * 调用 dumpWindowHierarchy 方法来获取当前界面的 UI 层次结构并保存到 xml 文件。
     *
     * @return 代表 UI 层次结构文件的 File 对象。
     */
    public static File dumpHierarchy() {
        try {
            File uiHierarchyFile = new File(FileUtils.UI_HIERARCHY_FILE);
            uiDevice.dumpWindowHierarchy(uiHierarchyFile);
            FileUtils.displayUiHierarchyFile(uiHierarchyFile); // log
            return uiHierarchyFile;
        } catch (IOException e) {
            Log.e(TAG, "uiHierarchyFile 写入 android 设备失败");
            e.printStackTrace();
        }
        return null;
    }

    public static UiObject findViewById(String id) {
        return uiDevice.findObject(new UiSelector().resourceId(id));
    }

    public static UiObject2 findViewByTypeName(String typeName) {
        return uiDevice.findObject(By.clazz(typeName));
    }

    public static List<UiObject2> findAllUiObjectsByTypeName(String typeName) {
        return uiDevice.findObjects(By.clazz(typeName));
    }

    public static UiObject2 findViewByTypeClass(Class<Object> type) {
        return uiDevice.findObject(By.clazz(type));
    }

    public static List<UiObject2> findAllUiObjects() {
        return uiDevice.findObjects(By.depth(0));

    }

    public static Map<String, String> getAbsoluteAdjacentTexts(String id) {
        return getAdjacentTexts(id, true);
    }

    public static Map<String, String> getAdjacentTexts(String id) {
        return getAdjacentTexts(id, false);
    }

    private static Map<String, String> getAdjacentTexts(String id, boolean isAbsolute) {
        if (Objects.equals(id, "")) {
            return Collections.emptyMap();
        }

        Map<String, String> textMap = new HashMap<>();
        for (AdjacentTexts.Direction direction : AdjacentTexts.Direction.values()) {
            String text = isAbsolute ? getAdjacentText(id, direction, true) : getAdjacentText(id, direction, false);
            textMap.put(direction.name(), text);
        }
        return textMap;
    }


    public static Map<String, UiObject2> getAbsoluteAdjacentObjects(String id) {
        return getAdjacentObjects(id, true);
    }

    public static Map<String, UiObject2> getAdjacentObjects(String id) {
        return getAdjacentObjects(id, false);
    }

    private static Map<String, UiObject2> getAdjacentObjects(String id, boolean isAbsolute) {
        if (id == null || id.equals("")) {
//            Log.e(TAG, "ID为空====");
            return Collections.emptyMap();
        }

        Map<String, UiObject2> textMap = new HashMap<>();
        for (AdjacentTexts.Direction direction : AdjacentTexts.Direction.values()) {
            UiObject2 text = isAbsolute ? getAdjacentObject(id, direction, true) : getAdjacentObject(id, direction, false);

//            Log.i(TAG, "getAdjacentObjects: " + text);
            textMap.put(direction.name(), text);
        }
        return textMap;
    }

    private static UiObject2 getAdjacentObject(String id, AdjacentTexts.Direction direction, boolean isAbsolute) {
        UiObject2 closestText = null;
        try {
            Rect bounds = findViewById(id).getBounds();
            List<UiObject2> textObjects = uiDevice.findObjects(By.clazz(android.widget.TextView.class));

            int minDistance = Integer.MAX_VALUE;

            for (UiObject2 textObject : textObjects) {
                Rect textBounds = textObject.getVisibleBounds();

                int textCenterX = (textBounds.left + textBounds.right) / 2;
                int textCenterY = (textBounds.top + textBounds.bottom) / 2;

                int distance;

                if (direction == AdjacentTexts.Direction.ABOVE && textBounds.bottom < bounds.top) {
                    distance = bounds.top - textBounds.bottom;
                } else if (direction == AdjacentTexts.Direction.BELOW && textBounds.top > bounds.bottom) {
                    distance = textBounds.top - bounds.bottom;
                } else if (direction == AdjacentTexts.Direction.LEFT && textBounds.right < bounds.left) {
                    distance = bounds.left - textBounds.right;
                } else if (direction == AdjacentTexts.Direction.RIGHT && textBounds.left > bounds.right) {
                    distance = textBounds.left - bounds.right;
                } else {
                    continue; // 跳过不符合方向的文本控件
                }

                if (!isAbsolute || (direction == AdjacentTexts.Direction.ABOVE || direction == AdjacentTexts.Direction.BELOW ?
                        (textCenterX >= bounds.left && textCenterX <= bounds.right) :
                        (textCenterY >= bounds.top && textCenterY <= bounds.bottom))) {
                    if (distance < minDistance) {
                        minDistance = distance;
                        closestText = textObject;
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in getAdjacentText: ", e);
        }
        return closestText;
    }

    private static String getAdjacentText(String id, AdjacentTexts.Direction direction, boolean isAbsolute) {
        UiObject2 closestText = getAdjacentObject(id, direction, isAbsolute);
        return (closestText != null) ? closestText.getText() : null;
    }

    public static String getSpinnerDefaultValueById(String id) {
        try {
            UiCollection uiCollection = new UiCollection(new UiSelector().resourceId(id));
            UiObject spinner = uiCollection.getChild(new UiSelector().className("android.widget.TextView"));

            return spinner.getText();
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getClassNameById(String id) {
        try {
            UiObject element = findViewById(id);
            return element.getClassName();
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getClassSimpleNameById(String id) {
        if (id == null || id.equals("")) {
            return "";
        }
        String name = getClassNameById(id);
        return name.substring(name.lastIndexOf(".") + 1);
    }

    public static String getClassSimpleName(UiObject2 obj) {
        if (obj == null) {
            Log.e(TAG, "对象为null");
            return "";
        }
        String name = obj.getClassName();
        return name.substring(name.lastIndexOf(".") + 1);
    }

    public static String getClassSimpleNameByFullName(String fullName) {
        if (fullName == null || fullName.equals("")) {
            return "";
        }
        String[] parts = fullName.split("\\.");  // 使用正则表达式中的转义符号
        return parts == null || parts.length < 1 ? "" : parts[parts.length - 1];
    }

    public static String getIdOfUiObject2(UiObject2 object2) {
        String resourceId = object2.getResourceName();

        return resourceId == null ? "" : resourceId;
    }
}
