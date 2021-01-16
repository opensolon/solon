package org.noear.nami;

import org.noear.nami.annotation.Mapping;
import org.noear.nami.annotation.NamiClient;
import org.noear.nami.common.Constants;
import org.noear.nami.common.MethodWrap;
import org.noear.nami.common.UpstreamFixed;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
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
    private static Pattern pathKeyExpr = Pattern.compile("\\{([^\\\\}]+)\\}");

    private final NamiConfig config;

    private final Map<String, String> headers0 = new LinkedHashMap<>();
    private final String name0; //upstream name
    private final String path0; //path
    private final String url0;  //url
    private final Class<?> clz0;
    private final Map<String, Map> pathKeysCached = new ConcurrentHashMap<>();

    /**
     * @param config 配置
     * @param client 客户端注解
     */
    public NamiHandler(Class<?> clz, NamiConfig config, NamiClient client) {
        this.config = config;

        this.clz0 = clz;

        //1.运行配置器
        if (client != null) {
            try {
                NamiConfiguration tmp = client.configuration().newInstance();

                if (tmp != null) {
                    tmp.config(client, new Nami.Builder(config));
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

            //>>添加接口header
            if (client.headers().length > 0) {
                for (String h : client.headers()) {
                    String[] ss = h.split("=");
                    if (ss.length == 2) {
                        headers0.put(ss[0].trim(), ss[1].trim());
                    }
                }
            }

            //>>添加upstream
            if (client.upstream().length > 0) {
                config.setUpstream(new UpstreamFixed(Arrays.asList(client.upstream())));
            }
        }

        //2.配置初始化
        config.tryInit();

        //3.获取 or url
        String uri = config.getUri();

        if (uri == null && client != null) {
            //1.优先从 XClient 获取服务地址或名称
            if (isEmpty(client.value()) == false) {
                uri = client.value();
            }
        }

        //2.如果没有，就报错
        if (uri == null) {
            uri = "";
            //throw new NamiException("NamiClient config is wrong: " + clz.getName());
        }

        if (uri.contains("://")) {
            url0 = uri;
            name0 = null;
            path0 = null;
        } else {
            if (uri.contains(":")) {
                url0 = null;
                name0 = uri.split(":")[0];
                path0 = uri.split(":")[1];
            } else {
                url0 = null;
                name0 = uri;
                path0 = null;
            }
        }
    }


    protected MethodHandles.Lookup lookup;

    @Override
    public Object invoke(Object proxy, Method method, Object[] vals) throws Throwable {
        if (url0 == null && config.getUpstream() == null) {
            throw new NamiException("NamiClient: Not found upstream: " + clz0.getName());
        }

        MethodWrap methodWrap = MethodWrap.get(method);

        //Object 函数调用
        Class caller = method.getDeclaringClass();
        if (Object.class == caller) {
            if (this.lookup == null) {
                Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, Integer.TYPE);
                constructor.setAccessible(true);
                this.lookup = constructor.newInstance(caller, MethodHandles.Lookup.PRIVATE);
            }

            return this.lookup.unreflectSpecial(method, caller).bindTo(proxy).invokeWithArguments(vals);
        }

        //构建 headers
        Map<String, String> headers = new HashMap<>(headers0);

        //构建 args
        Map<String, Object> args = new LinkedHashMap<>();
        Object body = null;
        Parameter[] names = methodWrap.getParameters();
        for (int i = 0, len = names.length; i < len; i++) {
            if (vals[i] != null) {
                args.put(names[i].getName(), vals[i]);
            }
        }

        //确定body及默认编码
        if(methodWrap.getBodyName() != null){
            body = args.get(methodWrap.getBodyName());

            if (config.getEncoder() == null) {
                headers.putIfAbsent(Constants.h_content_type, methodWrap.getBodyAnno().contentType());
            }
        }

        //构建 fun
        String fun = method.getName();
        String act = null;

        //处理mapping
        Mapping mapping = methodWrap.getMappingAnno();
        if (mapping != null) {
            if(methodWrap.getAct() != null){
                act = methodWrap.getAct();
            }

            if(methodWrap.getFun() != null){
                fun = methodWrap.getFun();
            }

            if (methodWrap.getMappingHeaders() != null) {
                headers.putAll(methodWrap.getMappingHeaders());
            }
        }


        //构建 url
        String url = null;
        if (url0 == null) {
            url = config.getUpstream().get();

            if (url == null) {
                throw new NamiException("NamiClient: Not found upstream!");
            }

            if (url.indexOf("://") < 0) {
                url = "http://";
            }

            if (path0 != null) {
                int idx = url.indexOf("/", 9);//https://a
                if (idx > 0) {
                    url = url.substring(0, idx);
                }

                if (path0.endsWith("/")) {
                    fun = path0 + fun;
                } else {
                    fun = path0 + "/" + fun;
                }
            }

        } else {
            url = url0;
        }

        if (fun != null && fun.indexOf("{") > 0) {
            //
            //处理Path参数
            //
            Map<String, String> pathKeys = buildPathKeys(fun);

            for (Map.Entry<String, String> kv : pathKeys.entrySet()) {
                String val = (String) args.get(kv.getValue());

                if (val != null) {
                    fun = fun.replace(kv.getKey(), val);
                    args.remove(kv.getValue());
                }
            }
        }

        //确定返回类型
        Type type = method.getGenericReturnType();
        if (type == null) {
            type = method.getReturnType();
        }


        //执行调用
        return new Nami(config)
                .method(method)
                .action(act)
                .url(url, fun)
                .call(headers, args, body)
                .getObject(type);
    }


    private static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    private Map<String, String> buildPathKeys(String path) {
        Map<String, String> pathKeys = pathKeysCached.get(path);
        if (pathKeys == null) {
            synchronized (path.intern()) {
                pathKeys = pathKeysCached.get(path);
                if (pathKeys == null) {
                    pathKeys = new LinkedHashMap<>();

                    Matcher pm = pathKeyExpr.matcher(path);

                    while (pm.find()) {
                        pathKeys.put(pm.group(), pm.group(1));
                    }
                }
            }
        }

        return pathKeys;
    }
}
