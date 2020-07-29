package org.noear.solonclient;

import org.noear.solonclient.annotation.XAlias;
import org.noear.solonclient.annotation.XClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class XProxyHandler implements InvocationHandler {
    private XProxyConfig _cfg;
    private String _sev;
    public XProxyHandler(XProxy proxy){
        _cfg = proxy.config();
        _sev =_cfg.getServer();

    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] vals) throws Throwable {
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
        Map<String, Object> args = new LinkedHashMap<>();
        Parameter[] names = method.getParameters();
        for (int i = 0, len = names.length; i < len; i++) {
            if (vals[i] != null) {
                args.put(names[i].getName(), vals[i]);
            }
        }

        //构建headers
        Map<String, String> headers = new HashMap<>();
        //>>添加全局header
        headers.putAll(_cfg.getHeaders());
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

            url = _cfg.getUpstream().getTarget(_sev);

            if(url == null){
                throw new RuntimeException("Solon client proxy: Not found upstream!");
            }

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
        return new XProxy(_cfg)
                .url(url, fun)
                .call(headers, args)
                .getObject(method.getReturnType());
    }


    private static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }
}
