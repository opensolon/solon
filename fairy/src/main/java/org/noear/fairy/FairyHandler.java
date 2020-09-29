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

    private final String name;
    private final String path;
    private final String url;

    private String[] client_headers;

    /**
     * @param config 配置
     * @param client 客户端注解
     * */
    public FairyHandler(FairyConfig config, FairyClient client) {
        this.config = config;

        //1.运行配置器
        if (client != null) {
            client_headers = client.headers();

            try {
                FairyConfiguration tmp = client.configuration().newInstance();

                if (tmp != null) {
                    tmp.config(client, new Fairy.Builder(config));
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }

        //2.配置初始化
        config.tryInit();

        //3.获取 or url
        String url0 = config.getUrl();

        if (url0 == null && client != null) {
            //1.优先从 XClient 获取服务地址或名称
            if (isEmpty(client.value()) == false) {
                url0 = client.value();
            }
        }

        //2.如果没有，就报错
        if (url0 == null) {
            throw new FairyException("FairyClient config is wrong");
        }

        if (url0.contains("://")) {
            url = url0;
            name = null;
            path = null;
        } else {
            if (url0.contains(":")) {
                url = null;
                name = url0.split(":")[0];
                path = url0.split(":")[1];
            } else {
                url = null;
                name = null;
                path = url0;
            }
        }

        if( url == null && config.getUpstream() == null){
            throw new FairyException("FairyClient config on upstream");
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] vals) throws Throwable {
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
        if (client_headers != null) {
            for (String h : client_headers) {
                String[] ss = h.split("=");
                headers.put(ss[0], ss[1]);
            }
        }

        String url2 = null;
        if (url == null) {
            url2 = config.getUpstream().getServer();

            if (url2 == null) {
                throw new RuntimeException("Solon client proxy: Not found upstream!");
            }

            if (path != null) {
                int idx = url2.indexOf("/", 9);//https://a
                if (idx > 0) {
                    url2 = url2.substring(0, idx);
                }

                if (path.endsWith("/")) {
                    fun = path + fun;
                } else {
                    fun = path + "/" + fun;
                }
            }

        } else {
            url2 = url;
        }

        //执行调用
        return new Fairy(config)
                .url(url2, fun)
                .call(headers, args)
                .getObject(method.getReturnType());
    }


    private static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }
}
