package com.tommy.rulesengine.model;

import com.googlecode.aviator.AviatorEvaluator;
import org.jeasy.rules.api.Facts;

import java.util.List;
import java.util.Map;

/**
 * 规则模型
 * @author zhanghao
 * @since 2025/08/05 14:20
 */
public class RuleDefinition extends RuleNode {

    private static final long serialVersionUID = -6275744375419577254L;

    /**
     * 表达式
     */
    private String expression;



    public RuleDefinition(String id, String name, int priority, boolean enabled, String description,
                          String expression, List<String> actions,Map<String,Object> attributes) {
        super(id, name, priority, enabled, description, NodeType.LEAF,actions,attributes);
        this.expression = expression;
        this.actions = actions;
        this.attributes = attributes;
    }


    /**
     * 表达式
     */
    public String getExpression() {
        return expression;
    }



    @Override
    public RuleResult evaluate(Facts facts) {
        if (!enabled) {
            return new RuleResult(id, true, "enabled is false",attributes);
        }
        boolean pass;
        try {
            pass = Boolean.TRUE.equals(AviatorEvaluator.execute(expression, facts.asMap(), true));
        } catch (Exception e) {
            return new RuleResult(id, false, "表达式执行异常：" + e.getMessage(),attributes);
        }
        return new RuleResult(id, pass, pass ? "规则通过" : "规则未通过",attributes);
    }

}
