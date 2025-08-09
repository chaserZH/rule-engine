package com.tommy.rulesengine.actions;

import com.tommy.rulesengine.function.CrowdFunction;
import org.jeasy.rules.api.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 动作注册中心
 * @author zhanghao
 */
public class ActionRegistry {

    private static final Logger log = LoggerFactory.getLogger(ActionRegistry.class);


    private static final ConcurrentHashMap<String, Rule> ACTIONS = new ConcurrentHashMap<>();

    static {
        ServiceLoader<RuleActionMarker> loader = ServiceLoader.load(RuleActionMarker.class);
        for (RuleActionMarker action : loader) {
            ACTIONS.put(action.getName(), action);
            log.info("Registered SPI action: {}" , action.getName());
        }
    }


    public static Rule getAction(String name) {
        return ACTIONS.get(name);
    }
}