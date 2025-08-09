package com.tommy.rulesengine.model;

import com.tommy.rulesengine.actions.ActionRegistry;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;

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

    private List<String> actions = new ArrayList<>();

    public RuleGroup(String id, String name, int priority, boolean enabled, String description,
                     LogicType logic, List<RuleNode> children, List<String> actions) {
        super(id, name, priority, enabled, description, NodeType.COMPOSITE);
        this.logic = logic;
        this.children = children != null ? children : new ArrayList<>();
        this.actions = actions != null ? actions : new ArrayList<>();
    }



    public LogicType getLogic() { return logic; }
    public void setLogic(LogicType logic) { this.logic = logic; }

    public List<RuleNode> getChildren() { return children; }
    public void setChildren(List<RuleNode> children) { this.children = children; }
    public void addChild(RuleNode child) { this.children.add(child); }
    public List<String> getActions() {
        return actions;
    }

    public void setActions(List<String> actions) {
        this.actions = actions;
    }

    @Override
    public RuleResult evaluateWithActions(Facts facts) {
        if (!enabled) {
            return new RuleResult(id, true, "规则组未启用，默认通过");
        }

        List<RuleResult> childResults = new ArrayList<>();
        boolean passed;

        List<RuleNode> activeChildren = children.stream()
                .filter(RuleNode::isEnabled)
                .sorted(Comparator.comparingInt(RuleNode::getPriority))
                .collect(Collectors.toList());

        if (logic == LogicType.AND) {
            passed = true;
            for (RuleNode child : activeChildren) {
                RuleResult res = child.evaluateWithActions(facts);
                childResults.add(res);
                if (!res.isPass()) {
                    passed = false;
                    break;
                }
            }
        } else { // OR
            passed = false;
            for (RuleNode child : activeChildren) {
                RuleResult res = child.evaluateWithActions(facts);
                childResults.add(res);
                if (res.isPass()) {
                    passed = true;
                    break;
                }
            }
        }

        if (passed && actions != null) {
            for (String actionName : actions) {
                Rule action = ActionRegistry.getAction(actionName);
                if (action != null) {
                    try {
                        action.execute(facts);
                    } catch (Exception e) {
                        return new RuleResult(id, false, "动作执行异常：" + e.getMessage());
                    }
                } else {
                    return new RuleResult(id, false, "动作未注册：" + actionName);
                }
            }
        }

        return new RuleResult(id, passed,
                passed ? "组合规则通过" : "组合规则未通过",
                childResults);
    }

}




