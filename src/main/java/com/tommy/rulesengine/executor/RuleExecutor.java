package com.tommy.rulesengine.executor;

import com.tommy.rulesengine.model.RuleGroup;
import com.tommy.rulesengine.model.RuleNode;
import com.tommy.rulesengine.model.RuleResult;
import com.tommy.rulesengine.util.FactsUtils;
import org.jeasy.rules.api.Facts;

import java.util.Map;


public class RuleExecutor {



    public RuleResult execute(RuleNode ruleGroup,Map<String, Object> map) {
        Facts facts = FactsUtils.fromMap(map, false);
        return ruleGroup.evaluateWithResult(facts);
    }


}
