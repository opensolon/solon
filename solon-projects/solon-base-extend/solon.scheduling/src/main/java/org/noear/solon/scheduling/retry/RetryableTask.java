package org.noear.solon.scheduling.retry;


import org.noear.solon.core.util.RunnableEx;
import org.noear.solon.core.util.SupplierEx;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 重试任务类
 *
 * @author kongweiguang
 * @since 2.3
 */
public class RetryableTask<T> {

    /**
     * 异常包含
     */
    private final Set<Class<? extends Throwable>> exceptionIncludes = new HashSet<>();
    /**
     * 异常排除
     */
    private final Set<Class<? extends Throwable>> exceptionExcludes = new HashSet<>();

    /**
     * 重试次数
     */
    private long maxRetryCount = 3;
    /**
     * 重试间隔
     */
    private long interval = 1;
    /**
     * 重试间隔单位
     */
    private TimeUnit unit = TimeUnit.SECONDS;
    /**
     * 重试达到最大次数后还是失败，使用兜底策略
     */
    private Recover<T> recover;

    /**
     * 执行后结果存放
     */
    private T result;
    /**
     * 执行法方法
     */
    private final SupplierEx<T> caller;

    private RetryableTask(RunnableEx run) {
        this.caller = () -> {
            run.run();
            return null;
        };
    }

    private RetryableTask(SupplierEx<T> sup) {
        this.caller = sup;
    }

    /**
     * 创建一个没有返回值的重试任务
     *
     * @param run 任务
     * @return Void
     */
    public static RetryableTask<Void> of(RunnableEx run) {
        return new RetryableTask<>(run);
    }

    /**
     * 创建一个有返回值的重试任务
     *
     * @param sup 任务
     * @return 执行结果
     */
    public static <T> RetryableTask<T> of(SupplierEx<T> sup) {
        return new RetryableTask<>(sup);
    }

    /**
     * 最大重试次数
     *
     * @param maxRetryCount 次数
     * @return this
     */
    public RetryableTask<T> maxRetryCount(long maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
        return this;
    }

    /**
     * 设置间隔时间
     *
     * @param interval 间隔时间
     */
    public RetryableTask<T> interval(long interval) {
        this.interval = interval;
        return this;
    }

    /**
     * 设置间隔时间单位
     *
     * @param unit 时间单位
     */
    public RetryableTask<T> unit(TimeUnit unit) {
        this.unit = unit;
        return this;
    }

    /**
     * 指定的异常类型需要重试
     *
     * @param exs 异常集合
     * @return this
     */
    public final RetryableTask<T> retryForIncludes(Class<? extends Throwable>... exs) {
        if (exs != null && exs.length > 0) {
            exceptionIncludes.addAll(Arrays.asList(exs));
        }

        return this;
    }

    public final RetryableTask<T> retryForExcludes(Class<? extends Throwable>... exs) {
        if (exs != null && exs.length > 0) {
            exceptionExcludes.addAll(Arrays.asList(exs));
        }

        return this;
    }

    /**
     * 达到最大重试次数后执行的备用方法，入参是重试过程中的异常
     *
     * @param recover 兜底策略
     * @return this
     */
    public RetryableTask<T> recover(Recover<T> recover) {
        this.recover = recover;
        return this;
    }

    /**
     * 获取答案
     *
     * @return 执行方法返回的值
     */
    public T get() {
        return this.result;
    }


    /**
     * 开始执行重试方法
     *
     * @return this
     */
    public RetryableTask<T> execute() throws Throwable {
        if (this.caller == null) {
            throw new IllegalArgumentException("task parameter cannot be null");
        }

        if (this.unit == null) {
            throw new IllegalArgumentException("delay parameter cannot be null");
        }

        if (this.interval < 0) {
            throw new IllegalArgumentException("interval must be greater than 0");
        }

        executeDo();

        return this;
    }

    private boolean retryPredicate(Throwable e) {
        if (exceptionExcludes.size() > 0) {
            if (exceptionExcludes.stream().anyMatch(ex -> ex.isAssignableFrom(e.getClass()))) {
                //如果排除成功
                return false;
            }
        }

        if (exceptionIncludes.size() > 0) {
            return exceptionIncludes.stream().anyMatch(ex -> ex.isAssignableFrom(e.getClass()));
        } else {
            return true;
        }
    }


    /**
     * 指定异常重试
     **/
    private void executeDo() throws Throwable {
        Throwable throwable = null;

        while (--maxRetryCount >= 0) {
            try {
                result = caller.get();
                return;
            } catch (Throwable e) {
                if (e instanceof InvocationTargetException) {
                    e = ((InvocationTargetException) e).getTargetException();
                }

                throwable = e;

                if (retryPredicate(e)) {
                    try {
                        unit.sleep(interval);
                    } catch (InterruptedException ignore) {

                    }
                } else {
                    throw e;
                }
            }
        }

        if (recover != null) {
            result = recover.recover(throwable);
        }
    }
}