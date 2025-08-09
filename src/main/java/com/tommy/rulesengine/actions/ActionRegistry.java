package com.tommy.rulesengine.actions;

import org.jeasy.rules.api.Rule;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 动作注册中心
 */
public class ActionRegistry {
    private static final ConcurrentHashMap<String, Rule> ACTIONS = new ConcurrentHashMap<>();

    public static void register(String name, Rule action) {
        ACTIONS.put(name, action);
    }

    public static Rule getAction(String name) {
        return ACTIONS.get(name);
    }
}