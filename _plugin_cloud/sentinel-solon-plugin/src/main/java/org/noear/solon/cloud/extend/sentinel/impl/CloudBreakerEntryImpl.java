package org.noear.solon.cloud.extend.sentinel.impl;

import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import org.noear.solon.cloud.model.BreakerException;
import org.noear.solon.cloud.model.BreakerEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * @author noear
 * @since 1.3
 */
public class CloudBreakerEntryImpl implements BreakerEntry {
   String breakerName;

    public CloudBreakerEntryImpl(String breakerName, int permits) {
        this.breakerName = breakerName;

        List<FlowRule> ruleList = new ArrayList<>();
        FlowRule rule = null;

        rule = new FlowRule();
        rule.setResource(breakerName);
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS); //qps
        rule.setCount(permits);
        ruleList.add(rule);

        rule = new FlowRule();
        rule.setResource(breakerName);
        rule.setGrade(RuleConstant.FLOW_GRADE_THREAD); //并发数
        rule.setCount(permits);
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
}
