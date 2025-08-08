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

    public RuleGroup(String id, String name, String description, int priority, LogicType logic, List<RuleNode> children) {
        super(id, RuleGroupType.COMPOSITE, name, description, priority);
        this.logic = logic;
        this.children = children;
    }

    public RuleGroup(String id, LogicType logic, List<RuleNode> children) {
        super(id, RuleGroupType.COMPOSITE);
        this.logic = Objects.requireNonNull(logic);
        this.children = new ArrayList<>(Objects.requireNonNull(children));
    }


    public LogicType getLogic() {
        return logic;
    }
    
    public void setLogic(LogicType logic) {
        this.logic = logic;
    }
    
    
    public List<RuleNode> getChildren() {
        return children;
    }
    
    public void addChild(RuleNode child) {
        this.children.add(child);
    }

    public void setChildren(List<RuleNode> children) {
        this.children = children;
    }


    @Override
    public RuleResult evaluateWithResult(Facts facts) {
        RuleResult.Builder builder = new RuleResult.Builder(id);
        List<RuleResult> childResults = new ArrayList<>();
        boolean passed = false;

        switch (logic) {
            case AND:
                childResults = children.stream()
                        .sorted(Comparator.comparingInt(RuleNode::getPriority))
                        .map(child -> child.evaluateWithResult(facts))
                        .collect(Collectors.toList());
                passed = childResults.stream().allMatch(RuleResult::isPass);
                break;
                case OR:
                    childResults = children.stream()
                            .sorted(Comparator.comparingInt(RuleNode::getPriority))
                            .map(child -> child.evaluateWithResult(facts))
                            .collect(Collectors.toList());
                    passed = childResults.stream().anyMatch(RuleResult::isPass);
                    break;
                    default:
                        break;
        }

        return builder.pass(passed)
                .message(passed ? "组合规则通过" : "组合规则未通过")
                .children(childResults)
                .build();
    }

    @Override
    public String toString() {
        return "RuleGroup{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", priority=" + priority +
                ", children=" + children +
                '}';
    }
}
