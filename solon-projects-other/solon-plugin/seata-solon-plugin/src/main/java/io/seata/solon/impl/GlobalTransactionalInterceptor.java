package io.seata.solon.impl;

import io.seata.common.exception.ShouldNeverHappenException;
import io.seata.core.event.EventBus;
import io.seata.core.event.GuavaEventBus;
import io.seata.core.exception.TmTransactionException;
import io.seata.core.exception.TransactionExceptionCode;
import io.seata.integration.tx.api.event.DegradeCheckEvent;
import io.seata.solon.annotation.GlobalTransactional;
import io.seata.tm.api.FailureHandler;
import io.seata.tm.api.GlobalTransaction;
import io.seata.tm.api.TransactionalExecutor;
import io.seata.tm.api.TransactionalTemplate;
import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.Invocation;

import java.util.concurrent.atomic.AtomicBoolean;

import static io.seata.tm.api.GlobalTransactionRole.Participant;

/**
 * @author noear
 * @since 2.5
 */
public class GlobalTransactionalInterceptor implements Interceptor {
    private static final AtomicBoolean ATOMIC_DEGRADE_CHECK = new AtomicBoolean(false);
    private static final EventBus EVENT_BUS = new GuavaEventBus("degradeCheckEventBus", true);

    FailureHandler failureHandler;
    TransactionalTemplate transactionalTemplate;

    public GlobalTransactionalInterceptor(TransactionalTemplate transactionalTemplate) {
        this.transactionalTemplate = transactionalTemplate;
    }

    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        GlobalTransactional anno = inv.getMethodAnnotation(GlobalTransactional.class);
        if (anno == null) {
            anno = inv.getTargetAnnotation(GlobalTransactional.class);
        }

        if (anno == null) {
            return inv.invoke();
        } else {
            return tranInvoke(inv, anno);
        }
    }

    private Object tranInvoke(Invocation inv, GlobalTransactional anno) throws Throwable {
        boolean succeed = true;

        try {
            return transactionalTemplate.execute(new GlobalTransactionalExecutorImpl(inv, anno));
        } catch (TransactionalExecutor.ExecutionException e) {
            GlobalTransaction globalTransaction = e.getTransaction();

            // If Participant, just throw the exception to original.
            if (globalTransaction.getGlobalTransactionRole() == Participant) {
                throw e.getOriginalException();
            }

            TransactionalExecutor.Code code = e.getCode();
            Throwable cause = e.getCause();
            boolean timeout = isTimeoutException(cause);
            switch (code) {
                case RollbackDone:
                    if (timeout) {
                        throw cause;
                    } else {
                        throw e.getOriginalException();
                    }
                case BeginFailure:
                    succeed = false;
                    failureHandler.onBeginFailure(globalTransaction, cause);
                    throw cause;
                case CommitFailure:
                    succeed = false;
                    failureHandler.onCommitFailure(globalTransaction, cause);
                    throw cause;
                case RollbackFailure:
                    failureHandler.onRollbackFailure(globalTransaction, e.getOriginalException());
                    throw e.getOriginalException();
                case Rollbacking:
                    failureHandler.onRollbacking(globalTransaction, e.getOriginalException());
                    if (timeout) {
                        throw cause;
                    } else {
                        throw e.getOriginalException();
                    }
                default:
                    throw new ShouldNeverHappenException(String.format("Unknown TransactionalExecutor.Code: %s", code), e.getOriginalException());
            }
        } finally {
            if (ATOMIC_DEGRADE_CHECK.get()) {
                EVENT_BUS.post(new DegradeCheckEvent(succeed));
            }
        }
    }

    private boolean isTimeoutException(Throwable th) {
        if (null == th) {
            return false;
        }
        if (th instanceof TmTransactionException) {
            TmTransactionException exx = (TmTransactionException)th;
            if (TransactionExceptionCode.TransactionTimeout == exx.getCode()) {
                return true;
            }
        }
        return false;
    }
}
