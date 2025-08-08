package com.tommy.rulesengine.model;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import org.jeasy.rules.api.Facts;

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
     * // Aviator表达式
     */
    private String expression;

    public RuleDefinition(String id, String expression) {
        super(id, RuleGroupType.LEAF);
        this.expression = validateExpression(expression);
    }

    public RuleDefinition(String id, String name, String description, int priority, String expression) {
        super(id, RuleGroupType.LEAF, name, description, priority);
        this.expression = expression;
    }

    private static String validateExpression(String expr) {
        // 预编译验证表达式
        AviatorEvaluator.compile(expr);
        return expr;
    }

    public String getExpression() {
        return expression;
    }


    public void setExpression(String expression) {
        this.expression = expression;
    }


    @Override
    public RuleResult evaluateWithResult(Facts facts) {
        try {
            Object result = AviatorEvaluator.execute(expression, facts.asMap());
            boolean pass = Boolean.TRUE.equals(result);

            return createResult(pass,
                    pass ? "表达式验证通过" : "表达式验证失败");
        } catch (Exception e) {
            return createResult(false,
                    "表达式执行错误: " + e.getMessage());
        }
    }


    @Override
    public String toString() {
        return "RuleDefinition{" +
                "expression='" + expression + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", priority=" + priority +
                '}';
    }
}
