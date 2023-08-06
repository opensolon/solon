package org.noear.solon.extend.sqltoy.configure;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public class SqlToyContextTaskPoolProperties {
    /**
     * 指定线程池名称，该属性指定后则以指定的线程池作为默认线程池
     */
    private String targetPoolName = "none";

    /**
     * 线程前缀
     */
    private String threadNamePrefix = "sqltoyThreadPool";

    /**
     * 线程池维护线程的最少数量,核心线程数
     */
    private Integer corePoolSize = Runtime.getRuntime().availableProcessors() / 2 + 1;

    /**
     * 线程池维护线程的最大数量
     */
    private Integer maxPoolSize = Runtime.getRuntime().availableProcessors() * 3;

    /**
     * 线程池所使用的缓冲队列
     */
    private Integer queueCapacity = 200;

    /**
     * 线程池维护线程所允许的空闲时间
     */
    private Integer keepAliveSeconds = 60;
    /**
     * 决定使用ThreadPool的shutdown()还是shutdownNow()方法来关闭，默认为false
     */
    private Boolean waitForTasksToCompleteOnShutdown = Boolean.TRUE;
    /**
     * 超时中断时间
     */
    private Integer awaitTerminationSeconds = -1;
    /**
     * 拒绝策略
     */
    private RejectedExecutionHandler rejectedExecutionHandler = new ThreadPoolExecutor.CallerRunsPolicy();

    public String getTargetPoolName() {
        return targetPoolName;
    }

    public void setTargetPoolName(String targetPoolName) {
        this.targetPoolName = targetPoolName;
    }

    public String getThreadNamePrefix() {
        return threadNamePrefix;
    }

    public void setThreadNamePrefix(String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix;
    }

    public Integer getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(Integer corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public Integer getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(Integer maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public Integer getQueueCapacity() {
        return queueCapacity;
    }

    public void setQueueCapacity(Integer queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

    public Integer getKeepAliveSeconds() {
        return keepAliveSeconds;
    }

    public void setKeepAliveSeconds(Integer keepAliveSeconds) {
        this.keepAliveSeconds = keepAliveSeconds;
    }

    public Boolean getWaitForTasksToCompleteOnShutdown() {
        return waitForTasksToCompleteOnShutdown;
    }

    public void setWaitForTasksToCompleteOnShutdown(Boolean waitForTasksToCompleteOnShutdown) {
        this.waitForTasksToCompleteOnShutdown = waitForTasksToCompleteOnShutdown;
    }

    public Integer getAwaitTerminationSeconds() {
        return awaitTerminationSeconds;
    }

    public void setAwaitTerminationSeconds(Integer awaitTerminationSeconds) {
        this.awaitTerminationSeconds = awaitTerminationSeconds;
    }

    public RejectedExecutionHandler getRejectedExecutionHandler() {
        return rejectedExecutionHandler;
    }

    public void setRejectedExecutionHandler(RejectedExecutionHandler rejectedExecutionHandler) {
        this.rejectedExecutionHandler = rejectedExecutionHandler;
    }
}
