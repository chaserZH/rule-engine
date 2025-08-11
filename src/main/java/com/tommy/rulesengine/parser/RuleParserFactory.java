package com.tommy.rulesengine.parser;

import com.tommy.rulesengine.parser.impl.JsonRuleParser;
import com.tommy.rulesengine.parser.impl.PropertiesRuleParser;
import com.tommy.rulesengine.parser.impl.YamlRuleParser;

import java.util.Objects;

/**
 * @author zhanghao
 */
public class RuleParserFactory {
    /**
     * 获取规则解析器
     *
     * @param format 规则文件格式
     * @return 规则解析器
     */
    public static RuleParser getParser(String format) {
        ParserType parserType = ParserType.getByParserType(format);

        switch (Objects.requireNonNull(parserType)) {
            case JSON:
                return new JsonRuleParser();
            case PROPERTIES:
                return new PropertiesRuleParser();
            case YAML:
                return new YamlRuleParser();
            default:
                throw new IllegalArgumentException("Unsupported format: " + format);
        }
    }
}