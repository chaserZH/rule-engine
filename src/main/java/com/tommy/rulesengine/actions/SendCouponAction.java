package com.tommy.rulesengine.actions;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;

/**
 * 自定义规则动作：发券
 * @author zhanghao
 */
public class SendCouponAction implements RuleActionMarker {

    private boolean enabled = true;

    @Override
    public String getName() {
        return "SendCouponAction";
    }

    @Override
    public String getDescription() {
        return "发送优惠券的动作";
    }

    @Override
    public int getPriority() {
        return 1; // 优先级
    }

    @Override
    public boolean evaluate(Facts facts) {
        // 动作本身通常不用条件判断，这里可以返回true表示执行
        return enabled;
    }

    @Override
    public void execute(Facts facts) throws Exception {
        Long uid = facts.get("uid");
        // 业务逻辑，比如发优惠券
        System.out.println("发送优惠券给用户: " + uid);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public int compareTo(Rule o) {
        if (o == null) {
            return -1;
        }
        return Integer.compare(this.getPriority(), o.getPriority());
    }
}
