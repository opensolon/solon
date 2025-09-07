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
package org.noear.nami;

import org.noear.nami.annotation.NamiClient;
import org.noear.nami.common.*;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.util.PathUtil;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Nami - 调用处理程序
 *
 * @author noear
 * @since 1.0
 * */
public class NamiHandler implements InvocationHandler {
    private final static Pattern pathKeyExpr = Pattern.compile("\\{([^\\\\}]+)\\}");

    private final Config config;
    private final NamiClient client;

    private final Map<String, String> headers0 = new LinkedHashMap<>();
    private final Class<?> clz0;
    private final Map<String, Map> pathKeysCached = new ConcurrentHashMap<>();

    /**
     * @param config 配置
     * @param client 客户端注解
     */
    public NamiHandler(Class<?> clz, Config config, NamiClient client) {
        this.config = config;
        this.client = client;

        this.clz0 = clz;

        try {
            init();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void init() throws Exception {
        //1.运行配置器
        if (client != null) {
            //尝试添加全局拦截器
            for (Filter mi : NamiManager.getFilters()) {
                config.filterAdd(mi);
            }

            //尝试配置器配置
            NamiConfiguration tmp = NamiManager.getConfigurator(client.configuration());
            if (tmp != null) {
                tmp.config(client, new NamiBuilder(config));
            }

            //尝试设置超时
            if (client.timeout() > 0) {
                config.setTimeout(client.timeout());
            }

            //尝试设置心跳
            if (client.heartbeat() > 0) {
                config.setHeartbeat(client.heartbeat());
            }

            //>>添加接口url
            if (TextUtils.isNotEmpty(client.url())) {
                config.setUrl(client.url());
            }

            //>>添加接口group
            if (TextUtils.isNotEmpty(client.group())) {
                config.setGroup(client.group());
            }

            //>>添加接口name
            if (TextUtils.isNotEmpty(client.name())) {
                config.setName(client.name());
            }

            //>>添加接口path
            if (TextUtils.isNotEmpty(client.path())) {
                config.setPath(client.path());
            }

            //>>添加接口header
            if (client.headers().length > 0) {
                for (String h : client.headers()) {
                    String[] ss = null;
                    if (h.contains(":")) {
                        ss = h.split(":");
                    } else {
                        ss = h.split("=");
                    }
                    if (ss.length == 2) {
                        headers0.put(ss[0].trim(), ss[1].trim());
                        config.setHeader(ss[0].trim(), ss[1].trim());
                    }
                }
            }

            //>>添加upstream
            if (client.upstream().length > 0) {
                config.setUpstream(new UpstreamFixed(Arrays.asList(client.upstream())));
            }
        }

        //2.配置初始化
        config.init();
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] vals) throws Throwable {
        //检查upstream
        if (TextUtils.isEmpty(config.getUrl()) && config.getUpstream() == null) {
            StringBuilder buf = new StringBuilder();
            buf.append("NamiClient: Not found upstream: ").append(clz0.getName());

            if (TextUtils.isNotEmpty(config.getName())) {
                buf.append(": '").append(config.getName()).append("'");
            }

            throw new NamiException(buf.toString());
        }

        //调用 Default 函数
        if (method.isDefault()) {
            return MethodHandlerUtils.invokeDefault(proxy, method, vals);
        }

        //调用 Object 函数
        if (method.getDeclaringClass() == Object.class) {
            return MethodHandlerUtils.invokeObject(clz0, proxy, method, vals);
        }

        MethodDesc methodWrap = MethodDesc.get(method);

        //构建 headers
        Map<String, String> headers = new HashMap<>(headers0);

        //mapping headers 优先1
        if (methodWrap.getHeaders() != null) {
            headers.putAll(methodWrap.getHeaders());
        }

        //attachment headers 优先2（处理附加信息）
        Map<String, String> contextMap = NamiAttachment.getData();
        if (contextMap.size() > 0) {
            headers.putAll(contextMap);
        }

        //构建 args
        Map<String, Object> args = new LinkedHashMap<>();
        Object body = null;
        List<ParameterDesc> params = methodWrap.getParameters();
        StringBuilder cookies = new StringBuilder();
        for (int i = 0, len = params.size(); i < len; i++) {
            if (vals[i] != null) {
                ParameterDesc pw = params.get(i);

                if (pw.isBody()) {
                    //body
                    body = vals[i];
                } else if (pw.isHeader()) {
                    //header
                    headers.put(pw.getName(), String.valueOf(vals[i]));
                } else if (pw.isCookie()) {
                    //cookie
                    if (cookies.length() > 0) {
                        cookies.append("; ");
                    }
                    cookies.append(pw.getName()).append("=").append(vals[i]);
                } else {
                    args.put(pw.getName(), vals[i]);
                }
            }
        }

        if(cookies.length() > 0) {
            headers.put("Cookie", cookies.toString());
        }

        //构建 path
        String path = method.getName();
        String action = null;

        //处理mapping
        if (methodWrap.getAction() != null) {
            action = methodWrap.getAction();
        }

        if (methodWrap.getPath() != null) {
            path = methodWrap.getPath();
        }


        //构建 baseUrl
        String baseUrl = null;
        if (TextUtils.isEmpty(config.getUrl())) {
            baseUrl = config.getUpstream().get();

            if (baseUrl == null) {
                StringBuilder buf = new StringBuilder();
                buf.append("NamiClient: Upstream not found server instance: ").append(clz0.getName());

                if (TextUtils.isEmpty(config.getName())) {
                    buf.append(": '").append(config.getName()).append("'");
                }

                throw new NamiException(buf.toString());
            }

            if (baseUrl.indexOf("://") < 0) {
                baseUrl = "http://" + baseUrl;
            }

            if (TextUtils.isNotEmpty(config.getPath())) {
                int idx = baseUrl.indexOf("/", 9);//https://a
                if (idx > 0) {
                    baseUrl = baseUrl.substring(0, idx);
                }

                path = PathUtil.joinUri(config.getPath(), path);
            }

        } else {
            baseUrl = config.getUrl();
        }

        if (path != null && path.indexOf("{") > 0) {
            //
            //处理Path参数
            //
            Map<String, String> pathKeys = buildPathKeys(path);

            for (Map.Entry<String, String> kv : pathKeys.entrySet()) {
                //
                //处理path参数不为String类型时报错的问题
                //String val = (String) args.get(kv.getValue());
                //
                Object arg = args.get(kv.getValue());

                if (arg != null) {
                    String val = arg.toString();
                    path = path.replace(kv.getKey(), val);
                    args.remove(kv.getValue());
                }
            }
        }

        //确定 action
        if (MethodType.ALL.name.equals(action)) {
            if (body == null && Utils.isEmpty(args)) {
                action = MethodType.GET.name;
            } else {
                action = MethodType.POST.name;
            }
        }

        //确定返回类型
        Type type = method.getGenericReturnType();
        if (type == null) {
            type = method.getReturnType();
        }

        if (baseUrl.startsWith("sd:")) {
            baseUrl = baseUrl.substring(3);
        }

        //执行调用
        Object rst = new Nami(config)
                .method(proxy, method)
                .action(action)
                .url(baseUrl, path)
                .callOrThrow(headers, args, body) //使用 OrThrow，可减少异常包装
                .getObjectOrThrow(type);

        return rst;//调试时，方便看
    }

    private Map<String, String> buildPathKeys(String path) {
        Map<String, String> pathKeys = pathKeysCached.computeIfAbsent(path, k -> {
            Map<String, String> pks = new LinkedHashMap<>();
            Matcher pm = pathKeyExpr.matcher(path);
            while (pm.find()) {
                pks.put(pm.group(), pm.group(1));
            }
            return pks;
        });

        return pathKeys;
    }
}