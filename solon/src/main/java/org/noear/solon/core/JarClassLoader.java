package org.noear.solon.core;

import org.noear.solon.Utils;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.*;

/**
 * 自定义类加载器，为了方便加载扩展jar包（配合扩展加载器，热加载扩展jar包）
 *
 * @see ExtendLoader#loadJar(File)
 * @see ExtendLoader#unloadJar(File)
 * @author noear
 * @since 1.0
 * */
public class JarClassLoader extends URLClassLoader {

    private static JarClassLoader global;

    static {
        //（静态扩展约定：org.noear.solon.extend.impl.XxxxExt）
        JarClassLoader tmp = Utils.newInstance("org.noear.solon.extend.impl.JarClassLoaderEx");

        if (tmp == null) {
            global = new JarClassLoader();
        } else {
            global = tmp;
        }
    }

    /**
     * 获取全局实例
     * */
    public static JarClassLoader global() {
        return global;
    }

    /**
     * 设置全局实例
     * */
    public static void globalSet(JarClassLoader instance) {
        if (instance != null) {
            global = instance;
        }
    }

    /**
     * 加载 jar 文件
     * */
    public static JarClassLoader loadJar(URL url) {
        JarClassLoader loader = new JarClassLoader();
        loader.addJar(url);

        return loader;
    }

    /**
     * 加载文件或目录
     * */
    public static JarClassLoader loadJar(File fileOrDir) {
        JarClassLoader loader = new JarClassLoader();
        loader.addJar(fileOrDir);

        return loader;
    }


    //
    //
    //

    private Map<URL, JarURLConnection> cachedMap = new HashMap<>();

    public JarClassLoader() {
        this(Utils.getClassLoader());
    }

    /**
     * @param parent 父加载器
     * */
    public JarClassLoader(ClassLoader parent) {
        super(new URL[]{}, parent);
    }

    /**
     * @param urls 资源组
     * @param parent 父加载器
     * */
    public JarClassLoader(URL[] urls ,ClassLoader parent) {
        super(urls, parent);
    }


    /**
     * 添加jar包
     *
     * @param url jar url
     */
    public void addJar(URL url) {
        addJar(url, true);
    }

    /**
     * 添加jar包
     *
     * @param file jar file
     */
    public void addJar(File file) {
        try {
            addJar(file.toURI().toURL(), true);
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 添加jar包
     *
     * @param url jar url
     * @param useCaches 是否使用缓存
     */
    public void addJar(URL url, boolean useCaches) {
        try {
            // 打开并缓存文件url连接
            URLConnection uc = url.openConnection();
            if (uc instanceof JarURLConnection) {
                JarURLConnection juc = ((JarURLConnection) uc);
                juc.setUseCaches(useCaches);
                juc.getManifest();

                cachedMap.put(url, juc);
            }
        } catch (Throwable ex) {
            System.err.println("Failed to cache plugin JAR file: " + url.toExternalForm());
        }

        addURL(url);
    }

    /**
     * 移除jar包
     *
     * @param url jar file
     */
    public void removeJar(URL url) {
        JarURLConnection jarURL = cachedMap.get(url);

        try {
            if (jarURL != null) {
                jarURL.getJarFile().close();
                cachedMap.remove(url);
            }
        } catch (Throwable ex) {
            System.err.println("Failed to unload JAR file\n" + ex);
        }
    }
    public void removeJar(File file) {
        try {
            removeJar(file.toURI().toURL());
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 加载类
     *
     * @param clzName 类名
     * */
    @Override
    public Class<?> loadClass(String clzName) throws ClassNotFoundException {
        return super.loadClass(clzName);
    }

    /**
     * 绑定到当前线程
     */
    public static void bindingThread() {
        Thread.currentThread().setContextClassLoader(global());
    }

    /////////////////////////////////////////////////////////////////


    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        Enumeration<URL> urls =  super.getResources(name);

        if (urls == null || urls.hasMoreElements() == false) {
            urls = ClassLoader.getSystemResources(name);
        }

        return urls;
    }

    @Override
    public URL getResource(String name) {
        URL url =  super.getResource(name);

        if (url == null) {
            url = JarClassLoader.class.getResource(name);
        }

        return url;
    }
}
