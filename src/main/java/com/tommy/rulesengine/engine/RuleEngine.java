package com.tommy.rulesengine.engine;

import com.ctrip.framework.apollo.ConfigService;
import com.tommy.rulesengine.constants.FileType;
import com.tommy.rulesengine.executor.RuleExecutor;
import com.tommy.rulesengine.function.FunctionRegistrar;
import com.tommy.rulesengine.model.RuleGroup;
import com.tommy.rulesengine.model.RuleResult;
import com.tommy.rulesengine.parser.RuleParser;
import com.tommy.rulesengine.parser.RuleValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RuleEngine {

    private static final Logger log = LoggerFactory.getLogger(RuleEngine.class);


    // 缓存已解析的规则组，key = sourceType::identifier
    private final Map<String, RuleGroup> cache = new ConcurrentHashMap<>();

    // 规则解析器（支持 YAML / JSON / properties，可拓展）
    private final RuleParser parser = new RuleParser();

    // 规则执行器
    private final RuleExecutor executor = new RuleExecutor();

    // 规则校验器
    private final RuleValidator validator = new RuleValidator();

    // 注册所有自定义函数
    static {
        FunctionRegistrar.registerAll();
    }



    /**
     * 方式一：通过 Apollo 配置 namespace 内容加载
     */
    public RuleResult executeFromApollo(String namespaceContent, FileType format, String ruleGroupId, Map<String, Object> input) {

        // 1、从Apollo读取yaml配置数据
        String yamlContent = ConfigService.getConfig(namespaceContent+"."+format.getFormat())
                .getProperty("rules", "");

        String cacheKey = "apollo::" + ruleGroupId;
        RuleGroup group = cache.computeIfAbsent(cacheKey, key -> {
            List<RuleGroup> groups = parser.parse(yamlContent, format);
            return findRuleGroupById(groups, ruleGroupId);
        });

        validator.validate(group);
        return executor.execute(group, input);

    }


    /**
     * 方式二：通过文件内容（支持 yaml/json/properties）+ 规则 ID 执行
     */
    public RuleResult executeFromFile(String fileContent, FileType format, String ruleGroupId, Map<String, Object> input) {
        String cacheKey = "file::" + format + "::" + ruleGroupId;
        RuleGroup group = cache.computeIfAbsent(cacheKey, key -> {
            List<RuleGroup> groups = parser.parse(fileContent, format);
            return findRuleGroupById(groups, ruleGroupId);
        });

        validator.validate(group);
        return executor.execute(group, input);
    }

    /**
     * 方式三：接入方自定义构造 RuleGroup（不缓存）
     */
    public RuleResult executeFromObject(RuleGroup group, Map<String, Object> input) {
        log.info("executeFromObject, group: {}, input: {}", group, input);
        validator.validate(group);
        RuleResult result = executor.execute(group, input);
        log.info("executeFromObject, result: {}", result);
        return result;
    }

    /**
     * 方式三：接入方自定义构造 RuleGroup（不缓存）
     */
    public RuleResult executeFromJsonStr(String jsonStr, Map<String, Object> input) {
        log.info("executeFromJsonStr, group: {}, input: {}", jsonStr, input);
        List<RuleGroup> groups = parser.parse(jsonStr, FileType.JSON);
        RuleGroup group = groups.get(0);
        validator.validate(group);
        RuleResult result = executor.execute(group, input);
        log.info("executeFromJsonStr, result: {}", result);
        return result;
    }


    /**
     * 根据 ruleGroupId 在列表中查找
     */
    private RuleGroup findRuleGroupById(List<RuleGroup> groups, String id) {
        for (RuleGroup group : groups) {
            if (group.getId().equals(id)) {
                return group;
            }
        }
        throw new IllegalArgumentException("未找到规则组 ID: " + id);
    }

    /**
     * 可清空缓存（如接收到 Apollo 配置更新通知）
     */
    public void clearCache() {
        cache.clear();
    }
}
