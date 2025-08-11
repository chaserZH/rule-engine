package com.tommy.rulesengine.parser.impl;

import com.tommy.rulesengine.model.*;
import com.tommy.rulesengine.parser.AbstractRuleParser;
import com.tommy.rulesengine.parser.ParserType;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PropertiesRuleParser extends AbstractRuleParser {

    private static final Pattern INDEX_PATTERN = Pattern.compile("\\[(\\d+)]");

    @Override
    public String getFormate() {
        return ParserType.PROPERTIES.getParserType();
    }

    @Override
    public List<RuleNode> doParse(String content) {
        Properties props = new Properties();
        try {
            props.load(new StringReader(content));
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid properties format", e);
        }
        return parseRules(props);
    }

    public static List<RuleNode> parseRules(Properties props) {
        List<Integer> rootIndexes = findIndexes(props, "rules");
        List<RuleNode> rootNodes = new ArrayList<>();
        for (Integer idx : rootIndexes) {
            String baseKey = "rules[" + idx + "]";
            RuleNode node = buildRuleNode(baseKey, props);
            if (node != null) {
                rootNodes.add(node);
            }
        }
        return rootNodes;
    }

    private static RuleNode buildRuleNode(String baseKey, Properties props) {
        String id = props.getProperty(baseKey + ".id");
        if (id == null) {
            return null;
        }

        String name = props.getProperty(baseKey + ".name", "");
        int priority = Integer.parseInt(props.getProperty(baseKey + ".priority", "0"));
        boolean enabled = Boolean.parseBoolean(props.getProperty(baseKey + ".enabled", "true"));
        String description = props.getProperty(baseKey + ".description", "");
        String typeStr = props.getProperty(baseKey + ".type");
        if (typeStr == null) {
            throw new IllegalArgumentException("Node type missing: " + baseKey);
        }

        NodeType nodeType = NodeType.valueOf(typeStr.trim().toUpperCase());

        if (nodeType == NodeType.COMPOSITE) {
            String logicStr = props.getProperty(baseKey + ".logic", "AND");
            LogicType logic = LogicType.valueOf(logicStr.trim().toUpperCase());

            List<Integer> childIndexes = findIndexes(props, baseKey + ".children");
            List<RuleNode> children = new ArrayList<>();
            for (Integer cidx : childIndexes) {
                String childKey = baseKey + ".children[" + cidx + "]";
                RuleNode childNode = buildRuleNode(childKey, props);
                if (childNode != null) {
                    children.add(childNode);
                }
            }

            List<String> actions = readActions(props, baseKey);

            return new RuleGroup(id, name, priority, enabled, description, logic, children, actions);
        } else {
            // LEAF 节点
            String expression = props.getProperty(baseKey + ".expression", "");
            List<String> actions = readActions(props, baseKey);
            return new RuleDefinition(id, name, priority, enabled, description, expression, actions);
        }
    }

    private static List<String> readActions(Properties props, String baseKey) {
        List<Integer> actionIndexes = findIndexes(props, baseKey + ".actions");
        List<String> actions = new ArrayList<>();
        for (Integer aidx : actionIndexes) {
            String actionKey = baseKey + ".actions[" + aidx + "]";
            String action = props.getProperty(actionKey);
            if (action != null && !action.isEmpty()) {
                actions.add(action);
            }
        }
        return actions;
    }

    private static List<Integer> findIndexes(Properties props, String prefix) {
        Set<Integer> indexes = new HashSet<>();
        String prefixWithBracket = prefix + "[";
        for (String key : props.stringPropertyNames()) {
            if (key.startsWith(prefixWithBracket)) {
                String remain = key.substring(prefix.length());
                Matcher m = INDEX_PATTERN.matcher(remain);
                if (m.find()) {
                    indexes.add(Integer.parseInt(m.group(1)));
                }
            }
        }
        List<Integer> list = new ArrayList<>(indexes);
        Collections.sort(list);
        return list;
    }

}
