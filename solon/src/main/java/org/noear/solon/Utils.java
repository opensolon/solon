package org.noear.solon;

import org.noear.solon.annotation.Note;
import org.noear.solon.core.util.LogUtil;
import org.noear.solon.core.wrap.ClassWrap;
import org.noear.solon.core.*;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.*;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;

/**
 * 内部专用工具（外部项目不建议使用，随时可能会变动）
 *
 * @author noear
 * @since 1.0
 * */
@Note("内部专用工具（外部项目不建议使用，随时可能会变动）")
public class Utils {
    private static final FileNameMap mimeMap = URLConnection.getFileNameMap();
    /**
     * @deprecated 1.10
     * */
    @Deprecated
    public static final ExecutorService pools = Executors.newCachedThreadPool();
    public static final ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

    private static final char[] HEX_DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * 异步执行
     * */
    public static Future<?> async(Runnable task){
        return pools.submit(task);
    }

    /**
     * 异步执行
     * */
    public static <T> Future<T> async(Callable<T> task){
        return pools.submit(task);
    }

    /**
     * Ping 一个地址
     *
     * @param address （例：192.168.1.1 或 192.168.1.1:8080）
     */
    public static boolean ping(String address) throws Exception {
        if (address.contains(":")) {
            String host = address.split(":")[0];
            int port = Integer.parseInt(address.split(":")[1]);

            try (Socket socket = new Socket()) {
                SocketAddress addr = new InetSocketAddress(host, port);
                socket.connect(addr, 3000);
                return true;
            } catch (IOException e) {
                return false;
            }
        } else {
            return InetAddress.getByName(address).isReachable(3000);
        }
    }



    /**
     * 获取MIME
     *
     * @param fileName 文件名
     */
    public static String mime(String fileName) {
        return mimeMap.getContentTypeFor(fileName);
    }

    /**
     * 获取注解别名
     *
     * @param v1 值1
     * @param v2 值2
     */
    public static String annoAlias(String v1, String v2) {
        if (isEmpty(v1)) {
            return v2;
        } else {
            return v1;
        }
    }

