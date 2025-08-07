package com.tommy.rulesengine.parser;





import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tommy.rulesengine.constants.FileType;
import com.tommy.rulesengine.model.RuleDefinition;
import com.tommy.rulesengine.model.RuleGroup;
import com.tommy.rulesengine.model.RuleNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 规则解析器
 */
public class RuleParser {

    private static final Logger log = LoggerFactory.getLogger(RuleParser.class);


    private final Yaml yaml = new Yaml();
    private final ObjectMapper jsonMapper = new ObjectMapper();

    /**
     * 通用解析入口
     */
    public List<RuleGroup> parse(String content, FileType format) {
        log.info("Parsing rule definitions from: {} format:{}",content, format);
        List<RuleGroup> ruleGroups;
        switch (format) {
            case YAML:
            case YML:
                ruleGroups = parseYaml(content);
                break;
            case JSON:
                ruleGroups = parseJson(content);
                break;
            case PROPERTIES:
                ruleGroups = parseProperties(content);
                break;
            default:
                throw new IllegalArgumentException("Unsupported format: " + format);
        }
        log.info("Rule definitions parsed successfully, rule groups: {}", ruleGroups);
        return ruleGroups;
    }

    /**
     * YAML 格式解析
     */
    public List<RuleGroup> parseYaml(String yamlContent) {
        Object raw = yaml.load(yamlContent);
        if (!(raw instanceof Map)) {
            throw new IllegalArgumentException("YAML content must start with a map (e.g. {rules: [...]})");
        }

        Map<String, Object> map = (Map<String, Object>) raw;
        // 3. 获取 rules 部分
        List<Map<String, Object>> rulesList = (List<Map<String, Object>>) map.get("rules");

        if (rulesList == null || rulesList.isEmpty()) {
            throw new IllegalArgumentException("No rules found in the configuration");
        }
        return parseRuleList(rulesList);
    }


    /**
     * 解析rules
     * @param rulesList
     * @return
     */
    private List<RuleGroup> parseRuleList(List<Map<String, Object>> rulesList) {
        return rulesList.stream()
                .map(this::buildNode)
                .map(this::wrapAsGroupIfNeeded)
                .collect(Collectors.toList());
    }


