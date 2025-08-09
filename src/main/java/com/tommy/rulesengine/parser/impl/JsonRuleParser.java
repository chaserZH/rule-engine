package com.tommy.rulesengine.parser.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tommy.rulesengine.model.RuleNode;
import com.tommy.rulesengine.parser.AbstractRuleParser;
import com.tommy.rulesengine.parser.ParserType;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class JsonRuleParser extends AbstractRuleParser {

    private final ObjectMapper jsonMapper = new ObjectMapper();

    @Override
    public String getFormate() {
        return ParserType.JSON.getParserType();
    }

    @Override
    protected List<RuleNode> doParse(String content) {
        try {
            JsonNode rootNode = jsonMapper.readTree(content);
            // 处理单对象和数组两种情况
            if (rootNode.isArray()) {
                return parseRuleList(jsonMapper.convertValue(rootNode, new TypeReference<List<Map<String, Object>>>() {}));
            } else if (rootNode.has("rules")) {
                return parseRuleList(jsonMapper.convertValue(rootNode.get("rules"), new TypeReference<List<Map<String, Object>>>() {}));
            } else {
                // 当作单个RuleGroup处理
                Map<String, Object> ruleMap = jsonMapper.convertValue(rootNode, new TypeReference<Map<String, Object>>() {});
                return parseRuleList(Collections.singletonList(ruleMap));
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JSON format", e);
        }
    }


}
