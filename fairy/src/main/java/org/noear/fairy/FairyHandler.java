package org.noear.fairy;

import org.noear.fairy.annotation.Alias;
import org.noear.fairy.annotation.FairyClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Fairy - 调用处理程序
 *
 * @author noear
 * @since 1.0
 * */
public class FairyHandler implements InvocationHandler {
    private final FairyConfig config;
    private final FairyClient client;

    private final String name;
    private final String path;
    private final String server;

    public FairyHandler(FairyConfig config, FairyClient client) {
        this.config = config;
        this.client = client;

        //1.运行配置器
        if (client != null) {
            try {
                FairyConfiguration tmp = client.configuration().newInstance();

                if (tmp != null) {
                    tmp.config(client, new Fairy.Builder(config));
                }
            } catch (Exception ex) {
                throw new RuntimeException();
            }
        }

        //2.配置初始化
        config.tryInit();

        //3.获取server or url
        String sev = config.getServer();

        if (sev == null) {
            //1.优先从 XClient 获取服务地址或名称
            if (isEmpty(client.value()) == false) {
                sev = client.value();
            }

            //2.如果没有，就报错
            if (sev == null) {
                throw new FairyException("@FairyClient no value");
            }
        }

        if (sev.contains("://")) {
            server = sev;
            name = null;
            path = null;
        } else {
            if (sev.contains(":")) {
                server = null;
                name = sev.split(":")[0];
                path = sev.split(":")[1];
            } else {
                server = null;
                name = null;
                path = sev;
            }
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] vals) throws Throwable {
        if (client == null) {
            return null;
        }

        //调用准备
        String fun = method.getName();

        Alias alias = method.getAnnotation(Alias.class);


        if (alias != null && isEmpty(alias.value()) == false) {
            fun = alias.value();
        }

        //构建args
        Map<String, Object> args = new LinkedHashMap<>();
        Parameter[] names = method.getParameters();
        for (int i = 0, len = names.length; i < len; i++) {
            if (vals[i] != null) {
                args.put(names[i].getName(), vals[i]);
            }
        }

        //构建headers
        Map<String, String> headers = new LinkedHashMap<>();

        //>>添加接口header
        if (client.headers() != null) {
            for (String h : client.headers()) {
                String[] ss = h.split("=");
                headers.put(ss[0], ss[1]);
            }
        }

        String url = null;
        if (server == null) {
            url = config.getUpstream().getServer();

            if (url == null) {
                throw new RuntimeException("Solon client proxy: Not found upstream!");
            }

            if (path != null) {
                int idx = url.indexOf("/", 9);//https://a
                if (idx > 0) {
                    url = url.substring(0, idx);
                }

                if (path.endsWith("/")) {
                    fun = path + fun;
                } else {
                    fun = path + "/" + fun;
                }
            }

        } else {
            url = server;
        }

        //执行调用
        return new Fairy(config)
                .url(url, fun)
                .call(headers, args)
                .getObject(method.getReturnType());
    }


    private static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }
}
