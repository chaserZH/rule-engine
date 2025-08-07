package com.tommy.rulesengine.function;

import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorBoolean;
import com.googlecode.aviator.runtime.type.AviatorObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.Map;

/**
 * 自定义的客群函数
 * 所有自定义函数都实现这个标记接口用于SPI插件机制自动发现并注册
 */

public class CrowdFunction extends AbstractFunction implements RuleFunctionMarker {


    private static final Logger log = LoggerFactory.getLogger(CrowdFunction.class);


    @Override
    public String getName() {
        return "isInCrowd";
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject uidObj, AviatorObject crowdIdObj) {
        Long uid = Long.valueOf(uidObj.getValue(env).toString());
        String crowdId = crowdIdObj.getValue(env).toString();
        // 模拟调用客群服务接口（实际请调用真实服务）
        boolean result = mockCheckCrowd(uid, crowdId);
        log.debug("调用 isInCrowd({}, {}) = {}", uid, crowdId, result);

        return AviatorBoolean.valueOf(result);
    }

    private boolean mockCheckCrowd(Long uid, String crowdId) {
        // TODO: 调用外部系统，如客群服务，查询 uid 是否在指定 crowdId 中
        // 这里只是模拟：uid 为偶数且 crowdId 为 'crowdA' 才返回 true
        return uid % 2 == 0 && "200003".equals(crowdId);
    }

}
