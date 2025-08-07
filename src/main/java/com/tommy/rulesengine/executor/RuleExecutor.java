package com.tommy.rulesengine.executor;

import com.tommy.rulesengine.model.RuleGroup;
import com.tommy.rulesengine.model.RuleResult;

import java.util.*;

public class RuleExecutor {

    public RuleExecutor() {
        // 可选配置，不设置也行，去掉无效枚举
    }

    public RuleResult execute(RuleGroup group, Map<String, Object> input) {
        return group.evaluate(input);
    }


}