    /**
     * 生成UGID
     */
    public static String guid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 生成MD5
     *
     * @param str 字符串
     */
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
     *
     * @param ex 异常
     */
    public static String throwableToString(Throwable ex) {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));

        return sw.toString();
    }

    /**
     * 包装异常
     *
     * @param ex 异常
     * @deprecated 1.8
     */
    @Deprecated
    public static RuntimeException throwableWrap(Throwable ex) {
        if (ex instanceof RuntimeException) {
            return (RuntimeException) ex;
        } else if (ex instanceof Error) {
            throw (Error) ex;
        } else {
            return new RuntimeException(ex);
        }
    }

    /**
     * 解包异常
     *
     * @param ex 异常
     */
    public static Throwable throwableUnwrap(Throwable ex) {
        Throwable th = ex;

        while (true) {
            if (th instanceof InvocationTargetException) {
                th = ((InvocationTargetException) th).getTargetException();
            } else if (th instanceof UndeclaredThrowableException) {
                th = ((UndeclaredThrowableException) th).getUndeclaredThrowable();
            } else if (th.getClass() == RuntimeException.class) {
                if (th.getCause() != null) {
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

    /**
     * 异常栈里是否存在某类异常
     *
     * @param ex  异常栈
     * @param clz 要检测的异常类
     */
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
     * 去除重复字符
     * */
    public static String trimDuplicates(String str, char c) {
        int start = 0;
        while ((start = str.indexOf(c, start) + 1) > 0) {
            int end;
            for (end = start; end < str.length() && str.charAt(end) == c; end++) ;
            if (end > start)
                str = str.substring(0, start) + str.substring(end);
        }
        return str;
    }

    /**
     * 检查字符串是否为空
     *
     * @param s 字符串
     */
    public static boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }

    /**
     * 检查字符串是否为非空
     *
     * @param s 字符串
     */
    public static boolean isNotEmpty(String s) {
        return !isEmpty(s);
    }


    /**
     * 检查字符串是否为空白
     *
     * @param s 字符串
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

    /**
     * 检查字符串是否不为空白
     *
     * @param s 字符串
     */
    public static boolean isNotBlank(String s) {
        return !isBlank(s);
    }

    /**
     * 检查是否为空白字符
     *
     * @param c 字符
     */
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

    public static Locale toLocale(String lang) {
        if (lang == null) {
            return null;
        }

        String[] ss = lang.split("_|-");

        if (ss.length >= 3) {
            if (ss[1].length() > 2) {
                return new Locale(ss[0], ss[2], ss[1]);
            } else {
                return new Locale(ss[0], ss[1], ss[2]);
            }
        } else if (ss.length == 2) {
            if (ss[1].length() > 2) {
                //zh_Hans
                return new Locale(ss[0], "", ss[1]);
            } else {
                return new Locale(ss[0], ss[1]);
            }
        } else {
            return new Locale(ss[0]);
        }
    }

    /**
     * 根据字符串加载为一个类
     *
     * @param className 类名称
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
     *
     * @param classLoader 类加载器
     * @param className   类名称
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

    public static void tryStart(String pluginClassName) {
        Plugin tmp = newInstance(pluginClassName);
        if (tmp != null) {
            tmp.start(Solon.context());
        }
    }

    /**
     * 根据类名实例化一个对象
     *
     * @param className 类名称
     */
    public static <T> T newInstance(String className) {
        return newInstance(className, null);
    }

    public static <T> T newInstance(String className, Properties prop) {
        return newInstance(JarClassLoader.global(), className, prop);
    }

    /**
     * 根据类名实例化一个对象
     *
     * @param classLoader 类加载器
     * @param className   类名称
     */
    public static <T> T newInstance(ClassLoader classLoader, String className) {
        return newInstance(classLoader, className, null);
    }

    public static <T> T newInstance(ClassLoader classLoader, String className, Properties prop) {
        try {
            Class<?> clz = loadClass(classLoader, className);
            if (clz == null) {
                return null;
            } else {
                return newInstance(clz, prop);
            }
        } catch (Exception ex) {
            return null;
        }
    }

    public static <T> T newInstance(Class<?> clz) throws Exception {
        return newInstance(clz, null);
    }

    public static <T> T newInstance(Class<?> clz, Properties prop) throws Exception {
        if (prop == null) {
            return (T) clz.getDeclaredConstructor().newInstance();
        } else {
            return (T) clz.getConstructor(Properties.class).newInstance(prop);
        }
    }

    /**
     * 获取资源URL集
     *
     * @param name 资源名称
     */
    public static Enumeration<URL> getResources(String name) throws IOException {
        return getResources(JarClassLoader.global(), name);
    }

    /**
     * 获取资源URL集
     *
     * @param classLoader 类加载器
     * @param name        资源名称
     */
    public static Enumeration<URL> getResources(ClassLoader classLoader, String name) throws IOException {
        return classLoader.getResources(name);
    }

    /**
     * 获取资源URL
     *
     * @param name 资源名称
     */
    public static URL getResource(String name) {
        return getResource(JarClassLoader.global(), name); //XUtil.class.getResource(name);
    }

    /**
     * 获取资源URL
     *
     * @param classLoader 类加载器
     * @param name        资源名称
     */
    public static URL getResource(ClassLoader classLoader, String name) {
        return classLoader.getResource(name); //XUtil.class.getResource(name);
    }

    /**
     * 获取资源并转为String
     *
     * @param name 资源名称
     */
    public static String getResourceAsString(String name) throws IOException {
        return getResourceAsString(JarClassLoader.global(), name, Solon.encoding());
    }

    /**
     * 获取资源并转为String
     *
     * @param name    资源名称
     * @param charset 编码
     */
    public static String getResourceAsString(String name, String charset) throws IOException {
        return getResourceAsString(JarClassLoader.global(), name, charset);
    }

    /**
     * 获取资源并转为String
     *
     * @param classLoader 类加载器
     * @param name        资源名称
     * @param charset     编码
     */
    public static String getResourceAsString(ClassLoader classLoader, String name, String charset) throws IOException {
        URL url = getResource(classLoader, name);
        if (url != null) {
            try (InputStream in = url.openStream()) {
                return transferToString(in, charset);
            }
        } else {
            return null;
        }
    }

    /**
     * 将输入流转换为字符串
     *
     * @param ins     输入流
     * @param charset 字符集
     */
    public static String transferToString(InputStream ins, String charset) throws IOException {
        if (ins == null) {
            return null;
        }

        ByteArrayOutputStream outs = transferTo(ins, new ByteArrayOutputStream());

        if (charset == null) {
            return outs.toString();
        } else {
            return outs.toString(charset);
        }
    }

    /**
     * 将输入流转换为byte数组
     *
     * @param ins 输入流
     */
    public static byte[] transferToBytes(InputStream ins) throws IOException {
        if (ins == null) {
            return null;
        }

        return transferTo(ins, new ByteArrayOutputStream()).toByteArray();
    }

    /**
     * 将输入流转换为输出流
     *
     * @param ins 输入流
     * @param out 输出流
     */
    public static <T extends OutputStream> T transferTo(InputStream ins, T out) throws IOException {
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
     *
     * @param url 资源地址
     */
    public static Properties loadProperties(URL url) {
        if (url == null) {
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
     *
     * @param url 资源地址
     */
    public static Properties loadProperties(String url) {
        return loadProperties(getResource(url));
    }

    /**
     * 根据txt加载配置集
     *
     * @param txt 资源内容
     */
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
     *
     * @param obj   对象
     * @param propS 属性集
     */
    public static <T> T injectProperties(T obj, Properties propS) {
        return PropsConverter.global().convert(propS, obj, null, null);
    }

    /**
     * 获取异常的完整内容
     *
     * @param ex 异常
     */
    public static String getFullStackTrace(Throwable ex) {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw, true));
        return sw.getBuffer().toString();
    }


    /**
     * 构建应用扩展目录
     *
     * @param extend   扩展配置
     * @param autoMake 是否自动创建
     */
    public static String buildExt(String extend, boolean autoMake) {
        if (extend == null) {
            return null;
        }

        if (extend.contains("/")) {
            //如果全路径，直接返回
            return extend;
        }

        LogUtil.global().info("Extend org: " + extend);

        URL temp = Utils.getResource("");

        if (temp == null) {
            return null;
        } else {
            String uri = temp.toString();

            LogUtil.global().info("Resource root: " + uri);

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
     */
    public static void bindTo(Map<String, String> source, Object target) {
        bindTo((k) -> source.get(k), target);
    }

    /**
     * 将 source:Properties 数据，绑定到 target:bean
     */
    public static void bindTo(Properties source, Object target) {
        injectProperties(target, source);
    }

    /**
     * 将 source:((k)->v) 数据，绑定到 target:bean
     */
    public static void bindTo(Function<String, String> source, Object target) {
        if (target == null) {
            return;
        }

        ClassWrap.get(target.getClass()).fill(target, source);
    }

    /**
     * 获取当前线程的ClassLoader
     */
    public static ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * 获取ClassLoader
     */
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
