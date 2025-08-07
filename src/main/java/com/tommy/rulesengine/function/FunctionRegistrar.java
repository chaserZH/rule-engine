package com.tommy.rulesengine.function;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.ServiceLoader;

/**
 * SPI自动发现并注册自定义函数
 */
public class FunctionRegistrar {

    private static final Logger log = LoggerFactory.getLogger(FunctionRegistrar.class);


    public static void registerAll() {
        ServiceLoader<RuleFunctionMarker> loader = ServiceLoader.load(RuleFunctionMarker.class);

        for (RuleFunctionMarker function : loader) {
            if (function instanceof AbstractFunction ) {
                AbstractFunction abstractFunction = (AbstractFunction) function;
                AviatorEvaluator.addFunction(abstractFunction);
                log.info("Registered Aviator function: {}", abstractFunction.getName());
            }
        }
    }
}
