package com.tommy.rulesengine.model;

import com.tommy.rulesengine.actions.ActionRegistry;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;

import java.io.Serializable;
import java.util.List;
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

    /**
     * 需要执行的动作
     */
    protected List<String> actions;

    /**
     * 通用拓展参数
     */
    protected Map<String, Object> attributes;


    public RuleNode(String id, String name, int priority, boolean enabled, String description, NodeType nodeType,
                    List<String> actions, Map<String, Object> attributes) {
        this.id = id;
        this.name = name;
        this.priority = priority;
        this.enabled = enabled;
        this.nodeType = nodeType;
        this.description = description;
        this.actions = actions;
        this.attributes = attributes;
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

    public NodeType getNodeType() {
        return nodeType;
    }

    public List<String> getActions() {
        return actions;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    /**
     * 执行规则，返回执行结果
     */
    public abstract RuleResult evaluate(Facts facts);

    /**
     *  执行动作
     * @param facts 事实参数
     *
     */
    public void executeActions(Facts facts){
        if (actions != null) {
            for (String actionName : actions) {
                Rule action = ActionRegistry.getAction(actionName);
                if (action != null) {
                    try {
                        action.execute(facts);
                    } catch (Exception e) {
                        throw new RuntimeException("动作执行异常：" + e.getMessage(), e);
                    }
                } else {
                    throw new RuntimeException("动作未注册：" + actionName);
                }
            }
        }
    }
}
