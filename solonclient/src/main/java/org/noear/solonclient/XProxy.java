package org.noear.solonclient;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * 代理器
 * */
public class XProxy {
    /**
     * 默认的通讯通道（涉及第三方框架引用，不做定义）
     */
    public static IChannel defaultChannel;
    /**
     * 默认的序列化器（涉及第三方框架引用，不做定义）
     */
    public static ISerializer defaultSerializer;

    /**
     * 默认的反序列化器（涉及第三方框架引用，不做定义）
     */
    public static IDeserializer defaultDeserializer;

    private String _url;
    private final XProxyConfig _config;


    public XProxyConfig config() {
        return _config;
    }


    protected XProxy(XProxyConfig config) {
        _config = config;
    }

    public XProxy() {
        _config = new XProxyConfig();

        _config.setSerializer(defaultSerializer);
        _config.setDeserializer(defaultDeserializer);

        _config.setChannel(defaultChannel);
    }

    /**
     * 设置请求地址
     */
    public XProxy url(String url) {
        _url = url;
        return this;
    }

    /**
     * 设置请求地址
     */
    public XProxy url(String url, String fun) {
        if (url.indexOf("{fun}") > 0) {
            _url = url.replace("{fun}", fun);
        } else {
            if (fun == null) {
                _url = url;
            } else {
                StringBuilder sb = new StringBuilder(200);

                sb.append(url);
                if (url.endsWith("/")) {
                    if (fun.startsWith("/")) {
                        sb.append(fun.substring(1));
                    } else {
                        sb.append(fun);
                    }
                } else {
                    if (fun.startsWith("/")) {
                        sb.append(fun);
                    } else {
                        sb.append("/").append(fun);
                    }
                }

                _url = sb.toString();
            }
        }
        return this;
    }

    /**
     * 执行完成呼叫
     */
    public XProxy call(Map<String, String> headers, Map args) {
        try {
            _result = _config.getChannel().call(_config, _url, headers, args);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Throwable ex) {
            if (ex instanceof InvocationTargetException) {
                ex = ((InvocationTargetException) ex).getTargetException();
            }

            if (ex instanceof RuntimeException) {
                throw (RuntimeException) ex;
            } else {
                throw new RuntimeException(ex);
            }
        }

        return this;
    }

    private Result _result;

    public Result result() {
        return _result;
    }

    /**
     * 获取结果（以string形式）
     */
    public String getString() {
        return _result.bodyAsString();
    }

    /**
     * 获取结果（返序列化为object）
     */
    public <T> T getObject(Class<T> returnType) {
        return _config.getDeserializer().deserialize(_result, returnType);
    }

    //////////////////////////////////
    //
    // 下面为动态代理部分
    //
    //////////////////////////////////


    /**
     * 设置全局头信息
     */
    public XProxy headerAdd(String name, String value) {
        _config.headerAdd(name, value);
        return this;
    }

    /**
     * 设置服务端
     */
    public XProxy server(String server) {
        _config.setServer(server);
        return this;
    }


    /**
     * 创建接口调用代理客户端
     */
    public <T> T create(Class<?> clz) {
        XProxyHandler handler = new XProxyHandler(this);

        return (T) Proxy.newProxyInstance(
                clz.getClassLoader(),
                new Class[]{clz},
                handler);
    }

    /**
     * 设置负载代理
     */
    public XProxy upstream(XUpstream upstream) {
        _config.setUpstream(upstream);
        return this;
    }

    /**
     * 设置序列化器
     */
    public XProxy serializer(ISerializer serializer) {
        _config.setSerializer(serializer);
        return this;
    }

    /**
     * 设置反序列器
     */
    public XProxy deserializer(IDeserializer deserializer) {
        _config.setDeserializer(deserializer);
        return this;
    }


    /**
     * 设置通信通道
     */
    public XProxy channel(IChannel channel) {
        _config.setChannel(channel);
        return this;
    }
}
