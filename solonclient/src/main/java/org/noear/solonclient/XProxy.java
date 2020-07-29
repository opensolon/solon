package org.noear.solonclient;

import org.noear.solonclient.channel.HttpChannel;
import org.noear.solonclient.serializer.FastjsonSerializer;

import java.lang.reflect.Proxy;
import java.util.Map;

public class XProxy {

    /**
     * 默认的编码类型
     * */
    public static Enctype defaultEnctype = Enctype.form_data;
    /**
     * 默认的通讯通道（涉及第三方框架引用，不做定义）
     * */
    public static IChannel defaultChannel;
    /**
     * 默认的序列化器（涉及第三方框架引用，不做定义）
     * */
    public static ISerializer defaultSerializer;

    /**
     * 默认的反序列化器（涉及第三方框架引用，不做定义）
     * */
    public static IDeserializer defaultDeserializer;

    private String _url;
    private final XProxyConfig _config;



    public XProxyConfig config(){
        return _config;
    }


    protected XProxy(XProxyConfig config){
        _config = config;
    }

    public XProxy() {
        _config = new XProxyConfig();

        _config.serializer = (defaultSerializer != null ? defaultSerializer : FastjsonSerializer.instance);
        _config.deserializer = (defaultDeserializer != null ? defaultDeserializer : FastjsonSerializer.instance);

        _config.channel = (defaultChannel != null ? defaultChannel : HttpChannel.instance);
        _config.enctype = defaultEnctype;
    }

    public String url(){
        return _url;
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
            _result = _config.channel.call(this, headers, args);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return this;
    }

    private Result _result;
    public Result result(){
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
        return _config.deserializer.deserialize(_result, returnType);
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
        _config.headers.put(name, value);
        return this;
    }
    /**
     * 设置服务端
     */
    public XProxy sev(String sev) {
        _config.sev = sev;
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
    public XProxy upstream(HttpUpstream upstream) {
        _config.upstream = upstream;
        return this;
    }

    /**
     * 设置序列化器
     */
    public XProxy serializer(ISerializer serializer) {
        _config.serializer = serializer;
        return this;
    }

    /**
     * 设置反序列器
     * */
    public XProxy deserializer(IDeserializer deserializer) {
        _config.deserializer = deserializer;
        return this;
    }


    /**
     * 设置通信通道
     */
    public XProxy channel(IChannel channel) {
        _config.channel = channel;
        return this;
    }

    /**
     * 设置编码类型
     */
    public XProxy enctype(Enctype enctype) {
        _config.enctype = enctype;
        return this;
    }
}
