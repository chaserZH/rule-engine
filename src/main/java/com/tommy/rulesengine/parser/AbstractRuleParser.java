package com.tommy.rulesengine.parser;

import com.ctrip.framework.apollo.core.utils.StringUtils;
import com.tommy.rulesengine.model.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhanghao
 */
public abstract class AbstractRuleParser implements RuleParser {



    @Override
    public List<RuleNode> parse(String content) {
        return doParse(content);
    }

    /**
     * 解析规则
     * @param content 规则内容
     * @return 规则节点列表
     */
    protected abstract List<RuleNode> doParse(String content);


    /**
     * 构造规则组之前进行检查
     * @param nodeMap 节点信息
     * @return 是否合法
     */
    private boolean prePredicateNode(Map<String, Object> nodeMap) {
        boolean isGroup = nodeMap.containsKey("children");
        String type = (String) nodeMap.get("type");
        String id = (String) nodeMap.get("id");
        String name = (String) nodeMap.get("name");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> children = (List<Map<String, Object>>) nodeMap.get("children");
        // 必填字段校验
        if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("Rule id is required");
        }
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Rule name is required");
        }
        if (StringUtils.isBlank(type)) {
            throw new IllegalArgumentException("Rule type is required");
        }

        // 类型校验
        boolean supported = NodeType.isSupported(type);
        if (!supported) {
            throw new IllegalArgumentException("Rule type is not supported");
        }

        // 叶子节点
        if (!isGroup) {
            NodeType nodeType = NodeType.of(type);
            if (NodeType.LEAF != nodeType) {
                throw new IllegalArgumentException("Rule type is not leaf");
            }
            String expression = (String) nodeMap.get("expression");
            if (StringUtils.isEmpty(expression)) {
                throw new IllegalArgumentException("Rule expression is required,id:" + id);
            }

        }
        //是组合节点，name必须存在
        if (children == null || children.isEmpty()) {
            throw new IllegalArgumentException("Rule children is required");
        }
        if (children.size() > 2) {
            throw new IllegalArgumentException("Rule children contains more than 2 elements,id:" + id);
        }

        String logic = (String) nodeMap.get("logic");
        if (StringUtils.isEmpty(logic)) {
            throw new IllegalArgumentException("Composite rule logic is required"+id);
        }
        return true;
    }

    private RuleNode buildNode(Map<String, Object> nodeMap) {

        boolean isGroup = nodeMap.containsKey("children");
        String id = (String) nodeMap.get("id");
        String name = (String) nodeMap.get("name");
        int priority = (int) nodeMap.getOrDefault("priority",0);
        boolean enabled = (boolean) nodeMap.getOrDefault("enabled", true);
        String description = (String) nodeMap.get("description");

        if (isGroup) {
            @SuppressWarnings("unchecked")
            List<String> actions = (List<String>) nodeMap.getOrDefault("actions", Collections.emptyList());
            LogicType logic = LogicType.valueOf(((String) nodeMap.get("logic")).toUpperCase());
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> childrenRaw = (List<Map<String, Object>>) nodeMap.get("children");
            // 统一解析 attributes（可选的业务扩展属性）
            @SuppressWarnings("unchecked")
            Map<String, Object> attributes = (Map<String, Object>) nodeMap.getOrDefault("attributes", Collections.emptyMap());

            List<RuleNode> children = childrenRaw.stream()
                    .map(this::buildNode)
                    .collect(Collectors.toList());
            return new RuleGroup(id, name, priority, enabled, description, logic, children,actions,attributes);
        } else {
            String expression = (String) nodeMap.get("expression");
            @SuppressWarnings("unchecked")
            List<String> actions = (List<String>) nodeMap.getOrDefault("actions", Collections.emptyList());
            // 统一解析 attributes（可选的业务扩展属性）
            @SuppressWarnings("unchecked")
            Map<String, Object> attributes = (Map<String, Object>) nodeMap.getOrDefault("attributes", Collections.emptyMap());

            return new RuleDefinition(id, name, priority, enabled, description, expression, actions,attributes);
        }
    }

    protected List<RuleNode> parseRuleList(List<Map<String, Object>> rulesList) {

        return rulesList.stream()
                .filter(this::prePredicateNode)
                .map(this::buildNode)
                .collect(Collectors.toList());
    }
}
