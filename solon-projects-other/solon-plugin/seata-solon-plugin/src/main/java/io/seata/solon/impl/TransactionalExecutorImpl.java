package io.seata.solon.impl;

import io.seata.solon.annotation.SeataTran;
import io.seata.tm.api.TransactionalExecutor;
import io.seata.tm.api.transaction.NoRollbackRule;
import io.seata.tm.api.transaction.RollbackRule;
import io.seata.tm.api.transaction.TransactionInfo;
import org.noear.solon.Utils;
import org.noear.solon.core.aspect.Invocation;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author noear 2023/8/16 created
 */
public class TransactionalExecutorImpl implements TransactionalExecutor {
    Invocation inv;
    SeataTran anno;

    public TransactionalExecutorImpl(Invocation inv, SeataTran anno) {
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
        transactionInfo.setRollbackRules(rollbackRules);
        return transactionInfo;
    }


    private String getName() {
        if (Utils.isEmpty(anno.name())) {
            String paramTypes = Arrays.stream(inv.method().getMethod().getParameterTypes())
                    .map(Class::getName)
                    .collect(Collectors.joining(", ", "(", ")"));
            return inv.method().getMethod().getName() + paramTypes;
        } else {
            return anno.name();
        }
    }
}
