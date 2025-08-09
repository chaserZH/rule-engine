package com.tommy.rulesengine.model;

import java.io.Serializable;
import java.util.List;

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
     * 子结果
     */
    private List<RuleResult> children;

    public RuleResult(String ruleId, boolean pass, String message) {
        this.ruleId = ruleId;
        this.pass = pass;
        this.message = message;
    }

    public RuleResult(String ruleId, boolean pass, String message, List<RuleResult> children) {
        this(ruleId, pass, message);
        this.children = children;
    }

    public String getRuleId() { return ruleId; }
    public boolean isPass() { return pass; }
    public String getMessage() { return message; }
    public List<RuleResult> getChildren() { return children; }
}