package org.noear.solonclient;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.ParserConfig;
import org.noear.solonclient.annotation.XAlias;
import org.noear.solonclient.annotation.XClient;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class XProxy {
    static {
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
    }

    private  String _url;
    private String _result;

    public XProxy url(String url){
        _url = url;
        return this;
    }

    public XProxy url(String url, String fun){
        if (url.indexOf("{fun}") > 0) {
            _url = url.replace("{fun}", fun);
        } else {
            if(fun == null){
                _url = url;
            }else {
                if (url.endsWith("/")) {
                    _url = url + fun;
                } else {
                    _url = url + "/" + fun;
                }
            }
        }
        return this;
    }

    /** 执行完成呼叫 */
    public XProxy call(Map<String,String> headers, Map<String, String> args) {
        try {
            _result = HttpUtil.postString(_url, args, headers);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
        return this;
    }

    public String getString(){
        return _result;
    }

    public   <T> T getObject(Class<T> returnType) {
        Object returnVal = null;
        try {
            if (_result == null) {
                return (T) _result;
            }
            returnVal = JSONObject.parseObject(_result, new TypeReference<T>() {});

        }catch (Exception ex){
            System.out.println(_result);
            returnVal = ex;
        }

        if (returnVal != null && Throwable.class.isAssignableFrom(returnVal.getClass())) {
            throw new RuntimeException((Throwable)returnVal);
        } else {
            return (T)returnVal;
        }
    }

    //////////////////////////////////
    //
    // 下面为动态代理部分
    //
    //////////////////////////////////
    private XProxy.Fun1<String,String> _upstream;
    private String _sev;

    public XProxy sev(String sev){
        _sev = sev;
        return this;
    }

    /** 创建接口调用代理客户端 */
    public  <T> T create(Class<?> clz ) {
        return (T) Proxy.newProxyInstance(
                clz.getClassLoader(),
                new Class[]{clz},
                (proxy, method, args) -> proxy_call(proxy, method, args));
    }

    /** 执行调用代理 */
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
        if (c_meta.headers() != null) {
            for (String h : c_meta.headers()) {
                String[] ss = h.split("=");
                headers.put(ss[0], ss[1]);
            }
        }

        String url = null;
        if (_sev.indexOf("://") < 0) {
            url = _upstream.run(_sev);
        } else {
            url = _sev;
        }

        //执行调用
        return new XProxy()
                .url(url, fun)
                .call(headers, args)
                .getObject(method.getReturnType());
    }

    public XProxy upstream(XProxy.Fun1<String,String> upstream){
        _upstream = upstream;
        return this;
    }


    private static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public interface Fun1<R,T>{
        R run(R r);
    }
}
