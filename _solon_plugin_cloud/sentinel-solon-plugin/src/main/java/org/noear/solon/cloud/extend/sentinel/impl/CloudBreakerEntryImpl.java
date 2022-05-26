package org.noear.solon.cloud.extend.sentinel.impl;

import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import org.noear.solon.cloud.model.BreakerEntrySim;
import org.noear.solon.cloud.model.BreakerException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author noear
 * @since 1.3
 */
public class CloudBreakerEntryImpl extends BreakerEntrySim {
    String breakerName;
    int thresholdValue;

    public CloudBreakerEntryImpl(String breakerName, int permits) {
        this.breakerName = breakerName;
        this.thresholdValue = permits;

        loadRules();
    }

    private void loadRules(){
        List<FlowRule> ruleList = new ArrayList<>();
        FlowRule rule = null;

        rule = new FlowRule();
        rule.setResource(breakerName);
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS); //qps
        rule.setCount(thresholdValue);
        ruleList.add(rule);

        rule = new FlowRule();
        rule.setResource(breakerName);
        rule.setGrade(RuleConstant.FLOW_GRADE_THREAD); //并发数
        rule.setCount(thresholdValue);
        ruleList.add(rule);

        FlowRuleManager.loadRules(ruleList);
    }

    @Override
    public AutoCloseable enter() throws BreakerException {
        try {
            return SphU.entry(breakerName);
        } catch (BlockException ex) {
            throw new BreakerException(ex);
        }
    }

    @Override
    public void reset(int value) {
        if(thresholdValue != value){
            thresholdValue = value;

            loadRules();
        }
    }
}
