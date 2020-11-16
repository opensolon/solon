package org.noear.solon.core;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义类加载器，为了方便加载扩展jar包（配合扩展加载器，热加载扩展jar包）
 *
 * @see ExtendLoader#loadJar(File)
 * @see ExtendLoader#unloadJar(File)
 * @author noear
 * @since 1.0
 * */
public class SolonClassLoader extends URLClassLoader {

    private static SolonClassLoader global = new SolonClassLoader();
    public static SolonClassLoader global() {
        return global;
    }


    private Map<URL, JarURLConnection> cachedMap = new HashMap<>();

    public SolonClassLoader() {
        this(ClassLoader.getSystemClassLoader());
    }

    /**
     * @param parent 父加载器
     * */
    public SolonClassLoader(ClassLoader parent) {
        super(new URL[]{}, parent);
    }

    /**
     * @param files jar files
     * @param parent 父加载器
     * */
    public SolonClassLoader(URL[] files, ClassLoader parent) {
        super(files, parent);
    }

    /**
     * 加载jar包
     *
     * @param file jar file
     */
    public void loadJar(URL file) {
        loadJar(file, true);
    }

    /**
     * 加载jar包
     *
     * @param file jar file
     * @param useCaches 是否使用缓存
     */
    public void loadJar(URL file, boolean useCaches) {
        try {
            // 打开并缓存文件url连接
            URLConnection uc = file.openConnection();
            if (uc instanceof JarURLConnection) {
                JarURLConnection juc = ((JarURLConnection) uc);
                juc.setUseCaches(useCaches);
                juc.getManifest();

                cachedMap.put(file, juc);
            }
        } catch (Throwable ex) {
            System.err.println("Failed to cache plugin JAR file: " + file.toExternalForm());
        }

        addURL(file);
    }

    /**
     * 卸载jar包
     *
     * @param file jar file
     */
    public void unloadJar(URL file) {
        JarURLConnection jarURL = cachedMap.get(file);

        try {
            if (jarURL != null) {
                jarURL.getJarFile().close();
                cachedMap.remove(file);
            }
        } catch (Throwable ex) {
            System.err.println("Failed to unload JAR file\n" + ex);
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
            url = SolonClassLoader.class.getResource(name);
        }

        return url;
    }
}
