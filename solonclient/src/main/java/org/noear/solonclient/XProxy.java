package org.noear.solonclient;

import org.noear.solonclient.annotation.XAlias;
import org.noear.solonclient.annotation.XClient;
import org.noear.solonclient.channel.HttpChannel;
import org.noear.solonclient.serializer.FastjsonSerializer;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.util.HashMap;
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

    private String _url;
    private Result _result;
    private ISerializer _serializer;
    private IChannel _channel;
    private Enctype _enctype;



    public XProxy() {
        _serializer = (defaultSerializer != null ? defaultSerializer : FastjsonSerializer.instance);
        _channel = (defaultChannel != null ? defaultChannel : HttpChannel.instance);
        _enctype = defaultEnctype;
    }

    public XProxy(ISerializer serializer) {
        this();
        _serializer = serializer;
    }

    public XProxy(ISerializer serializer, IChannel channel) {
        this();
        _serializer = serializer;
        _channel = channel;
    }

    public String url(){return _url;}
    public ISerializer serializer(){return _serializer;}
    public Enctype enctype(){return _enctype;}

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
            _result = _channel.call(this, headers, args);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return this;
    }

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
        return _serializer.deserialize(_result, returnType);
    }

    //////////////////////////////////
    //
    // 下面为动态代理部分
    //
    //////////////////////////////////
    private HttpUpstream _upstream;
    private String _sev;
    private Map<String, String> _headers = new HashMap<>();

    /**
     * 设置全局头信息
     */
    public XProxy headerAdd(String name, String value) {
        _headers.put(name, value);
        return this;
    }

    public Map<String,String> headers(){
        return _headers;
    }

    /**
     * 设置服务端
     */
    public XProxy sev(String sev) {
        _sev = sev;
        return this;
    }

    public String sev(){
        return _sev;
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
        _upstream = upstream;
        return this;
    }

    public HttpUpstream upstream(){
        return _upstream;
    }

    /**
     * 设置序列化器
     */
    public XProxy serializer(ISerializer serializer) {
        _serializer = serializer;
        return this;
    }


    /**
     * 设置通信通道
     */
    public XProxy channel(IChannel channel) {
        _channel = channel;
        return this;
    }

    public IChannel channel(){
        return _channel;
    }

    /**
     * 设置编码类型
     */
    public XProxy enctype(Enctype enctype) {
        _enctype = enctype;
        return this;
    }


}
