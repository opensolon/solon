package io.seata.solon.impl;

import io.seata.solon.annotation.GlobalTransactional;
import io.seata.tm.api.TransactionalExecutor;
import io.seata.tm.api.transaction.NoRollbackRule;
import io.seata.tm.api.transaction.RollbackRule;
import io.seata.tm.api.transaction.TransactionInfo;
import org.noear.solon.Utils;
import org.noear.solon.core.aspect.Invocation;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 全局事务执行器
 *
 * @author noear
 * @since 2.5
 */
public class GlobalTransactionalExecutorImpl implements TransactionalExecutor {
    Invocation inv;
    GlobalTransactional anno;

    public GlobalTransactionalExecutorImpl(Invocation inv, GlobalTransactional anno) {
        this.inv = inv;
        this.anno = anno;
    }

    @Override
    public Object execute() throws Throwable {
        return inv.invoke();
    }

    @Override
    public TransactionInfo getTransactionInfo() {
        TransactionInfo transactionInfo = new TransactionInfo();

        transactionInfo.setTimeOut(anno.timeoutMills());
        transactionInfo.setName(getName());
        transactionInfo.setRollbackRules(getRollbackRules());
        transactionInfo.setPropagation(anno.propagation());
        transactionInfo.setLockRetryInterval(anno.lockRetryInterval());
        transactionInfo.setLockRetryTimes(anno.lockRetryTimes());
        transactionInfo.setLockStrategyMode(anno.lockStrategyMode());

        return transactionInfo;
    }

    /**
     * Get the rollback rule
     * */
    private Set<RollbackRule> getRollbackRules() {
        Set<RollbackRule> rollbackRules = new LinkedHashSet<>();

        for (Class<?> rbRule : anno.rollbackFor()) {
            rollbackRules.add(new RollbackRule(rbRule));
        }
        for (String rbRule : anno.rollbackForClassName()) {
            rollbackRules.add(new RollbackRule(rbRule));
        }
        for (Class<?> rbRule : anno.noRollbackFor()) {
            rollbackRules.add(new NoRollbackRule(rbRule));
        }
        for (String rbRule : anno.noRollbackForClassName()) {
            rollbackRules.add(new NoRollbackRule(rbRule));
        }

        return rollbackRules;
    }


    /**
     * Get global transaction name
     * */
    private String getName() {
        if (Utils.isEmpty(anno.name())) {
            StringBuilder sb = new StringBuilder(inv.method().getMethod().getName()).append("(");

            Class<?>[] params = inv.method().getMethod().getParameterTypes();
            int in = 0;
            for (Class<?> clazz : params) {
                sb.append(clazz.getName());
                if (++in < params.length) {
                    sb.append(", ");
                }
            }
            return sb.append(")").toString();
        } else {
            return anno.name();
        }
    }
}