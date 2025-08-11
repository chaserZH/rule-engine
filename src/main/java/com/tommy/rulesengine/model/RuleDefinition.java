package com.tommy.rulesengine.model;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import com.tommy.rulesengine.actions.ActionRegistry;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;

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

    /**
     * 动作
     */
    private List<String> actions;



    public RuleDefinition(String id, String name, int priority, boolean enabled, String description,
                          String expression, List<String> actions) {
        super(id, name, priority, enabled, description, NodeType.LEAF);
        this.expression = expression;
        this.actions = actions;
    }



    /**
     * 表达式
     */
    public String getExpression() {
        return expression;
    }

    /**
     * 动作
     */
    public List<String> getActions() {
        return actions;
    }

    @Override
    public RuleResult evaluateWithActions(Facts facts) {
        if (!enabled) {
            return new RuleResult(id, true, "enabled is false");
        }
        boolean pass;
        try {
            pass = Boolean.TRUE.equals(AviatorEvaluator.execute(expression, facts.asMap(), true));
        } catch (Exception e) {
            return new RuleResult(id, false, "表达式执行异常：" + e.getMessage());
        }
        if (pass && actions != null) {
            for (String actionName : actions) {
                Rule action = ActionRegistry.getAction(actionName);
                if (action == null) {
                    return new RuleResult(id, false, "动作未注册：" + actionName);
                }
                try {
                    action.execute(facts);
                } catch (Exception e) {
                    return new RuleResult(id, false, "动作执行异常：" + e.getMessage());
                }
            }
        }
        return new RuleResult(id, pass, pass ? "规则通过，动作执行成功" : "规则未通过");
    }
}
