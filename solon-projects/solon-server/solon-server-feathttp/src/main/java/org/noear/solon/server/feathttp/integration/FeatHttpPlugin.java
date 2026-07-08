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
package org.noear.solon.server.feathttp.integration;

import java.util.Enumeration;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.*;
import org.noear.solon.core.bean.LifecycleBean;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.server.ServerConstants;
import org.noear.solon.server.ServerProps;
import org.noear.solon.server.feathttp.FeatHttpServerComb;
import org.noear.solon.server.feathttp.http.MultipartUtil;
import org.noear.solon.server.prop.impl.HttpServerProps;
import org.noear.solon.server.prop.impl.WebSocketServerProps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 * @author airhead
 */
public final class FeatHttpPlugin implements Plugin {
  static final Logger log = LoggerFactory.getLogger(FeatHttpPlugin.class);

  /** feat-core 中使用 LoggerFactory.getLogger() 的所有类（基于 feat-core 2.1.0）。 升级 feat-core 版本时需同步检查此清单。 */
  private static final String[] FEAT_LOGGER_CLASSES = {
    "tech.smartboot.feat.core.client.HttpRestImpl",
    "tech.smartboot.feat.core.client.WebSocketClient",
    "tech.smartboot.feat.core.common.FeatUtils",
    "tech.smartboot.feat.core.server.handler.HttpStaticResourceHandler",
    "tech.smartboot.feat.core.server.impl.Endpoint",
    "tech.smartboot.feat.core.server.impl.HttpEndpoint",
    "tech.smartboot.feat.core.server.impl.HttpMessageProcessor",
    "tech.smartboot.feat.core.server.impl.HttpRequestProtocol",
    "tech.smartboot.feat.core.server.upgrade.http2.Http2Session",
    "tech.smartboot.feat.core.server.upgrade.websocket.WebSocketResponseImpl",
    "tech.smartboot.feat.core.server.upgrade.websocket.WebSocketUpgrade",
    "tech.smartboot.feat.router.Router",
    "tech.smartboot.feat.router.session.LocalSessionManager",
  };

  private static Signal _signal;
  private FeatHttpServerComb _server;

  public static Signal signal() {
    return _signal;
  }

  public static String solon_server_ver() {
    return "FeatHttp 2.1/" + Solon.version();
  }

  @Override
  public void start(AppContext context) throws Throwable {
    if (context.app().enableHttp() == false) {
      return;
    }

    if (context.isStarted()) {
      start0(context);
    } else {
      context.lifecycle(
          ServerConstants.SIGNAL_LIFECYCLE_INDEX,
          new LifecycleBean() {
            @Override
            public void postStart() throws Throwable {
              start0(context);
            }
          });
    }
  }

  @Override
  public void stop() throws Throwable {
    if (_server != null) {
      _server.stop();
      _server = null;

      log.info("Server:main: feat: Has Stopped (" + solon_server_ver() + ")");
    }
  }

  private void start0(AppContext context) throws Throwable {
    installJulBridge();

    // 初始化属性
    ServerProps.init();
    MultipartUtil.init();

    HttpServerProps props = new HttpServerProps();
    final String _host = props.getHost();
    final int _port = props.getPort();
    final String _name = props.getName();

    long time_start = System.currentTimeMillis();

    _server = new FeatHttpServerComb(props);
    _server.enableWebSocket(context.app().enableWebSocket());
    _server.setCoreThreads(props.getCoreThreads());

    if (props.isIoBound()) {
      // 如果是io密集型的，加二段线程池
      _server.setExecutor(props.newWorkExecutor("feat-"));
    }

    _server.setHandler(context.app()::tryHandle);

    // 尝试事件扩展
    EventBus.publish(_server);
    _server.start(_host, _port);

    final String _wrapHost = props.getWrapHost();
    final int _wrapPort = props.getWrapPort();
    _signal = new SignalSim(_name, _wrapHost, _wrapPort, "http", SignalType.HTTP);
    context.app().signalAdd(_signal);

    long time_end = System.currentTimeMillis();

    String connectorInfo =
        "solon.connector:main: feat: Started ServerConnector@{HTTP/1.1,[http/1.1]";
    if (context.app().enableWebSocket()) {
      // 有名字定义时，添加信号注册
      WebSocketServerProps wsProps = WebSocketServerProps.getInstance();
      if (Utils.isNotEmpty(wsProps.getName())) {
        SignalSim wsSignal =
            new SignalSim(wsProps.getName(), _wrapHost, _wrapPort, "ws", SignalType.WEBSOCKET);
        context.app().signalAdd(wsSignal);
      }

      String wsServerUrl = props.buildWsServerUrl(_server.isSecure());
      log.info(connectorInfo + "[WebSocket]}{" + wsServerUrl + "}");
    }

    String httpServerUrl = props.buildHttpServerUrl(_server.isSecure());
    log.info(connectorInfo + "}{" + httpServerUrl + "}");
    log.info(
        "Server:main: feat: Started ("
            + solon_server_ver()
            + ") @"
            + (time_end - time_start)
            + "ms");
  }

  /** 1.将 SLF4JBridgeHandler 安装到 JUL root logger，2.预热所有 feat-core logger 并桥接到 SLF4J */
  private void installJulBridge() {
    System.setProperty("feat.log.level", "ALL");
    SLF4JBridgeHandler.removeHandlersForRootLogger();
    SLF4JBridgeHandler.install();

    bridgeFeatLoggers();
  }

  /** 桥接 feat-core RunLogger 创建的 JUL logger：移除原有 handler，打开 parent 传播。 */
  private void bridgeFeatLoggers() {
    // 预热 feat-core 所有使用 LoggerFactory 的 logger
    for (String className : FEAT_LOGGER_CLASSES) {
      tech.smartboot.feat.core.common.logging.LoggerFactory.getLogger(className);
    }

    LogManager manager = LogManager.getLogManager();
    Enumeration<String> names = manager.getLoggerNames();
    while (names.hasMoreElements()) {
      String name = names.nextElement();
      if (name != null && name.startsWith("tech.smartboot.feat")) {
        java.util.logging.Logger julLogger = manager.getLogger(name);
        if (julLogger != null) {
          Handler[] handlers = julLogger.getHandlers();
          if (handlers == null || handlers.length == 0) {
            continue;
          }

          for (Handler h : handlers) {
            julLogger.removeHandler(h);
          }

          julLogger.setUseParentHandlers(true);
          julLogger.setLevel(Level.ALL);
        }
      }
    }
  }
}
