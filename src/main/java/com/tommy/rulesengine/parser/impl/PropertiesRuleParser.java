package com.tommy.rulesengine.parser.impl;

import com.tommy.rulesengine.model.RuleNode;
import com.tommy.rulesengine.parser.AbstractRuleParser;
import com.tommy.rulesengine.parser.ParserType;
import com.tommy.rulesengine.util.PropertiesUtil;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;

public class PropertiesRuleParser extends AbstractRuleParser {

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
        // 转换为与YAML/JSON解析兼容的结构
        List<Map<String, Object>> rulesList = PropertiesUtil.parse(props);
        return parseRuleList(rulesList);
    }
}
