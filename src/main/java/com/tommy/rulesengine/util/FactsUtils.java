package com.tommy.rulesengine.util;

import org.jeasy.rules.api.Facts;

import java.util.Map;

public class FactsUtils {
    /**
     * 安全转换Map到Facts（推荐大多数场景使用）
     * @param input 原始Map数据
     * @param ignoreUnsupportedTypes 是否跳过不支持的类型（true跳过，false抛异常）
     */
    public static Facts fromMap(Map<String, Object> input, boolean ignoreUnsupportedTypes) {
        Facts facts = new Facts();
        if (input == null) {
            return facts;
        }

        input.forEach((key, value) -> {
            if (key == null || key.trim().isEmpty()) {
                if (!ignoreUnsupportedTypes) {
                    throw new IllegalArgumentException("Fact key cannot be empty");
                }
                return;
            }

            // 基础类型检查（可根据需求扩展）
            if (value != null && !isSimpleType(value.getClass())) {
                if (!ignoreUnsupportedTypes) {
                    throw new IllegalArgumentException("Unsupported type for key '" + key + "': " + value.getClass());
                }
                return;
            }
            facts.put(key, value);
        });
        return facts;
    }

    private static boolean isSimpleType(Class<?> clazz) {
        return clazz.isPrimitive() ||
                Number.class.isAssignableFrom(clazz) ||
                String.class.isAssignableFrom(clazz) ||
                Boolean.class.isAssignableFrom(clazz);
    }
}