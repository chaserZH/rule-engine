package com.tommy.rulesengine.parser.impl;

import com.tommy.rulesengine.model.RuleNode;
import com.tommy.rulesengine.parser.AbstractRuleParser;
import com.tommy.rulesengine.parser.ParserType;
import org.yaml.snakeyaml.Yaml;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author zhanghao
 */
public class YamlRuleParser extends AbstractRuleParser {

    private final Yaml yaml = new Yaml();
    @Override
    protected List<RuleNode> doParse(String content) {

        Object raw = yaml.load(content);
        if (!(raw instanceof Map)) {
            throw new IllegalArgumentException("YAML content must start with a map (e.g. {rules: [...]})");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) raw;
        // 3. 获取 rules 部分
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rulesList = (List<Map<String, Object>>) map.get("rules");

        if (rulesList == null || rulesList.isEmpty()) {
            throw new IllegalArgumentException("No rules found in the configuration");
        }
        return parseRuleList(rulesList);
    }

    @Override
    public String getFormate() {
        return ParserType.YAML.name();
    }
}
