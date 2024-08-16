package org.noear.solon.cloud.gateway;

import org.noear.solon.Utils;
import org.noear.solon.core.exception.StatusException;
import org.noear.solon.core.handle.Context;
import org.noear.solon.net.http.HttpUtils;
import org.noear.solon.web.reactive.RxHandler;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 分布式路由默认处理
 *
 * @author noear
 * @since 2.9
 */
public class CloudRouteHandlerDefault implements RxHandler {
    private CloudRoute route;

    public CloudRouteHandlerDefault(CloudRoute route) {
        this.route = route;
    }

    /**
     * 获取目标路径
     * */
    public String getTargetPath(Context ctx){
        //目标路径重组
        List<String> fromPathFragments = Arrays.asList(ctx.pathNew().split("/", -1));
        String targetPath = "/" + String.join("/", fromPathFragments.subList(route.getStripPrefix() + 1, fromPathFragments.size()));
        if (Utils.isNotEmpty(ctx.queryString())) {
            targetPath += "?" + ctx.queryString();
        }

        return targetPath;
    }

    /**
     * 处理
     */
    @Override
    public Mono<Void> handle(Context ctx) {
        URI uri = route.getUri();

        //目标路径重组
        String targetPath = getTargetPath(ctx);

        //构建请求工具
        HttpUtils httpUtils;
        if ("lb".equals(uri.getScheme())) {
            httpUtils = HttpUtils.http(uri.getHost(), targetPath);
        } else {
            String url = uri + targetPath;
            httpUtils = HttpUtils.http(url);
        }

        try {
            //同步 header
            for (Map.Entry<String, List<String>> kv : ctx.headersMap().entrySet()) {
                for (String val : kv.getValue()) {
                    httpUtils.headerAdd(kv.getKey(), val);
                }
            }

            //同步 body（流复制）
            httpUtils.bodyRaw(ctx.bodyAsStream(), ctx.contentType());

            return Mono.create(monoSink -> {
                //异步 执行
                httpUtils.execAsync(ctx.method(), (isSuccessful, resp, error) -> {
                    try {
                        if (resp != null) {
                            ctx.status(resp.code());
                            ctx.contentType(resp.contentType());
                            for (String name : resp.headerNames()) {
                                for (String v : resp.headers(name)) {
                                    ctx.headerAdd(name, v);
                                }
                            }
                            //输出流复制
                            ctx.output(resp.body());
                            monoSink.success();
                        } else {
                            monoSink.error(error);
                        }
                    } catch (Throwable ex) {
                        monoSink.error(ex);
                    }
                });
            });
        } catch (Throwable ex) {
            //如查出错，说明客户端发的数据有问题
            return Mono.error(new StatusException(ex, 400));
        }
    }
}