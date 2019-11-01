package org.noear.solon;

import org.noear.solon.core.PathAnalyzer;
import org.noear.solon.core.XMap;
import org.noear.solon.core.XPropertiesLoader;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 内部专用工具
 * */
public class XUtil {
    /** 生成UGID*/
    public static String guid(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    /** 检查字符串是否为空*/
    public static boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }

    /** 获取第一项或者null */
    public static <T> T firstOrNull(List<T> list){
        if(list.size()>0){
            return list.get(0);
        }else{
            return null;
        }
    }

    /** 根据字符串加载为一个类*/
    public static Class<?> loadClass(String className) {
        try {
            return Class.forName(className);
        }catch (Exception ex) {
            return null;
        }
    }

    /** 根据字段串加载为一个对象 */
    public static <T> T newClass(String className) {
        try {
            Class clz = Class.forName(className);
            return (T) clz.newInstance();
        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    /** 获取资源URL集*/
    public static Enumeration<URL> getResources(String name) throws IOException{
        Enumeration<URL> urls = XUtil.class.getClassLoader().getResources(name);
        if (urls == null || urls.hasMoreElements()==false) {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            if (loader != null) {
                urls = loader.getResources(name);
            } else {
                urls = ClassLoader.getSystemResources(name);
            }
        }

        return urls;
    }

    /** 获取资源URL*/
    public static URL getResource(String name) {
        URL url = XUtil.class.getResource(name);
        if (url == null) {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            if (loader != null) {
                url = loader.getResource(name);
            } else {
                url = ClassLoader.getSystemResource(name);
            }
        }

        return url;
    }

    public static Properties getProperties(URL url){
        try {
            return XPropertiesLoader.global.load(url);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /** 获取异常的完整内容*/
    public static String getFullStackTrace(Throwable ex){
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw, true));
        return sw.getBuffer().toString();
    }

    /** 合并两个路径*/
    public static String mergePath(String path1, String path2) {
        if (XUtil.isEmpty(path1)) {
            if (path2.startsWith("/")) {
                return path2;
            } else {
                return "/" + path2;
            }
        }

        if (path1.startsWith("/") == false) {
            path1 = "/" + path1;
        }

        if(XUtil.isEmpty(path2)){
            return path1;
        }

        if (path2.startsWith("/")) {
            path2 = path2.substring(1);
        }

        if (path1.endsWith("/")) {
            return path1 + path2;
        } else {
            if (path1.endsWith("*")) {
                int idx = path1.lastIndexOf('/') + 1;
                if (idx < 1) {
                    return path2;
                } else {
                    return path1.substring(0, idx) + path2;
                }
            } else {
                return path1 + "/" + path2;
            }
        }
    }

    private static Pattern _pkr = Pattern.compile("\\{([^\\\\}]+)\\}");

    public static XMap pathVarMap(String path, String expr) {
        XMap _map = new XMap();

        //支持path变量
        if (expr.indexOf("{") >= 0) {
            String path2 = null;
            try{
                path2 =URLDecoder.decode(path, "utf-8");
            }catch (Exception ex){
                path2 = path;
            }

            Matcher pm = _pkr.matcher(expr);

            List<String> _pks = new ArrayList<>();

            while (pm.find()) {
                _pks.add(pm.group(1));
            }

            if (_pks.size() > 0) {
                PathAnalyzer _pr = new PathAnalyzer(expr);
                //Pattern _pr = Pattern.compile(XUtil.expCompile(expr), Pattern.CASE_INSENSITIVE);

                pm = _pr.matcher(path2);
                if (pm.find()) {
                    for (int i = 0, len = _pks.size(); i < len; i++) {
                        _map.put(_pks.get(i), pm.group(i + 1));//不采用group name,可解决_的问题
                    }
                }
            }
        }

        return _map;
    }
}
