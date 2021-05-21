package org.noear.solon;

import org.noear.solon.annotation.Note;
import org.noear.solon.core.wrap.ClassWrap;
import org.noear.solon.core.*;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.FileNameMap;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Function;

/**
 * 内部专用工具（外部项目不建议使用，随时可能会变动）
 *
 * @author noear
 * @since 1.0
 * */
@Note("内部专用工具（外部项目不建议使用，随时可能会变动）")
public class Utils {
    public static final FileNameMap mimeMap = URLConnection.getFileNameMap();
    public static final ExecutorService pools = Executors.newCachedThreadPool();
    public static final ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

    private static final char[] HEX_DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String mime(String fileName){
        return mimeMap.getContentTypeFor(fileName);
    }

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
                chars[k++] = HEX_DIGITS[byte0 >>> 4 & 15];
                chars[k++] = HEX_DIGITS[byte0 & 15];
            }

            return new String(chars);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 获取异常打印信息
     * */
    public static String throwableToString(Throwable ex){
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));

        return sw.toString();
    }

    /**
     * 包装异常
     * */
    public static RuntimeException throwableWrap(Throwable ex){
        if(ex instanceof RuntimeException){
            return  (RuntimeException)ex;
        }else {
            return new RuntimeException(ex);
        }
    }

    /**
     * 解包异常
     * */
    public static Throwable throwableUnwrap(Throwable ex) {
        Throwable th = ex;

        while (true) {
            if (th instanceof InvocationTargetException) {
                th = ((InvocationTargetException) th).getTargetException();
            } else if (th instanceof UndeclaredThrowableException) {
                th = ((UndeclaredThrowableException) th).getUndeclaredThrowable();
            } else if (th.getClass() == RuntimeException.class) {
                if (th.getMessage() == null && th.getCause() != null) {
                    th = th.getCause();
                } else {
                    break;
                }
            } else {
                break;
            }
        }

        return th;
    }

    public static boolean throwableHas(Throwable ex, Class<? extends Throwable> clz) {
        Throwable th = ex;

        while (true) {
            if (clz.isAssignableFrom(th.getClass())) {
                return true;
            }

            if (th instanceof InvocationTargetException) {
                th = ((InvocationTargetException) th).getTargetException();
            } else if (th instanceof UndeclaredThrowableException) {
                th = ((UndeclaredThrowableException) th).getUndeclaredThrowable();
            } else if (th.getCause() != null) {
                th = th.getCause();
            } else {
                break;
            }
        }

        return false;
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
            return loadClass(null, className); //Class.forName(className);
        } catch (Throwable ex) {
            return null;
        }
    }

    /**
     * 根据字符串加载为一个类
     */
    public static Class<?> loadClass(ClassLoader classLoader, String className) {
        try {
            if (classLoader == null) {
                return Class.forName(className);
            } else {
                return classLoader.loadClass(className);
            }
        } catch (Throwable ex) {
            return null;
        }
    }

    /**
     * 根据字段串加载为一个对象
     */
    public static <T> T newInstance(String className) {
        return newInstance(JarClassLoader.global(), className);
    }

    /**
     * 根据字段串加载为一个对象
     */
    public static <T> T newInstance(ClassLoader classLoader,String className) {
        try {
            Class<?> clz = loadClass(classLoader, className);
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
     *
     * @param name 资源名称
     */
    public static Enumeration<URL> getResources(String name) throws IOException {
        return getResources(JarClassLoader.global(), name); //XUtil.class.getClassLoader().getResources(name);
    }

    public static Enumeration<URL> getResources(ClassLoader classLoader, String name) throws IOException {
        return classLoader.getResources(name); //XUtil.class.getClassLoader().getResources(name);
    }

    /**
     * 获取资源URL
     *
     * @param name 资源名称
     */
    public static URL getResource(String name) {
        return getResource(JarClassLoader.global(), name); //XUtil.class.getResource(name);
    }

    public static URL getResource(ClassLoader classLoader, String name) {
        return classLoader.getResource(name); //XUtil.class.getResource(name);
    }

    /**
     * 获取资源并转为String
     *
     * @param name 资源名称
     * @param charset 编码
     * */
    public static String getResourceAsString(String name, String charset) {
        return getResourceAsString(JarClassLoader.global(), name, charset);
    }

    public static String getResourceAsString(ClassLoader classLoader, String name, String charset) {
        URL url = getResource(classLoader, name);
        if (url != null) {
            try {
                return getString(url.openStream(), charset);
            } catch (Exception ex) {
                throw throwableWrap(ex);
            }
        } else {
            return null;
        }
    }

    public static String getString(InputStream ins, String charset) {
        if (ins == null) {
            return null;
        }

        try {
            ByteArrayOutputStream outs = transfer(ins, new ByteArrayOutputStream());

            if (charset == null) {
                return outs.toString();
            } else {
                return outs.toString(charset);
            }
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    public static <T extends OutputStream>  T transfer(InputStream ins, T out) throws IOException {
        if (ins == null || out == null) {
            return null;
        }

        int len = 0;
        byte[] buf = new byte[512];
        while ((len = ins.read(buf)) != -1) {
            out.write(buf, 0, len);
        }

        return out;
    }


    /**
     * 根据url加载配置集
     * */
    public static Properties loadProperties(URL url) {
        if(url == null){
            return null;
        }

        try {
            return PropsLoader.global().load(url);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 根据本地资源url加载配置集
     * */
    public static Properties loadProperties(String url) {
        return loadProperties(getResource(url));
    }

    /**
     * 根据txt加载配置集
     * */
    public static Properties buildProperties(String txt) {
        try {
            return PropsLoader.global().build(txt);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 注入属性
     * */
    public static <T> T injectProperties(T bean, Properties propS) {
        ClassWrap.get(bean.getClass()).fill(bean, propS::getProperty);
        return bean;
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
     * 构建应用扩展目录
     *
     * @param extend 扩展配置
     * @param autoMake 是否自动创建
     * */
    public static String buildExt(String extend, boolean autoMake) {
        if (extend == null) {
            return null;
        }

        if (extend.contains("/")) {
            //如果全路径，直接返回
            return extend;
        }

        URL temp = Utils.getResource("");

        if (temp == null) {
            return null;
        } else {
            String uri = temp.toString();
            if (uri.startsWith("file:/")) {
                int idx = uri.lastIndexOf("/target/");
                if (idx > 0) {
                    idx = idx + 8;
                } else {
                    idx = uri.lastIndexOf("/", idx) + 1;
                }

                uri = uri.substring(5, idx);
            } else {
                int idx = uri.indexOf("jar!/");
                idx = uri.lastIndexOf("/", idx) + 1;

                uri = uri.substring(9, idx);
            }

            uri = uri + extend + "/";
            File dir = new File(uri);

            if (dir.exists() == false) {
                if (autoMake) {
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

    /**
     * 获取当前线程的ClassLoader
     * */
    public static ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * 获取ClassLoader
     * */
    public static ClassLoader getClassLoader() {
        ClassLoader classLoader = getContextClassLoader();
        if (classLoader == null) {
            classLoader = Utils.class.getClassLoader();
            if (null == classLoader) {
                classLoader = ClassLoader.getSystemClassLoader();
            }
        }

        return classLoader;
    }
}
