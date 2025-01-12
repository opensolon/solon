/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.scheduling.retry;


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
     * 重试对象
     */
    private final Callee callee;


    private RetryableTask(Callee callee) {
        this.callee = callee;
    }

    /**
     * 创建重试任务
     *
     * @param task 任务
     * @return Void
     */
    public static RetryableTask<Void> of(Callee task) {
        return new RetryableTask<>(task);
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
        if (this.callee == null) {
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
                result = (T) callee.call();
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
            result = recover.recover(callee, throwable);
        }
    }
}