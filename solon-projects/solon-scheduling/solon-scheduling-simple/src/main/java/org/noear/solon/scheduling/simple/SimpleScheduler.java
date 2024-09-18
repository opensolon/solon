/*
 * Copyright 2017-2024 noear.org and authors
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
package org.noear.solon.scheduling.simple;

import org.noear.java_cron.CronExpressionPlus;
import org.noear.java_cron.CronUtils;
import org.noear.solon.Utils;
import org.noear.solon.core.Lifecycle;
import org.noear.solon.core.util.RunUtil;
import org.noear.solon.scheduling.scheduled.JobHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.ScheduledFuture;

/**
 * Job 简单调度器
 *
 * @author noear
 * @since 2.2
 */
public class SimpleScheduler implements Lifecycle {
    static final Logger log = LoggerFactory.getLogger(SimpleScheduler.class);

    private JobHolder jobHolder;
    /**
     * 调度表达式
     */
    private CronExpressionPlus cron;

    /**
     * 休息时间
     */
    private long sleepMillis;

    /**
     * 基准时间（对于比对）
     */
    private Date baseTime;
    /**
     * 下次执行时间
     */
    private Date nextTime;
    /**
     * 执行任务
     */
    private ScheduledFuture<?> jobFutureOfFixed;

    public SimpleScheduler(JobHolder jobHolder) {
        this.jobHolder = jobHolder;

        if (Utils.isNotEmpty(jobHolder.getScheduled().cron())) {
            this.cron = CronUtils.get(jobHolder.getScheduled().cron());

            if (Utils.isNotEmpty(jobHolder.getScheduled().zone())) {
                this.cron.setTimeZone(TimeZone.getTimeZone(jobHolder.getScheduled().zone()));
            }
        }
    }

    private boolean isStarted = false;

    /**
     * 是否已开始
     */
    public boolean isStarted() {
        return isStarted;
    }

    /**
     * 开始
     */
    @Override
    public void start() throws Throwable {
        if (isStarted) {
            return;
        } else {
            isStarted = true;
        }

        if (jobHolder.getScheduled().fixedDelay() > 0) {
            jobFutureOfFixed = RunUtil.scheduleWithFixedDelay(this::exec0,
                    jobHolder.getScheduled().initialDelay(),
                    jobHolder.getScheduled().fixedDelay());
        } else if (jobHolder.getScheduled().fixedRate() > 0) {
            jobFutureOfFixed = RunUtil.scheduleAtFixedRate(this::exec0,
                    jobHolder.getScheduled().initialDelay(),
                    jobHolder.getScheduled().fixedRate());
        } else {
            RunUtil.parallel(this::run);
        }
    }

    /**
     * 停止
     */
    @Override
    public void stop() throws Throwable {
        if (isStarted = false) {
            return;
        } else {
            isStarted = false;
        }

        if (jobFutureOfFixed != null) {
            jobFutureOfFixed.cancel(false);
        }
    }


    private void run() {
        if (baseTime == null) {
            baseTime = new Date();
        }

        if (isStarted == false) {
            return;
        }

        try {
            runAsCron();
        } catch (Throwable e) {
            e = Utils.throwableUnwrap(e);
            //过滤中断异常
            if (e instanceof InterruptedException == false) {
                log.warn(e.getMessage(), e);
            }
        }

        if (sleepMillis < 0) {
            sleepMillis = 100;
        }

        RunUtil.delay(this::run, sleepMillis);
    }

    /**
     * 调度
     */
    private void runAsCron() throws Throwable {
        //::按表达式调度（并行调用）
        nextTime = cron.getNextValidTimeAfter(baseTime);

        if (nextTime != null) {
            sleepMillis = System.currentTimeMillis() - nextTime.getTime();

            if (sleepMillis >= 0) {
                if (sleepMillis <= 1000) {
                    RunUtil.parallel(this::exec0);
                }

                baseTime = nextTime;
                nextTime = cron.getNextValidTimeAfter(baseTime);
                if (nextTime != null) {
                    if (sleepMillis <= 1000) {
                        //重新设定休息时间
                        sleepMillis = System.currentTimeMillis() - nextTime.getTime();
                    }
                }
            }
        }

        if (nextTime == null) {
            isStarted = false;
            log.warn("The cron expression has expired, and the job is complete!");
        }
    }

    /**
     * 执行
     */
    private void exec0() {
        try {
            jobHolder.handle(null);
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }
}