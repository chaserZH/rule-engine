package com.tommy.rulesengine.parser;


import com.tommy.rulesengine.model.RuleNode;

import java.util.List;

/**
 * 规则解析器
 * @author zhanghao
 */
public interface RuleParser {


    String getFormate();

    /**
     * 解析规则
     * @param content 规则内容
     * @return 规则节点列表
     */
    List<RuleNode> parse(String content);
}
