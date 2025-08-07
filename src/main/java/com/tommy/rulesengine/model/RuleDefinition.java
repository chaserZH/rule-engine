package com.tommy.rulesengine.model;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;

import java.util.Map;

/**
 * 规则模型
 * @author zhanghao
 * @since 2025/08/05 14:20
 */
public class RuleDefinition extends RuleNode {

    /**
     * 表达式
     * // Aviator表达式
     */
    private String expression;

    public RuleDefinition() {
    }

    public RuleDefinition(String id,String name,int priority,String expression) {
        this.id = id;
        this.name = name;
        this.priority = priority;
        this.expression = expression;
    }

    @Override
    public RuleResult evaluate(Map<String, Object> context) {
        try {
            Expression compiled = AviatorEvaluator.compile(expression, true);
            Object result = compiled.execute(context);
            boolean pass = result instanceof Boolean && (Boolean) result;
            return new RuleResult(id, pass, "Expression: " + expression);
        } catch (Exception e) {
            return new RuleResult(id, false, "Error: " + e.getMessage());
        }
    }

    public String getExpression() {
        return expression;
    }


    public void setExpression(String expression) {
        this.expression = expression;
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
