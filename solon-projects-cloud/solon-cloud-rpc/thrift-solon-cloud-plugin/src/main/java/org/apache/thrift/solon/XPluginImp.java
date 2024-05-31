package org.apache.thrift.solon;

import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.solon.annotation.EnableThrift;
import org.apache.thrift.solon.annotation.ThriftClient;
import org.apache.thrift.solon.annotation.ThriftService;
import org.apache.thrift.solon.integration.ThriftClientBeanInjector;
import org.apache.thrift.solon.integration.ThriftServerProps;
import org.apache.thrift.solon.integration.ThriftServiceBeanBuilder;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TTransportException;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.*;
import org.noear.solon.core.util.LogUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Thrift 插件入口
 *
 * @author LIAO.Chunping
 * @author noear
 * @since 1.10
 */
public class XPluginImp implements Plugin {
    private static Signal _signal;

    public static Signal signal() {
        return _signal;
    }

    public static String solon_boot_ver() {
        return "thrift 0.20.0/" + Solon.version();
    }

    private TThreadedSelectorServer server;
    private Thread serverThread;

    private Map<Class<?>, Object> serviceMap;
    private Map<Class<?>, Object> clientMap;

    @Override
    public void start(AppContext context) {
        if (Solon.app().source().getAnnotation(EnableThrift.class) == null) {
            return;
        }
        serviceMap = new HashMap<>();
        clientMap = new HashMap<>();

        context.beanBuilderAdd(ThriftService.class, new ThriftServiceBeanBuilder(serviceMap));
        context.beanInjectorAdd(ThriftClient.class, new ThriftClientBeanInjector(clientMap));

        context.lifecycle(LifecycleIndex.PLUGIN_BEAN_USES, this::startForServer);

    }

    /**
     * 开始启动服务端
     */
    private void startForServer() throws TTransportException {
        if (serviceMap.isEmpty()) {
            return;
        }

        ThriftServerProps props = new ThriftServerProps(25000);
        final String _host = props.getHost();
        final int _port = props.getPort();
        final String _name = props.getName();

        long time_start = System.currentTimeMillis();

        TNonblockingServerSocket serverSocket = new TNonblockingServerSocket(props.getPort());

        TThreadedSelectorServer.Args serverArgs = new TThreadedSelectorServer.Args(serverSocket)
                .protocolFactory(new TBinaryProtocol.Factory())
                .processor(createMultiplexedProcessor());
        server = new TThreadedSelectorServer(serverArgs);
        serverThread = new Thread(server::serve);
        serverThread.start();

        final String _wrapHost = props.getWrapHost();
        final int _wrapPort = props.getWrapPort();
        _signal = new SignalSim(_name, _wrapHost, _wrapPort, "thrift", SignalType.SOCKET);

        Solon.app().signalAdd(_signal);

        long time_end = System.currentTimeMillis();

        LogUtil.global().info("Connector:main: thrift: Started ServerConnector@{thrift://localhost:" + _port + "}");
        LogUtil.global().info("Server:main: thrift: Started ("+solon_boot_ver()+") @" + (time_end - time_start) + "ms");

        LogUtil.global().info(solon_boot_ver());
        LogUtil.global().info("Server:main: thrift: Started ServerConnector@{localhost:" + props.getPort() + "}(" + solon_boot_ver() + ")");
    }

    /**
     * 创建多路复用处理器，将服务提供者注册进处理器中
     * <p>
     * serviceName： 默认为 @ThriftService 所在的类名，也可通过 serviceName 使用自定义的服务名
     *
     * @return 处理器
     */
    private TMultiplexedProcessor createMultiplexedProcessor() {
        TMultiplexedProcessor multiplexedProcessor = new TMultiplexedProcessor();
        serviceMap.forEach((k, v) -> {
            try {
                String serviceName = k.getSimpleName();
                ThriftService anno = k.getAnnotation(ThriftService.class);
                if (anno != null && Utils.isNotEmpty(anno.serviceName())) {
                    serviceName = anno.serviceName();
                }

                multiplexedProcessor.registerProcessor(serviceName, createTProcessor(v));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return multiplexedProcessor;
    }

    /**
     * 创建处理器
     *
     * @param o 服务提供类
     * @return 处理器
     */
    private TProcessor createTProcessor(Object o) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?>[] interfaces = o.getClass().getInterfaces();
        Class<?> ifaceInterface = null;
        for (Class<?> iface : interfaces) {
            if (iface.getName().contains("$Iface")) {
                ifaceInterface = iface;
            }
        }
        if (ifaceInterface == null) {
            throw new RuntimeException("Interface Iface not found");
        }
        Class<?> enclosingClass = ifaceInterface.getEnclosingClass();
        Class<?> processorClass = Class.forName(enclosingClass.getName() + "$Processor");
        Constructor<?> constructor = processorClass.getDeclaredConstructor(ifaceInterface);
        return (TProcessor) constructor.newInstance(o);
    }

    @Override
    public void stop() {
        if(server != null){
            server.stop();
            server = null;

            if(serverThread != null) {
                serverThread.interrupt();
            }

            LogUtil.global().info("Server:main: thrift: Has Stopped (" + solon_boot_ver() + ")");
        }
    }
}