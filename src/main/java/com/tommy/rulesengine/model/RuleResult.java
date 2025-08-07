package com.tommy.rulesengine.model;

import java.util.List;

public class RuleResult {

    private String ruleId;
    private boolean pass;
    private String message;
    private List<RuleResult> children;


    public RuleResult(String ruleId, boolean pass, String message) {
        this.ruleId = ruleId;
        this.pass = pass;
        this.message = message;
    }

    public RuleResult(String ruleId, boolean pass, String message, List<RuleResult> children) {
        this.ruleId = ruleId;
        this.pass = pass;
        this.message = message;
        this.children = children;
    }

    public String getRuleId() { return ruleId; }
    public boolean isPass() { return pass; }
    public String getMessage() { return message; }
    public List<RuleResult> getChildren() { return children; }
}
