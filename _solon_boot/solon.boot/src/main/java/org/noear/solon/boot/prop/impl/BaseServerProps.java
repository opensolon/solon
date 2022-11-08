package org.noear.solon.boot.prop.impl;

import org.noear.solon.boot.prop.ServerExecutorProps;
import org.noear.solon.boot.prop.ServerSignalProps;

/**
 * @author noear 2022/11/8 created
 */
public abstract class BaseServerProps implements ServerSignalProps, ServerExecutorProps {
    protected String name;
    protected int port;
    protected String host;


    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public String getHost() {
        return host;
    }

    protected int coreThreads;
    protected int maxThreads;
    protected long idleTimeout;


    /**
     * 核心线程数
     */
    @Override
    public int getCoreThreads() {
        if (coreThreads > 0) {
            return coreThreads;
        } else {
            return Math.max(Runtime.getRuntime().availableProcessors(), 2);
        }
    }

    /**
     * 最大线程数
     */
    @Override
    public int getMaxThreads(boolean bio) {
        if (maxThreads > 0) {
            return maxThreads;
        } else {
            if (bio) {
                return getCoreThreads() * 100;
            } else {
                return getCoreThreads() * 8;
            }
        }
    }

    /**
     * 闪置超时
     */
    @Override
    public long getIdleTimeout() { //idleTimeout
        if (idleTimeout > 0) {
            return idleTimeout;
        } else {
            return 60000;
        }
    }
}
