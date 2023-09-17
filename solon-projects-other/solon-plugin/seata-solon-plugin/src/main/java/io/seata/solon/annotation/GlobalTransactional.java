package io.seata.solon.annotation;

import io.seata.common.DefaultValues;
import io.seata.common.LockStrategyMode;
import io.seata.tm.api.transaction.Propagation;

import java.lang.annotation.*;

/**
 * 全局事务
 *
 * @author noear
 * @since 2.5
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.TYPE})
@Inherited
public @interface GlobalTransactional {

    /**
     * Global transaction timeoutMills in MILLISECONDS.
     * If client.tm.default-global-transaction-timeout is configured, It will replace the DefaultValues
     * .DEFAULT_GLOBAL_TRANSACTION_TIMEOUT.
     *
     * @return timeoutMills in MILLISECONDS.
     */
    int timeoutMills() default DefaultValues.DEFAULT_GLOBAL_TRANSACTION_TIMEOUT;

    /**
     * Given name of the global transaction instance.
     *
     * @return Given name.
     */
    String name() default "";

    /**
     * roll back for the Class
     *
     * @return the class array of the rollback for
     */
    Class<? extends Throwable>[] rollbackFor() default {};

    /**
     * roll back for the class name
     *
     * @return the class name of rollback for
     */
    String[] rollbackForClassName() default {};

    /**
     * not roll back for the Class
     *
     * @return the class array of no rollback for
     */
    Class<? extends Throwable>[] noRollbackFor() default {};

    /**
     * not roll back for the class name
     *
     * @return string [ ]
     */
    String[] noRollbackForClassName() default {};

    /**
     * the propagation of the global transaction
     *
     * @return propagation
     */
    Propagation propagation() default Propagation.REQUIRED;

    /**
     * customized global lock retry interval(unit: ms)
     * you may use this to override global config of "client.rm.lock.retryInterval"
     * note: 0 or negative number will take no effect(which mean fall back to global config)
     *
     * @return int
     */
    int lockRetryInterval() default 0;

    /**
     * customized global lock retry times
     * you may use this to override global config of "client.rm.lock.retryTimes"
     * note: negative number will take no effect(which mean fall back to global config)
     *
     * @return int
     */
    int lockRetryTimes() default -1;

    /**
     * pick the Acquire lock policy
     *
     * @return lock strategy mode
     */
    LockStrategyMode lockStrategyMode() default LockStrategyMode.PESSIMISTIC;

}