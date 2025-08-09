package com.tommy.rulesengine.util;


import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Properties到List<Map>的纯结构转换工具
 * - 不处理任何类型转换
 * - 所有值保持原始字符串格式
 * - 完全解耦业务逻辑
 * @author zhanghao
 */
public class PropertiesUtil {

    private static final Pattern RULE_INDEX_PATTERN =
            Pattern.compile("rules\\[(\\d+)\\](.*)");
    private static final Pattern CHILD_INDEX_PATTERN =
            Pattern.compile("children\\[(\\d+)\\](.*)");
    private static final Pattern ARRAY_INDEX_PATTERN =
            Pattern.compile("(.+?)\\[(\\d+)\\]");

    /**
     * 转换Properties为结构化Map列表（所有值为String类型）
     */
    public static List<Map<String, Object>> parse(Properties props) {
        Map<Integer, Map<String, Object>> ruleMap = new TreeMap<>();

        props.forEach((keyObj, valueObj) -> {
            String key = (String) keyObj;
            String value = (String) valueObj;

            Matcher matcher = RULE_INDEX_PATTERN.matcher(key);
            if (matcher.matches()) {
                int ruleIndex = Integer.parseInt(matcher.group(1));
                processEntry(ruleMap.computeIfAbsent(ruleIndex,
                        k -> new LinkedHashMap<>()), matcher.group(2), value);
            }
        });

        // 处理嵌套children
        ruleMap.values().forEach(rule -> {
            if (rule.containsKey("children")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> children = (List<Map<String, Object>>) rule.get("children");
                processChildren(children);
            }
        });

        return new ArrayList<>(ruleMap.values());
    }

    private static void processEntry(Map<String, Object> node,
                                     String path, String value) {
        if (path.startsWith(".children")) {
            handleChildNode(node, path, value);
        } else if (path.contains("[")) {
            handleArrayField(node, path, value);
        } else {
            node.put(path.substring(1), value); // 直接存原始字符串
        }
    }

    private static void handleChildNode(Map<String, Object> parent,
                                        String path, String value) {
        Matcher m = CHILD_INDEX_PATTERN.matcher(path);
        if (m.find()) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> children = (List<Map<String, Object>>)
                    parent.computeIfAbsent("children", k -> new ArrayList<>());

            while (children.size() <= Integer.parseInt(m.group(1))) {
                children.add(new LinkedHashMap<>());
            }
            processEntry(children.get(Integer.parseInt(m.group(1))), m.group(2), value);
        }
    }

    private static void handleArrayField(Map<String, Object> node,
                                         String path, String value) {
        Matcher m = ARRAY_INDEX_PATTERN.matcher(path);
        if (m.find()) {
            @SuppressWarnings("unchecked")
            List<String> list = (List<String>)
                    node.computeIfAbsent(m.group(1), k -> new ArrayList<>());

            while (list.size() <= Integer.parseInt(m.group(2))) {
                list.add(null);
            }
            list.set(Integer.parseInt(m.group(2)), value);
        }
    }

    private static void processChildren(List<Map<String, Object>> children) {
        children.forEach(child -> {
            if (child.containsKey("children")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> childChildren = (List<Map<String, Object>>) child.get("children");
                processChildren(childChildren);
            }
        });
    }

}
