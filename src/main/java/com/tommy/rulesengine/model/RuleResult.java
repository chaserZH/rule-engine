package com.tommy.rulesengine.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 规则结果
 * @author zhanghao
 */
public class RuleResult implements Serializable {
    private static final long serialVersionUID = 5915372283700694532L;
    private final String ruleId;
    private final boolean pass;
    private final String message;
    private final List<RuleResult> children;

    // 构建器模式
    public static class Builder {
        private String ruleId;
        private boolean pass;
        private String message = "";
        private List<RuleResult> children = new ArrayList<>();

        public Builder(String ruleId) {
            this.ruleId = ruleId;
        }

        public Builder pass(boolean pass) {
            this.pass = pass;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder addChild(RuleResult child) {
            this.children.add(child);
            return this;
        }
        public Builder children(List<RuleResult> children) {
            this.children = children;
            return this;
        }

        public RuleResult build() {
            return new RuleResult(ruleId, pass, message,
                    Collections.unmodifiableList(children));
        }
    }

    // 私有构造
    private RuleResult(String ruleId, boolean pass,
                       String message, List<RuleResult> children) {
        this.ruleId = ruleId;
        this.pass = pass;
        this.message = message;
        this.children = children;
    }

    // getters...

    public String getRuleId() {
        return ruleId;
    }

    public boolean isPass() {
        return pass;
    }

    public String getMessage() {
        return message;
    }

    public List<RuleResult> getChildren() {
        return children;
    }
}