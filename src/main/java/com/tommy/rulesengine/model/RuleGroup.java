package com.tommy.rulesengine.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 规则组实体
 */
public class RuleGroup extends RuleNode {

    public enum Type { AND, OR }

    // "AND" 或 "OR"
    private Type type;
    private List<RuleNode> children = new ArrayList<>();

    public RuleGroup() {}

    public RuleGroup(String id, String name, Type type, int priority) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.priority = priority;
    }

    public RuleGroup(String id, String name, Type type, int priority, List<RuleNode> children) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.priority = priority;
        this.children = children;
    }

    @Override
    public RuleResult evaluate(Map<String, Object> context) {
        List<RuleResult> results = children.stream()
                .sorted(Comparator.comparingInt(RuleNode::getPriority))
                .map(child -> child.evaluate(context))
                .collect(Collectors.toList());

        boolean pass = (type == Type.AND)
                ? results.stream().allMatch(RuleResult::isPass)
                : results.stream().anyMatch(RuleResult::isPass);

        return new RuleResult(id, pass, "Group: " + name, results);
    }

    public void addChild(RuleNode child) {
        this.children.add(child);
    }

    public void setType(String typeStr) {
        this.type = Type.valueOf(typeStr);
    }

    public Type getType() {
        return type;
    }

    public List<RuleNode> getChildren() {
        return children;
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