    /**
     * JSON 格式解析
     */
    public List<RuleGroup> parseJson(String jsonContent) {
        try {
            JsonNode rootNode = jsonMapper.readTree(jsonContent);
            // 处理单对象和数组两种情况
            if (rootNode.isArray()) {
                return parseRuleList(jsonMapper.convertValue(rootNode, new TypeReference<List<Map<String, Object>>>() {}));
            } else if (rootNode.has("rules")) {
                return parseRuleList(jsonMapper.convertValue(rootNode.get("rules"), new TypeReference<List<Map<String, Object>>>() {}));
            } else {
                // 当作单个RuleGroup处理
                Map<String, Object> ruleMap = jsonMapper.convertValue(rootNode, new TypeReference<Map<String, Object>>() {});
                return Collections.singletonList(wrapAsGroupIfNeeded(buildNode(ruleMap)));
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JSON format", e);
        }
    }

    /**
     * Properties 格式解析
     */
    public List<RuleGroup> parseProperties(String propertiesContent) {
        Properties props = new Properties();
        try {
            props.load(new StringReader(propertiesContent));
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid properties format", e);
        }

        // 转换为与YAML/JSON解析兼容的结构
        List<Map<String, Object>> rulesList = convertPropertiesToRuleList(props);
        return parseRuleList(rulesList);
    }

    /**
     * 将Properties转换为与YAML/JSON兼容的结构
     */
    private List<Map<String, Object>> convertPropertiesToRuleList(Properties props) {
        // 获取所有规则组的索引
        Set<Integer> groupIndices = props.stringPropertyNames().stream()
                .filter(key -> key.matches("rules\\[\\d+\\]\\.id"))
                .map(key -> Integer.parseInt(key.replaceAll("rules\\[(\\d+)\\].*", "$1")))
                .collect(Collectors.toSet());

        List<Map<String, Object>> rulesList = new ArrayList<>();
        for (int groupIndex : groupIndices) {
            rulesList.add(parsePropertiesGroup(props, groupIndex));
        }
        return rulesList;
    }

    /**
     * 解析单个规则组
     */
    private Map<String, Object> parsePropertiesGroup(Properties props, int groupIndex) {
        String prefix = "rules[" + groupIndex + "]";
        Map<String, Object> groupMap = new HashMap<>();
        groupMap.put("id", props.getProperty(prefix + ".id"));
        groupMap.put("name", props.getProperty(prefix + ".name"));
        groupMap.put("type", props.getProperty(prefix + ".type"));
        groupMap.put("priority", Integer.parseInt(props.getProperty(prefix + ".priority")));

        // 解析子节点
        Set<Integer> childIndices = props.stringPropertyNames().stream()
                .filter(key -> key.startsWith(prefix + ".children[") && key.contains("].id"))
                .map(key -> Integer.parseInt(key.replaceAll(".*children\\[(\\d+)\\].*", "$1")))
                .collect(Collectors.toSet());

        List<Map<String, Object>> children = new ArrayList<>();
        for (int childIndex : childIndices) {
            String childPrefix = prefix + ".children[" + childIndex + "]";
            children.add(parsePropertiesNode(props, childPrefix));
        }

        if (!children.isEmpty()) {
            groupMap.put("children", children);
        }

        return groupMap;
    }

    /**
     * 解析单个节点（可能是Rule或Group）
     */
    private Map<String, Object> parsePropertiesNode(Properties props, String prefix) {
        Map<String, Object> nodeMap = new HashMap<>();
        nodeMap.put("id", props.getProperty(prefix + ".id"));
        nodeMap.put("name", props.getProperty(prefix + ".name"));

        String type = props.getProperty(prefix + ".type");
        nodeMap.put("type", type);

        if (props.containsKey(prefix + ".priority")) {
            nodeMap.put("priority", Integer.parseInt(props.getProperty(prefix + ".priority")));
        }

        if ("SINGLE".equals(type)) {
            nodeMap.put("expression", props.getProperty(prefix + ".expression"));
        } else {
            // 处理嵌套的子节点
            Set<Integer> grandChildIndices = props.stringPropertyNames().stream()
                    .filter(key -> key.startsWith(prefix + ".children[") && key.contains("].id"))
                    .map(key -> Integer.parseInt(key.replaceAll(".*children\\[(\\d+)\\].*", "$1")))
                    .collect(Collectors.toSet());

            List<Map<String, Object>> grandChildren = new ArrayList<>();
            for (int grandChildIndex : grandChildIndices) {
                String grandChildPrefix = prefix + ".children[" + grandChildIndex + "]";
                grandChildren.add(parsePropertiesNode(props, grandChildPrefix));
            }

            if (!grandChildren.isEmpty()) {
                nodeMap.put("children", grandChildren);
            }
        }

        return nodeMap;
    }

    /**
     * 构建 RuleNode（递归）
     */
    private RuleNode buildNode(Map<String, Object> nodeMap) {
        String type = (String) nodeMap.get("type");
        String id = (String) nodeMap.get("id");
        String name = (String) nodeMap.get("name");
        int priority = nodeMap.get("priority") != null ? (int) nodeMap.get("priority") : 0;
        List<Map<String, Object>> children = (List<Map<String, Object>>) nodeMap.get("children");
        if ("SINGLE".equalsIgnoreCase(type) || children == null || children.isEmpty()){
            String expression = (String) nodeMap.get("expression");
            return new RuleDefinition(id, name, priority, expression);
        }

        // 子节点存在，递归处理
        List<RuleNode> childNodes = children.stream()
                .map(this::buildNode)
                .collect(Collectors.toList());
        RuleGroup.Type ruleType ;
        if (children.size() == 1) {
            ruleType = RuleGroup.Type.AND;
        } else {
            if (type == null) {
                throw new IllegalArgumentException("Missing group type (AND/OR)");
            }
            ruleType = RuleGroup.Type.valueOf(type);
        }

        return new RuleGroup(id, name, ruleType, priority, childNodes);
    }

    /**
     * 如果顶层是 RuleDefinition，自动包装为 AND Group
     */
    private RuleGroup wrapAsGroupIfNeeded(RuleNode node) {
        if (node instanceof RuleGroup) {
            return (RuleGroup) node;
        }

        // 如果为RuleDefinition，包装为AND Group
        RuleGroup wrapper = new RuleGroup();
        wrapper.setId("auto_group_" + node.getId());
        wrapper.setName("AutoWrappedGroup");
        wrapper.setType("AND");
        wrapper.setPriority(0);
        wrapper.addChild(node);
        return wrapper;
    }
}
