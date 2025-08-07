package com.tommy.rule.engine;

import com.tommy.rulesengine.model.RuleDefinition;
import com.tommy.rulesengine.model.RuleGroup;
import com.tommy.rulesengine.model.RuleResult;
import com.tommy.rulesengine.util.RuleEngineUtil;
import org.junit.jupiter.api.Test;


import java.io.IOException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RuleEngineTest {

    /**
     * 手动构造规则树，测试 RuleGroup 仅含一个子节点的情况
     */
    @Test
    public void testSingleChildGroup() {
        RuleDefinition r1 = new RuleDefinition("r1", "判断是否在客群A",1, "isInCrowd(uid, '200003')");
        r1.setPriority(1);

        RuleGroup root = new RuleGroup("single-group", "单节点组合", RuleGroup.Type.AND, 0, Collections.singletonList(r1));

        Map<String, Object> context = new HashMap<>();
        context.put("uid", 2038L);
        RuleResult result = RuleEngineUtil.runFromObject(root,context);
        printResult(result);
    }

    private void printResult(RuleResult result) {
        System.out.println("======== 执行结果 ========");
        printRecursive(result, 0);
    }

    private void printRecursive(RuleResult result, int level) {
        String indent = "  ";
        System.out.println(indent + "- [" + result.getRuleId() + "] PASS: " + result.isPass() + " | " + result.getMessage());
        if (result.getChildren() != null) {
            for (RuleResult child : result.getChildren()) {
                printRecursive(child, level + 1);
            }
        }
    }

    /**
     * 手动构造规则树，测试 RuleGroup 含多个子节点的情况
     */
    @Test
    public void testMultipleChildGroup() {
        RuleDefinition r1 = new RuleDefinition("r1", "判断是否在客群A",1, "isInCrowd(uid, '200003')");
        RuleDefinition r2 = new RuleDefinition("r2", "余额大于100",2, "balance > 100");
        RuleDefinition r3 = new RuleDefinition("r3", "年龄小于30",3, "age < 30");

        RuleGroup subGroup = new RuleGroup("group-1", "子组合组", RuleGroup.Type.OR, 2);
        subGroup.addChild(r2);
        subGroup.addChild(r3);

        RuleGroup root = new RuleGroup("root-group", "根组合规则", RuleGroup.Type.AND,0);
        root.addChild(r1);
        root.addChild(subGroup);

        Map<String, Object> context = new HashMap<>();
        context.put("uid", 2038L);
        context.put("balance", 80);
        context.put("age", 25);

        RuleResult result = RuleEngineUtil.runFromObject(root, context);
        printResult(result);
    }

    /**
     * 从本地 YAML 文件加载规则
     */
    @Test
    public void testParseFromYamlFile() throws IOException {

        Map<String, Object> context = new HashMap<>();
        context.put("uid", 2038L);
        context.put("balance", 150);
        context.put("age", 28);

        RuleResult result = RuleEngineUtil.runFromFile("src/test/resources/rules.yaml", "rule-group-1", context);
        printResult(result);
    }

    @Test
    public void testParseFromPropertiesFile() throws IOException {

        Map<String, Object> context = new HashMap<>();
        context.put("uid", 2038L);
        context.put("balance", 150);
        context.put("age", 28);

        RuleResult result = RuleEngineUtil.runFromFile("src/test/resources/rules.properties", "rule-group-1", context);
        printResult(result);
    }

    @Test
    public void testParseFromJsonFile() throws IOException {

        Map<String, Object> context = new HashMap<>();
        context.put("uid", 2038L);
        context.put("balance", 150);
        context.put("age", 28);

        RuleResult result = RuleEngineUtil.runFromFile("src/test/resources/rules.json", "rule-group-1", context);
        printResult(result);
    }

    @Test
    public void testParseFromJsonStr() throws IOException {

        Map<String, Object> context = new HashMap<>();
        context.put("uid", 2038L);
        context.put("balance", 150);
        context.put("age", 28);

        RuleResult result = RuleEngineUtil.runFromJsonStr("{\"id\":\"rule-group-1\",\"name\":\"用户标签组合规则\",\"type\":\"AND\",\"priority\":1,\"children\":[{\"id\":\"r1\",\"name\":\"判断是否在客群A\",\"type\":\"SINGLE\",\"expression\":\"isInCrowd(uid, '200003')\"},{\"id\":\"group-1\",\"name\":\"子组合组\",\"type\":\"OR\",\"children\":[{\"id\":\"r2\",\"name\":\"余额大于100\",\"type\":\"SINGLE\",\"expression\":\"balance > 100\"},{\"id\":\"r3\",\"name\":\"年龄小于30\",\"type\":\"SINGLE\",\"expression\":\"age < 30\"}]}]}", context);
        printResult(result);
    }

    @Test
    public void testParseFromJsonStrWithRuleGroupId() throws IOException {

        Map<String, Object> context = new HashMap<>();
        context.put("uid", 2038L);
        context.put("balance", 150);
        context.put("age", 28);

        RuleResult result = RuleEngineUtil.runFromJsonStr("{\"id\":\"rule-group-1\",\"name\":\"用户标签组合规则\",\"type\":\"AND\",\"priority\":1,\"children\":[{\"id\":\"r1\",\"name\":\"判断是否在客群A\",\"type\":\"SINGLE\",\"expression\":\"isInCrowd(uid, '200003')\"},{\"id\":\"group-1\",\"name\":\"子组合组\",\"type\":\"OR\",\"children\":[{\"id\":\"r2\",\"name\":\"余额大于100\",\"type\":\"SINGLE\",\"expression\":\"balance > 100\"},{\"id\":\"r3\",\"name\":\"年龄小于30\",\"type\":\"SINGLE\",\"expression\":\"age < 30\"}]}]}","rule-group-1", context);
        printResult(result);
    }




}
