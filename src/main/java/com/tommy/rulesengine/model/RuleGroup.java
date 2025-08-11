package com.tommy.rulesengine.model;

import org.jeasy.rules.api.Facts;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 规则组实体
 * @author zhanghao
 */
public class RuleGroup extends RuleNode {

    private static final long serialVersionUID = -4303779533283614895L;
    /**
     * 逻辑类型
     */
    private LogicType logic;

    /**
     * 子节点
     */
    private List<RuleNode> children = new ArrayList<>();



    public RuleGroup(String id, String name, int priority, boolean enabled, String description,
                     LogicType logic, List<RuleNode> children, List<String> actions, Map<String, Object> attributes) {
        super(id, name, priority, enabled, description, NodeType.COMPOSITE, actions, attributes);
        this.logic = logic;
        this.children = children != null ? children : new ArrayList<>();
        this.actions = actions != null ? actions : new ArrayList<>();
    }

    public LogicType getLogic() { return logic; }
    public void setLogic(LogicType logic) { this.logic = logic; }

    public List<RuleNode> getChildren() { return children; }
    public void setChildren(List<RuleNode> children) { this.children = children; }
    public void addChild(RuleNode child) { this.children.add(child); }


    public void setActions(List<String> actions) {
        this.actions = actions;
    }


    @Override
    public RuleResult evaluate(Facts facts) {
        List<RuleNode> activeChildren = children.stream()
                .filter(RuleNode::isEnabled)
                .sorted(Comparator.comparingInt(RuleNode::getPriority))
                .collect(Collectors.toList());

        List<RuleResult> childResults = new ArrayList<>();
        boolean passed = false;

        switch (logic) {
            case AND:
                passed = true;
                for (RuleNode child : activeChildren) {
                    RuleResult res = child.evaluate(facts);
                    childResults.add(res);
                    // and 操作
                    if (!res.isPass()) {
                        passed = false;
                        break;
                    }
                }
                break;
            case OR:
                for (RuleNode child : activeChildren) {
                    RuleResult res = child.evaluate(facts);
                    childResults.add(res);
                    // or 短路操作
                    if (res.isPass()) {
                        passed = true;
                        break;
                    }
                }
                break;
            case PRIORITY:
                for (RuleNode child : activeChildren) {
                    RuleResult res = child.evaluate(facts);
                    childResults.add(res);
                    if (res.isPass()) {
                        passed = true;
                        // 命中第一个符合条件的，直接执行动作
                        child.executeActions(facts);
                        // 取子节点的attributes里的paymentMethod
                        Map<String, Object> attrs = this.attributes != null ? attributes : new HashMap<>();
                        if (child.getAttributes() != null) {
                            attrs.putAll(child.getAttributes());
                        }
                        return new RuleResult(id, true,
                                "优先路由命中：" + child.getId(),
                                childResults,
                                attrs);
                    }
                }
                break;

        }

        // ✅ 如果整体通过，统一执行当前节点及所有子节点的动作
        if (passed && logic != LogicType.PRIORITY) {
            // 当前组的动作
            executeActions(facts);
            // 子节点动作
            for (RuleNode child : activeChildren) {
                child.executeActions(facts);
            }
        }
        return new RuleResult(id, passed,
                passed ? "组合规则通过" : "组合规则未通过",
                childResults,
                this.attributes);
    }
}




