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
package org.noear.solon.boot.prop.impl;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.boot.prop.ServerExecutorProps;
import org.noear.solon.boot.prop.ServerSignalProps;

/**
 * 基础服务属性
 *
 * @author noear
 * @since 1.10
 * @deprecated 3.5
 * */
@Deprecated
public abstract class BaseServerProps implements ServerSignalProps, ServerExecutorProps {
    private String PROP_NAME = "server.@@.name";
    private String PROP_PORT = "server.@@.port";
    private String PROP_HOST = "server.@@.host";
    private String PROP_WRAP_PORT = "server.@@.wrapPort";
    private String PROP_WRAP_HOST = "server.@@.wrapHost";

    private String PROP_IO_BOUND = "server.@@.ioBound";
    private String PROP_CORE_THREADS = "server.@@.coreThreads";
    private String PROP_MAX_THREADS = "server.@@.maxThreads";
    private String PROP_IDLE_TIMEOUT = "server.@@.idleTimeout";

    private String name;
    private int port;
    private String host;
    private int wrapPort;
    private String wrapHost;

    private boolean ioBound;
    private int coreThreads;
    private int maxThreads;
    private long idleTimeout;

    protected BaseServerProps(String signalName, int portBase) {
        PROP_NAME = PROP_NAME.replace("@@", signalName);
        PROP_PORT = PROP_PORT.replace("@@", signalName);
        PROP_HOST = PROP_HOST.replace("@@", signalName);

        PROP_WRAP_PORT = PROP_WRAP_PORT.replace("@@", signalName);
        PROP_WRAP_HOST = PROP_WRAP_HOST.replace("@@", signalName);

        PROP_IO_BOUND = PROP_IO_BOUND.replace("@@", signalName);
        PROP_CORE_THREADS = PROP_CORE_THREADS.replace("@@", signalName);
        PROP_MAX_THREADS = PROP_MAX_THREADS.replace("@@", signalName);
        PROP_IDLE_TIMEOUT = PROP_IDLE_TIMEOUT.replace("@@", signalName);

        //
        initSignalProps(portBase);

        //
        initExecutorProps();
    }


    private void initSignalProps(int portBase) {
        name = Solon.cfg().get(PROP_NAME);
        port = Solon.cfg().getInt(PROP_PORT, 0);
        host = Solon.cfg().get(PROP_HOST);

        wrapPort = Solon.cfg().getInt(PROP_WRAP_PORT, 0);
        wrapHost = Solon.cfg().get(PROP_WRAP_HOST);

        //host + port
        if (port < 1) {
            port = portBase + Solon.cfg().serverPort();
        }

        if (Utils.isEmpty(host)) {
            host = Solon.cfg().serverHost();
        }

        //imageHost + imagePort
        if (wrapPort < 1) {
            wrapPort = Solon.cfg().serverWrapPort(true);

            if (wrapPort < 1) {
                wrapPort = port;
            }
        }

        if (Utils.isEmpty(wrapHost)) {
            wrapHost = Solon.cfg().serverWrapHost(true);

            if (Utils.isEmpty(wrapHost)) {
                wrapHost = host;
            }
        }
    }


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

    /**
     * @since 1.12
     */
    @Override
    public int getWrapPort() {
        return wrapPort;
    }

    /**
     * @since 1.12
     */
    @Override
    public String getWrapHost() {
        return wrapHost;
    }

    ////////////////////////////////

    private void initExecutorProps() {
        ioBound = Solon.cfg().getBool(PROP_IO_BOUND, true);
        idleTimeout = Solon.cfg().getLong(PROP_IDLE_TIMEOUT, 0L);

        //支持：16 或 x16（倍数）
        String coreThreadsStr = Solon.cfg().get(PROP_CORE_THREADS);
        if (Utils.isNotEmpty(coreThreadsStr)) {
            if (coreThreadsStr.startsWith("x")) {
                //倍数模式
                if (coreThreadsStr.length() > 1) {
                    coreThreads = getCoreNum() * Integer.parseInt(coreThreadsStr.substring(1));
                } else {
                    coreThreads = 0;
                }
            } else {
                coreThreads = Integer.parseInt(coreThreadsStr);
            }
        }

        //支持：16 或 x16（倍数）
        String maxThreadsStr = Solon.cfg().get(PROP_MAX_THREADS);
        if (Utils.isNotEmpty(maxThreadsStr)) {
            if (maxThreadsStr.startsWith("x")) {
                //倍数模式
                if (maxThreadsStr.length() > 1) {
                    maxThreads = getCoreNum() * Integer.parseInt(maxThreadsStr.substring(1));
                } else {
                    maxThreads = 0;
                }
            } else {
                maxThreads = Integer.parseInt(maxThreadsStr);
            }
        }
    }

    @Override
    public boolean isIoBound() {
        return ioBound;
    }

    /**
     * Cpu 核数
     */
    private int getCoreNum() {
        return Runtime.getRuntime().availableProcessors();
    }


    /**
     * 核心线程数
     */
    @Override
    public int getCoreThreads() {
        if (coreThreads > 0) {
            return coreThreads;
        } else {
            return Math.max(getCoreNum(), 2);
        }
    }

    /**
     * 最大线程数
     */
    @Override
    public int getMaxThreads(boolean isIoBound) {
        if (maxThreads > 0) {
            return maxThreads;
        } else {
            if (isIoBound) {
                return getCoreThreads() * 32;
            } else {
                return getCoreThreads() * 8;
            }
        }
    }

    /**
     * 闲置超时
     */
    @Override
    public long getIdleTimeout() { //idleTimeout
        return idleTimeout;
    }

    @Override
    public long getIdleTimeoutOrDefault() {
        if (idleTimeout > 0) {
            return idleTimeout;
        } else {
            return 300_000;
        }
    }
}