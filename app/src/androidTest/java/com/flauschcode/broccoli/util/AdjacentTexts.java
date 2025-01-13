package com.flauschcode.broccoli.util;

import java.util.Map;

/**
 * 代表一组相邻文本信息的类。
 * 使用一个映射来存储各个方向上的相邻文本内容。
 *
 * @author cuichenhui
 * @date 2023-10-23
 */
public class AdjacentTexts {

    // 存储相邻文本信息的映射
    private final Map<String, String> adjacentTextMap;

    /**
     * AdjacentTexts的构造方法，用于初始化相邻文本信息的映射。
     *
     * @param adjacentTextMap 提供的存储相邻文本信息的映射
     */
    public AdjacentTexts(Map<String, String> adjacentTextMap) {
        this.adjacentTextMap = adjacentTextMap;
    }

    /**
     * 根据指定的方向获取相应的相邻文本内容。
     *
     * @param direction 指定的方向，例如 "ABOVE"、"BELOW"、"LEFT" 或 "RIGHT"
     * @return 如果映射中存在该方向的相邻文本则返回其内容，否则返回空字符串
     */
    public String getTextForDirection(Direction direction) {
        return adjacentTextMap.getOrDefault(direction.name(), "");
    }

    /**
     * 定义可能的文本相邻方向的枚举。
     */
    public enum Direction {
        ABOVE,  // 上方
        BELOW,  // 下方
        LEFT,   // 左侧
        RIGHT   // 右侧
    }
}
