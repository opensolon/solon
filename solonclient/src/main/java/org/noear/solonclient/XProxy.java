package org.noear.solonclient;

import org.noear.solonclient.annotation.XAlias;
import org.noear.solonclient.annotation.XClient;
import org.noear.solonclient.serializer.FastjsonSerializer;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class XProxy {

    protected String _url;
    private String _result;
    protected ISerializer _serializer = FastjsonSerializer.instance;
    protected IChannel _channel = ChannelHttp.instance;
    protected Enctype _enctype = Enctype.form_data;

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
    public XProxy call(Map<String, String> headers, Map<String, String> args) {
        try {
            _result = _channel.call(this, headers, args);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return this;
    }

    /**
     * 获取结果（以string形式）
     */
    public String getString() {
        return _result;
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

    /**
     * 设置服务端
     */
    public XProxy sev(String sev) {
        _sev = sev;
        return this;
    }


    /**
     * 创建接口调用代理客户端
     */
    public <T> T create(Class<?> clz) {
        return (T) Proxy.newProxyInstance(
                clz.getClassLoader(),
                new Class[]{clz},
                (proxy, method, args) -> proxy_call(proxy, method, args));
    }

    /**
     * 执行调用代理
     */
    private Object proxy_call(Object proxy, Method method, Object[] vals) throws Throwable {
        //调用准备
        String fun = method.getName();

        XClient c_meta = method.getDeclaringClass().getAnnotation(XClient.class);
        XAlias m_meta = method.getAnnotation(XAlias.class);

        if (c_meta == null) {
            return null;
        }

        if (_sev == null) {
            //1.优先从 XClient 获取服务地址或名称
            if (isEmpty(c_meta.value()) == false) {
                _sev = c_meta.value();
            }

            //2.如果没有，就报错
            if (_sev == null) {
                throw new RuntimeException("XClient no name");
            }
        }

        if (m_meta != null && isEmpty(m_meta.value()) == false) {
            fun = m_meta.value();
        }

        //构建args
        Map<String, String> args = new HashMap<>();
        Parameter[] names = method.getParameters();
        for (int i = 0, len = names.length; i < len; i++) {
            if (vals[i] != null) {
                args.put(names[i].getName(), vals[i].toString());
            }
        }

        //构建headers
        Map<String, String> headers = new HashMap<>();
        //>>添加全局header
        headers.putAll(_headers);
        //>>添加接口header
        if (c_meta.headers() != null) {
            for (String h : c_meta.headers()) {
                String[] ss = h.split("=");
                headers.put(ss[0], ss[1]);
            }
        }

        String url = null;
        if (_sev.indexOf("://") < 0) {
            String _pat = null;
            if (_sev.indexOf(":") > 0) {
                String[] ss = _sev.split(":");

                _sev = ss[0];
                _pat = ss[1];
            }

            url = _upstream.getTarget(_sev);

            if (_pat != null) {
                int idx = url.indexOf("/", 9);//https://a
                if (idx > 0) {
                    url = url.substring(0, idx);
                }

                if (_pat.endsWith("/")) {
                    fun = _pat + fun;
                } else {
                    fun = _pat + "/" + fun;
                }
            }

        } else {
            url = _sev;
        }

        //执行调用
        return new XProxy()
                .url(url, fun)
                .enctype(_enctype)
                .channel(_channel)
                .call(headers, args)
                .serializer(_serializer)
                .getObject(method.getReturnType());
    }

    /**
     * 设置负载代理
     */
    public XProxy upstream(HttpUpstream upstream) {
        _upstream = upstream;
        return this;
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

    /**
     * 设置编码类型
     */
    public XProxy enctype(Enctype enctype) {
        _enctype = enctype;
        return this;
    }


    private static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }
}
