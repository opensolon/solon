package org.noear.solon.boot.prop.impl;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.boot.prop.ServerExecutorProps;
import org.noear.solon.boot.prop.ServerSignalProps;

/**
 * @author noear
 * @since 1.10
 */
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
     * */
    @Override
    public int getWrapPort() {
        return wrapPort;
    }

    /**
     * @since 1.12
     * */
    @Override
    public String getWrapHost() {
        return wrapHost;
    }

    ////////////////////////////////

    private void initExecutorProps(){
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
    public int getMaxThreads(boolean bio) {
        if (maxThreads > 0) {
            return maxThreads;
        } else {
            if (bio) {
                return getCoreThreads() * 32;
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
