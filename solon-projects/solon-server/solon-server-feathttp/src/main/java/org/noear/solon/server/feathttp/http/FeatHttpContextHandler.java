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
package org.noear.solon.server.feathttp.http;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.server.ServerProps;
import org.noear.solon.server.feathttp.integration.FeatHttpPlugin;
import org.noear.solon.server.feathttp.websocket.FeatHttpWebSocketUpgrade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.smartboot.feat.core.common.HttpStatus;
import tech.smartboot.feat.core.server.HttpHandler;
import tech.smartboot.feat.core.server.HttpRequest;
import tech.smartboot.feat.core.server.impl.HttpEndpoint;

/**
 * @author airhead
 */
public class FeatHttpContextHandler implements HttpHandler {
  static final Logger log = LoggerFactory.getLogger(FeatHttpContextHandler.class);
  private final Handler handler;
  protected Executor executor;
  private boolean enableWebSocket;

  public FeatHttpContextHandler(Handler handler) {
    this.handler = handler;
  }

  public void setExecutor(Executor executor) {
    this.executor = executor;
  }

  public void setEnableWebSocket(boolean enableWebSocket) {
    this.enableWebSocket = enableWebSocket;
  }

  @Override
  public void handle(HttpRequest httpRequest, CompletableFuture<Void> future) throws Throwable {
    FeatHttpContext ctx = new FeatHttpContext(httpRequest, future);

    if (executor == null || (isWebSocketUpgrade(httpRequest))) {
      handle0(ctx, future);
      return;
    }

    try {
      executor.execute(() -> handle0(ctx, future));
    } catch (RejectedExecutionException e) {
      handle0(ctx, future);
    }
  }

  @Override
  public void handle(HttpRequest httpRequest) throws Throwable {
    // Normal HTTP handling (sync fallback)
    CompletableFuture<Void> future = new CompletableFuture<>();
    handle(httpRequest, future);
  }

  @Override
  public void onClose(HttpEndpoint endpoint) {
    Object attachment = endpoint.getAioSession().getAttachment();
    if (attachment == null) {
      return;
    }

    if (attachment instanceof FeatHttpContext) {
      FeatHttpContext ctx = (FeatHttpContext) attachment;
      if (ctx.asyncStarted()) {
        try {
          try {
            ctx.asyncState.onComplete(ctx);
          } catch (Throwable e) {
            log.warn(e.getMessage(), e);
          }
        } finally {
          endpoint.getAioSession().setAttachment(null);
        }
      }
    }
  }

  protected void handle0(FeatHttpContext ctx, CompletableFuture<Void> future) {
    try {
      handleDo(ctx);
    } catch (Throwable e) {
      log.warn(e.getMessage(), e);
    } finally {
      if (ctx.asyncStarted() == false) {
        future.complete(null);
      }
    }
  }

  protected void handleDo(FeatHttpContext ctx) {
    try {
      if (ServerProps.output_meta) {
        ctx.headerSet("Solon-Server", FeatHttpPlugin.solon_server_ver());
      }

      HttpRequest httpRequest = ctx.innerGetRequest();
      if (isWebSocketUpgrade(httpRequest)) {
        FeatHttpWebSocketUpgrade wsUpgrade = new FeatHttpWebSocketUpgrade();
        wsUpgrade.captureHttpRequest(httpRequest);
        wsUpgrade.setupSubProtocol(httpRequest);
        httpRequest.upgrade(wsUpgrade);
        return;
      }

      handler.handle(ctx);

      if (ctx.asyncStarted() == false) {
        ctx.innerCommit();
      }

    } catch (Throwable e) {
      log.warn(e.getMessage(), e);

      ctx.innerGetResponse().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  private boolean isWebSocketUpgrade(HttpRequest request) {
    try {
      Collection<String> upgradeValues = request.getHeaders("Upgrade");
      return enableWebSocket
          && upgradeValues != null
          && upgradeValues.stream().anyMatch("websocket"::equalsIgnoreCase);
    } catch (Exception e) {
      return false;
    }
  }
}
