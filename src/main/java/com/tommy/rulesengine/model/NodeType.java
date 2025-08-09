package com.tommy.rulesengine.model;

import java.util.Locale;

/**
 * 规则组类型
 * @author zhanghao
 */
public enum NodeType {

    /**
     * 叶子节点
     */
    LEAF("LEAF"),

    /**
     * 组合节点
     */
    COMPOSITE("COMPOSITE"),

    ;

    private final String type;

    NodeType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static NodeType of(String type) {
        for (NodeType nodeType : NodeType.values()) {
            if (nodeType.type.equals(type)) {
                return nodeType;
            }
        }
        return null;
    }

    public static boolean isSupported(String type) {
        for (NodeType nodeType : NodeType.values()) {
            if (nodeType.type.equals(type.toUpperCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }

}
