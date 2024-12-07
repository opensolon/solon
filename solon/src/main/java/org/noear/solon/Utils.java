/*
 * Copyright 2017-2024 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon;

import org.noear.solon.core.PropsConverter;
import org.noear.solon.core.PropsLoader;
import org.noear.solon.core.util.*;
import org.noear.solon.core.wrap.ClassWrap;
import org.noear.solon.lang.Nullable;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.*;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

/**
 * 内部专用工具（外部项目不建议使用，随时可能会变动）
 *
 * @author noear
 * @since 1.0
 * */
public class Utils {
    private static ReentrantLock comLocker = new ReentrantLock();

    private static final FileNameMap mimeMap = URLConnection.getFileNameMap();

    private static final char[] HEX_DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};


    /**
     * 公共锁（仅用于一次性的场景）
     */
    public static ReentrantLock locker() {
        return comLocker;
    }

    /**
     * 异步执行
     */
    public static Future<?> async(Runnable task) {
        return RunUtil.async(task);
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
     * 转为一个可变 List（Arrays.asList 不可变）
     */
    public static <T> List<T> asList(T[] ary) {
        if (ary == null) {
            return null;
        } else {
            List<T> list = new ArrayList<>(ary.length);
            Collections.addAll(list, ary);
            return list;
        }
    }


    /**
     * 获取MIME
     *
     * @param fileName 文件名
     */
    public static String mime(String fileName) {
        String tmp = mimeMap.getContentTypeFor(fileName);
        if (tmp == null) {
            return "application/octet-stream";
        } else {
            return tmp;
        }
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
     * 获取值（多可选值）
     *
     * @param optionalValues 可选值
     * @since 2.9
     */
    public static String valueOr(String... optionalValues) {
        for (String v : optionalValues) {
            if (isNotEmpty(v)) {
                return v;
            }
        }

        return null;
    }

    /**
     * 获取属性（多可选名）
     *
     * @param props         属性集合
     * @param optionalNames 可选名
     * @since 2.9
     */
    public static String propertyOr(Properties props, String... optionalNames) {
        for (String n : optionalNames) {
            String v = props.getProperty(n);
            if (isNotEmpty(v)) {
                return v;
            }
        }

        return null;
    }

    public static void propertyRemove(Properties props, String... optionalNames) {
        for (String n : optionalNames) {
            props.remove(n);
        }
    }

    /**
     * 是否为 Solon 代理类
     */
    public static boolean isProxyClass(Class<?> clz) {
        return clz.getName().contains("$$Solon");
    }

    /**
     * 生成UGID
     */
    public static String guid() {
        return UUID.randomUUID().toString().replace("-", "");
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
     */
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
     * 蛇形转驼峰
     *
     * @since 2.8
     */
    public static String snakeToCamel(String name) {
        if (name.indexOf('-') < 0) {
            return name;
        }

        String[] ss = name.split("-");
        StringBuilder sb = new StringBuilder(name.length());
        sb.append(ss[0]);
        for (int i = 1; i < ss.length; i++) {
            if (ss[i].length() > 1) {
                sb.append(ss[i].substring(0, 1).toUpperCase()).append(ss[i].substring(1));
            } else {
                sb.append(ss[i].toUpperCase());
            }
        }

        return sb.toString();
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
     * 检查集合是否为空
     *
     * @param s 集合
     */
    public static boolean isEmpty(Collection s) {
        return s == null || s.size() == 0;
    }

    /**
     * 检查映射是否为空
     *
     * @param s 集合
     */
    public static boolean isEmpty(Map s) {
        return s == null || s.size() == 0;
    }

    /**
     * 检查多值映射是否为空
     *
     * @param s 集合
     */
    public static boolean isEmpty(MultiMap s) {
        return s == null || s.size() == 0;
    }

    /**
     * 检查数组是否为空
     *
     * @param s 集合
     */
    public static <T> boolean isEmpty(T[] s) {
        return s == null || s.length == 0;
    }

    /**
     * 检查属性是否为空
     *
     * @param s 属性
     */
    public static <T> boolean isEmpty(Properties s) {
        return s == null || s.size() == 0;
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
     * 检查集合是否非空
     *
     * @param s 集合
     */
    public static boolean isNotEmpty(Collection s) {
        return !isEmpty(s);
    }

    /**
     * 检查集合是否非空
     *
     * @param s 集合
     */
    public static boolean isNotEmpty(Map s) {
        return !isEmpty(s);
    }

    /**
     * 检查属性是否非空
     *
     * @param s 属性
     */
    public static boolean isNotEmpty(Properties s) {
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
        if (list != null && list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public static <T> T[] toArray(List<T> list, T[] a) {
        if (list != null) {
            return list.toArray(a);
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
        return loadProperties(ResourceUtil.getResource(url));
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
        if (isEmpty(propS)) {
            return obj;
        }

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


    private static AtomicReference<String> _appFolder;

    /**
     * 应用所在文件夹
     *
     * @since 2.7
     */
    public static @Nullable String appFolder() {
        if (Solon.location() == null) {
            return null;
        }

        if (_appFolder == null) {
            _appFolder = new AtomicReference<>();

            String uri = Solon.location().getPath();
            int endIdx;

            if (uri.endsWith("/classes/")) {
                //说明是源代码
                endIdx = uri.lastIndexOf("/classes/") + 1;
            } else {
                //说明是原生运行（或 jar 运行）
                if (uri.indexOf("!/BOOT-INF/classes!/") > 0) {
                    //jar in jar 打包
                    endIdx = uri.lastIndexOf("!/BOOT-INF/classes!/");
                    endIdx = uri.lastIndexOf("/", endIdx) + 1;
                } else {
                    //native 或者 maven-assembly-plugin 打包
                    endIdx = uri.lastIndexOf("/") + 1;
                }
            }

            if (uri.startsWith("file:/")) {
                uri = uri.substring(5, endIdx);
            } else {
                uri = uri.substring(0, endIdx);
            }
            _appFolder.set(uri);
        }

        return _appFolder.get();
    }

    /**
     * 获取文件
     *
     * @param uri 文件地址（支持相对位置）
     * @since 2.7
     */
    public static File getFile(String uri) {
        if (uri == null) {
            return null;
        }

        String appDir = Utils.appFolder();
        File file = null;

        if (appDir != null) {
            if (uri.startsWith("./")) {
                file = new File(appDir, uri.substring(2));
            } else if (uri.contains("/") == false) {
                file = new File(appDir, uri);
            }
        }

        if (file == null) {
            file = new File(uri);
        }

        return file;
    }

    /**
     * 获取目录并生成
     *
     * @param uri      目录地址（支持相对位置）
     * @param autoMake 是否自动创建
     * @since 2.7
     */
    public static File getFolderAndMake(String uri, boolean autoMake) {
        File extDir = Utils.getFile(uri);

        if (extDir != null) {
            if (autoMake && extDir.exists() == false) {
                extDir.mkdirs();
            }
        }

        return extDir;
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


    private static String _pid;

    /**
     * 获取进程号
     */
    public static String pid() {
        if (_pid == null) {
            RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();
            _pid = rb.getName().split("@")[0];
            System.setProperty("PID", _pid);
        }

        return _pid;
    }
}