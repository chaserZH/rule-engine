package com.tommy.rulesengine.parser;

import com.tommy.rulesengine.exception.RuleEngineException;
import com.tommy.rulesengine.model.*;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;

/**
 * 规则校验器
 * @author zhanghao
 */
public class RuleValidator {

    private static final Logger log = LoggerFactory.getLogger(RuleValidator.class);

    /**
     * 校验规则组合法性（包括递归校验子节点）
     */
    public void validate(RuleGroup group) {
        log.info("begin validate rule group: {}", group.getId());


        Set<String> ids = new HashSet<>();
        validateNode(group, ids);
        log.info("end validate rule group: {}", group.getId());
    }

    private void validateNode(RuleNode node, Set<String> ids) {
        if (node instanceof RuleDefinition) {
            RuleDefinition def = (RuleDefinition) node;
            if (isBlank(def.getId())) {
                throw new RuleEngineException("Rule ID is blank");
            }
            if (isBlank(def.getExpression())) {
                throw new RuleEngineException("Rule expression is blank");
            }

            if (!ids.add(def.getId())) {
                throw new RuleEngineException("Duplicate rule ID found: " + def.getId());
            }
            if (def.getType() == null || def.getType() != RuleGroupType.LEAF) {
                throw new RuleEngineException("single rule type must be LEAF : " + def.getId());
            }


        } else if (node instanceof RuleGroup) {
            RuleGroup group = (RuleGroup) node;
            if (isBlank(group.getId())) {
                throw new RuleEngineException("Rule group ID is blank");
            }
            if (group.getChildren() == null || group.getChildren().isEmpty()) {
                throw new RuleEngineException("Rule group has no children: " + group.getId());
            }
            if (!ids.add(group.getId())) {
                throw new RuleEngineException("Duplicate rule group ID found: " + group.getId());
            }

            //组合节点
            if (group.getType() == null || group.getType() != RuleGroupType.COMPOSITE) {
                throw new RuleEngineException("single rule type must be COMPOSITE : " + group.getId());
            }
            if (group.getChildren().size() == 1) {
                // 自动视为 AND，不强制 type 为 AND
                if (group.getLogic() != LogicType.AND) {
                    throw new RuleEngineException("Rule group has one child, its type must be AND or unspecified: " + group.getId());
                }
            }else if (group.getChildren().size() == 2) {
                if (group.getLogic() == null) {
                    throw new RuleEngineException("Rule group has multiple children, its type must be specified (AND or OR): " + group.getId());
                }
            }else if (group.getChildren().size() > 2) {
                if (group.getType() == null) {
                    throw new RuleEngineException("Rule group has more than 2 children " + group.getId());
                }
            }

            List<RuleNode> children = group.getChildren();
            for (RuleNode child : children) {
                validateNode(child, ids);
            }
        } else {
            throw new RuleEngineException("Unknown RuleNode type: " + node.getClass());
        }
    }


    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
}

