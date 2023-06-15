package org.noear.solon.scheduling.retry;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

/**
 * 重试任务类
 *
 * @author kongweiguang
 * @since 2.3
 */
public class RetryableTask<T> {

    /**
     * 存放指定异常类
     */
    private final List<Class<? extends Throwable>> exs = new ArrayList<>();
    /**
     * 执行后结果存放
     */
    private T result;
    /**
     * 执行法方法
     */
    private final Supplier<T> sup;
    /**
     * 重试次数
     */
    private long maxRetryCount;
    /**
     * 重试间隔
     */
    private long interval;
    /**
     * 重试间隔单位
     */
    private TimeUnit unit;
    /**
     * 重试达到最大次数后还是失败，使用兜底策略
     */
    private Supplier<T> recover;
    /**
     * 自定义重试策略
     */
    private BiPredicate<T, Throwable> predicate;


    private RetryableTask(Runnable run) {
        this.sup = () -> {
            run.run();
            return null;
        };
    }

    private RetryableTask(Supplier<T> sup) {
        this.sup = sup;
    }

    /**
     * 创建一个没有返回值的重试任务
     *
     * @param run 任务
     * @return Void
     */
    public static RetryableTask<Void> of(Runnable run) {
        return new RetryableTask<>(run);
    }

    /**
     * 创建一个有返回值的重试任务
     *
     * @param sup 任务
     * @return 执行结果
     */
    public static <T> RetryableTask<T> of(Supplier<T> sup) {
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
    @SafeVarargs
    public final RetryableTask<T> retryForExceptions(Class<? extends Throwable>... exs) {
        this.exs.addAll(Arrays.asList(exs));
        return this;
    }

    /**
     * 重试根据自定义策略
     * 第一个参数是执行结果，第二个参数是抛出的异常
     *
     * @param predicate 策略
     * @return this
     */
    public final RetryableTask<T> retryForPredicate(BiPredicate<T, Throwable> predicate) {
        this.predicate = predicate;
        return this;
    }

    /**
     * 达到最大重试次数后执行的备用方法，入参是重试过程中的异常
     *
     * @param recover 兜底策略
     * @return this
     */
    public RetryableTask<T> recover(Supplier<T> recover) {
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
     * 异步执行重试
     *
     * @return CompletableFuture
     */
    public CompletableFuture<RetryableTask<T>> asyncExecute() {
        return CompletableFuture.supplyAsync(this::execute);
    }

    /**
     * 开始执行重试方法
     *
     * @return this
     */
    public RetryableTask<T> execute() {
        if (this.sup == null) {
            throw new IllegalArgumentException("task parameter cannot be null");
        }

        if (this.unit == null) {
            throw new IllegalArgumentException("delay parameter cannot be null");
        }

        if (this.interval < 0) {
            throw new IllegalArgumentException("interval must be greater than 0");
        }

        if (predicate != null) {
            retryPHelper();
        } else if (!exs.isEmpty()) {
            if (this.maxRetryCount < 0) {
                throw new IllegalArgumentException("maxRetryCount must be greater than 0");
            }
            retryEHelper();
        } else {
            throw new IllegalArgumentException("predicate and exs cannot both be empty");
        }
        return this;
    }


    /**
     * 自定义重试方法
     */
    private void retryPHelper() {

        Throwable ex = null;
        while (true) {
            try {
                result = this.sup.get();
            } catch (Exception e) {
                ex = e;
            }

            if (Boolean.TRUE.equals(predicate.test(result, ex))) {
                try {
                    unit.sleep(interval);
                } catch (InterruptedException ignored) {

                }
            } else {
                if (this.recover != null) {
                    result = this.recover.get();
                }
                break;
            }
        }

    }


    /**
     * 指定异常重试
     **/
    private void retryEHelper() {

        while (--maxRetryCount >= 0) {

            try {
                result = this.sup.get();
                return;
            } catch (Exception e) {

                if (this.exs.stream().anyMatch(ex -> ex.isAssignableFrom(e.getClass()))) {
                    try {
                        unit.sleep(interval);
                    } catch (InterruptedException ignored) {

                    }
                } else {
                    break;
                }

            }

        }

        if (recover != null) {
            result = recover.get();
        }
    }

}

