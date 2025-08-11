package com.tommy.rulesengine.executor;


import com.tommy.rulesengine.model.RuleNode;
import com.tommy.rulesengine.model.RuleResult;
import org.jeasy.rules.api.Facts;

public class RuleExecutor {


    public RuleResult executeRuleNode(RuleNode ruleNode, Facts facts) {

        return ruleNode.evaluate(facts);
    }




}
