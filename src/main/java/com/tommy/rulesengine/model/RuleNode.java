package com.tommy.rulesengine.model;

import org.jeasy.rules.api.Facts;

import java.io.Serializable;

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
     * 节点名称
     */
    protected String name;

    /**
     * 优先级
     */
    protected int priority;

    /**
     * 是否启用
     */
    protected boolean enabled = true;


    /**
     * 节点描述
     */
    protected  String description;

    /**
     * 节点类型LEAF,COMPOSITE
     */
    protected NodeType nodeType;


    public RuleNode(String id, String name, int priority, boolean enabled, String description, NodeType nodeType) {
        this.id = id;
        this.name = name;
        this.priority = priority;
        this.enabled = enabled;
        this.nodeType = nodeType;
        this.description = description;
    }



    public NodeType getType() {
        return this.nodeType;
    }

    //Getter


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPriority() {
        return priority;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getDescription() {
        return description;
    }


    /**
     * 执行规则，返回执行结果
     */
    public abstract RuleResult evaluateWithActions(Facts facts);
}
