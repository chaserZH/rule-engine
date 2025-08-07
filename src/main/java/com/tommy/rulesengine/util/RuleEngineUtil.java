package com.tommy.rulesengine.util;

import com.tommy.rulesengine.constants.FileType;
import com.tommy.rulesengine.engine.RuleEngine;
import com.tommy.rulesengine.model.RuleGroup;
import com.tommy.rulesengine.model.RuleResult;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;

/**
 * 接入方统一接入入口
 */
public class RuleEngineUtil {

    private static final RuleEngine engine = new RuleEngine();

    private RuleEngineUtil() {}

    // =======================
    // 模式一：Apollo 配置调用
    // =======================
    public static RuleResult runFromApolloYaml(String namespaceContent, String ruleGroupId, Map<String, Object> input) {
        return engine.executeFromApollo(namespaceContent, FileType.YAML, ruleGroupId, input);
    }

    public static RuleResult runFromApolloYml(String namespaceContent, String ruleGroupId, Map<String, Object> input) {
        return engine.executeFromApollo(namespaceContent,FileType.YML, ruleGroupId, input);
    }

    public static RuleResult runFromApolloProperties(String namespaceContent, String ruleGroupId, Map<String, Object> input) {
        return engine.executeFromApollo(namespaceContent,FileType.PROPERTIES, ruleGroupId, input);
    }

    // =======================
    // 模式二：基于文件（yaml/json/properties）
    // =======================
    public static RuleResult runFromFile(String filePath, String ruleGroupId, Map<String, Object> input) throws IOException {
        FileType format = FileType.fromFilename(filePath);
        String fileContent = readFile(filePath);
        return engine.executeFromFile(fileContent, format, ruleGroupId, input);
    }

    // =======================
    // 模式三：用户构造 RuleGroup
    // =======================
    public static RuleResult runFromObject(RuleGroup group, Map<String, Object> input) {
        return engine.executeFromObject(group, input);
    }

    // =======================
    // 模式四：用户构造 RuleGroup（json 字符串）
    // =======================
    public static RuleResult runFromJsonStr(String jsonStr, Map<String, Object> input) {
        return engine.executeFromJsonStr(jsonStr, input);
    }

    // =======================
    // 模式四：用户构造 RuleGroup（json 为字符串数组或者头节点为“rules”）
    // =======================
    public static RuleResult runFromJsonStr(String jsonStr, String ruleGroupId, Map<String, Object> input) {
        return engine.executeFromFile(jsonStr, FileType.JSON, ruleGroupId, input);
    }



    // 读取文件内容（确保正确处理UTF-8编码）
    private static String readFile(String filePath) throws IOException {
        // 如果是properties文件，使用特殊方式读取
        if (filePath.endsWith(".properties")) {
            Properties props = new Properties();
            try (InputStreamReader reader = new InputStreamReader(
                    new FileInputStream(filePath), StandardCharsets.UTF_8)) {
                props.load(reader);
            }
            // 将Properties转换为字符串格式（保持原有逻辑）
            StringWriter writer = new StringWriter();
            props.store(writer, null);
            return writer.toString();
        } else {
            // 其他文件类型保持原有读取方式
            try (InputStream in = new FileInputStream(filePath)) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int n;
                while ((n = in.read(buffer)) != -1) {
                    out.write(buffer, 0, n);
                }
                return out.toString(StandardCharsets.UTF_8.name());
            }
        }
    }

    // 清除内部缓存
    public static void clearCache() {
        engine.clearCache();
    }

}
