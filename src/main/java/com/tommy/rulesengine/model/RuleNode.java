package com.tommy.rulesengine.model;

import java.io.Serializable;
import java.util.Map;

/**
 * 规则节点基类
 * @author zhanghao
 */
public abstract class RuleNode implements Serializable {

    private static final long serialVersionUID = 2945212834894124768L;
    /**
     * 节点id
     */
    protected String id;

    /**
     * 名称
     */
    protected String name;

    /**
     * 优先级
     */
    protected int priority;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }

    @Override
    public String toString() {
        return "RuleNode{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", priority=" + priority +
                '}';
    }

    /**
     * 规则节点执行方法
     * @param context 上下文
     * @return 规则是否通过
     */
    public abstract RuleResult evaluate(Map<String, Object> context);
}
