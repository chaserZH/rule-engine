package com.tommy.rulesengine.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 规则结果
 * @author zhanghao
 */
public class RuleResult implements Serializable{
    private static final long serialVersionUID = -7829454581871841410L;
    /**
     * 规则id
     */
    private String ruleId;
    /**
     * 是否通过
     */
    private boolean pass;
    /**
     * 结果信息
     */
    private String message;

    /**
     * 业务扩展属性
     */
    private Map<String, Object> attributes;

    /**
     * 子结果
     */
    private List<RuleResult> children;

    public RuleResult(String ruleId, boolean pass, String message, Map<String, Object> attributes) {
        this.ruleId = ruleId;
        this.pass = pass;
        this.message = message;
        this.attributes = attributes;
    }

    public RuleResult(String ruleId, boolean pass, String message, List<RuleResult> children, Map<String, Object> attributes) {
        this(ruleId, pass, message, attributes);
        this.children = children;
    }

    public String getRuleId() { return ruleId; }
    public boolean isPass() { return pass; }
    public String getMessage() { return message; }
    public List<RuleResult> getChildren() { return children; }
}