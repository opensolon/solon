package org.noear.solon;

import org.noear.solon.core.*;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 内部专用工具
 * */
public class XUtil {
    public static ExecutorService commonPool = Executors.newCachedThreadPool();
    private static final char[] _hexDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * 生成UGID
     */
    public static String guid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 生成MD5
     * */
    public static String md5(String str) {
        try {
            byte[] btInput = str.getBytes("UTF-8");

            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            int j = md.length;
            char[] chars = new char[j * 2];
            int k = 0;

            for (int i = 0; i < j; ++i) {
                byte byte0 = md[i];
                chars[k++] = _hexDigits[byte0 >>> 4 & 15];
                chars[k++] = _hexDigits[byte0 & 15];
            }

            return new String(chars);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void throwTr(Throwable ex){
        if(ex instanceof RuntimeException){
            throw (RuntimeException)ex;
        }else {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 检查字符串是否为空
     */
    public static boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }

    /**
     * 检查字符串是否为非空
     */
    public static boolean isNotEmpty(String s) {
        return !isEmpty(s);
    }

    /**
     * 检查字符串是否为空白
     */
    public static boolean isBlank(String s) {
        if (isEmpty(s)) {
            return true;
        } else {
            for (int i = 0, l = s.length(); i < l; ++i) {
                if (!isWhitespace(s.codePointAt(i))) {
                    return false;
                }
            }

            return true;
        }
    }

    public static boolean isWhitespace(int c) {
        return c == 32 || c == 9 || c == 10 || c == 12 || c == 13;
    }

    /**
     * 获取第一项或者null
     */
    public static <T> T firstOrNull(List<T> list) {
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    /**
     * 根据字符串加载为一个类
     */
    public static Class<?> loadClass(String className) {
        try {
            return XClassLoader.global().loadClass(className); //Class.forName(className);
        } catch (Throwable ex) {
            return null;
        }
    }

    /**
     * 根据字段串加载为一个对象
     */
    public static <T> T newInstance(String className) {
        try {
            Class<?> clz = loadClass(className);
            if (clz == null) {
                return null;
            } else {
                return (T)clz.newInstance();
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * 获取资源URL集
     */
    public static Enumeration<URL> getResources(String name) throws IOException {
        Enumeration<URL> urls = XClassLoader.global().getResources(name); //XUtil.class.getClassLoader().getResources(name);
        if (urls == null || urls.hasMoreElements() == false) {
            urls = ClassLoader.getSystemResources(name);
        }

        return urls;
    }

    /**
     * 获取资源URL
     */
    public static URL getResource(String name) {
        URL url = XClassLoader.global().getResource(name);//XUtil.class.getResource(name);

        if (url == null) {
            url = XUtil.class.getResource(name);
        }

        return url;
    }

    /**
     * 根据url加载配置集
     * */
    public static Properties getProperties(URL url) {
        if(url == null){
            return null;
        }

        try {
            return XPropertiesLoader.global.load(url);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 根据txt加载配置集
     * */
    public static Properties getProperties(String txt) {
        try {
            return XPropertiesLoader.global.load(txt);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 获取异常的完整内容
     */
    public static String getFullStackTrace(Throwable ex) {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw, true));
        return sw.getBuffer().toString();
    }

    /**
     * 合并两个路径
     */
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

        if (XUtil.isEmpty(path2)) {
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

    /**
     * 将路径根据表达式转成map
     * */
    public static XMap pathVarMap(String path, String expr) {
        XMap _map = new XMap();

        //支持path变量
        if (expr.indexOf("{") >= 0) {
            String path2 = null;
            try {
                path2 = URLDecoder.decode(path, "utf-8");
            } catch (Throwable ex) {
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

    /**
     * 构建应用扩展目录
     * */
    public static String buildExt(String ext_dir, boolean autoCreate) {
        URL temp = XUtil.getResource("application.properties");
        if (temp == null) {
            temp = XUtil.getResource("application.yml");
        }

        if (temp == null) {
            return null;
        } else {
            String uri = temp.toString();
            if (uri.startsWith("file:/")) {
                uri = uri.substring(5, uri.length() - 30);
            } else {
                int idx = uri.indexOf("jar!/");
                idx = uri.lastIndexOf("/", idx) + 1;

                uri = uri.substring(9, idx);
            }

            uri = uri + ext_dir + "/";
            File dir = new File(uri);

            if (dir.exists() == false) {
                if (autoCreate) {
                    dir.mkdir();
                } else {
                    return null;
                }
            }

            return uri;
        }
    }

    /**
     * 将 source:Map 数据，绑定到 target:bean
     * */
    public static void bindTo(Map<String,String> source, Object target) {
        bindTo((k) -> source.get(k), target);
    }

    /**
     * 将 source:Properties 数据，绑定到 target:bean
     * */
    public static void bindTo(Properties source, Object target) {
        bindTo((k) -> source.getProperty(k), target);
    }

    /**
     * 将 source:((k)->v) 数据，绑定到 target:bean
     * */
    public static void bindTo(Function<String, String> source, Object target) {
        if (target == null) {
            return;
        }

        ClassWrap.get(target.getClass()).fill(target,source,null);
    }
}
