package com.tommy.rulesengine.model;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.core.BasicRule;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

/**
 * 规则节点基类
 * @author zhanghao
 */
public abstract class RuleNode extends BasicRule implements Serializable {

    private static final long serialVersionUID = 2945212834894124768L;
    /**
     * 节点id
     */
    protected String id;
    /**
     * 节点类型
     */
    protected final RuleGroupType type;

    // 强制子类明确类型
    protected RuleNode(String id, RuleGroupType type,
                       String name, String description, int priority) {
        super(name, description, priority);
        this.id = Objects.requireNonNull(id);
        this.type = Objects.requireNonNull(type);
    }

    // 简化构造（自动生成默认名称等）
    protected RuleNode(String id, RuleGroupType type) {
        //// 默认名称,// 空描述
        this(id, type,
                "Rule-" + id,
                "",
                Rule.DEFAULT_PRIORITY);
    }

    public RuleGroupType getType() {
        return this.type;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    /**
     * 实现BasicRule的boolean方法（适配旧逻辑）
     * @param facts 参数
     * @return 结果
     */
    @Override
    public  boolean evaluate(Facts facts){
        return evaluateWithResult(facts).isPass();
    }

    /**
     * 强制子类实现新方法
     * @param facts 参数
     * @return 结果
     */
    public abstract RuleResult evaluateWithResult(Facts facts);


    // 提供快捷方法
    protected RuleResult createResult(boolean pass, String message) {
        return new RuleResult.Builder(id)
                .pass(pass)
                .message(message)
                .build();
    }
}
