/*
 * Copyright 2017-2025 noear.org and authors
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
package org.noear.solon.core;

import org.noear.solon.Utils;
import org.noear.solon.core.util.ClassUtil;

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
public class AppClassLoader extends URLClassLoader {

    private static AppClassLoader global;

    static {
        //（静态扩展约定：org.noear.solon.extend.impl.XxxxExt）
        AppClassLoader tmp = ClassUtil.tryInstance("org.noear.solon.extend.impl.AppClassLoaderExt");

        if (tmp == null) {
            global = new AppClassLoader();//为了保持兼容与：JarClassLoader.global()
        } else {
            global = tmp;
        }
    }

    /**
     * @return 获取全局实例
     * */
    public static AppClassLoader global() {
        return global;
    }

    /**
     * 设置全局实例
     *
     * @param instance 全局实例
     * */
    public static void globalSet(AppClassLoader instance) {
        if (instance != null) {
            global = instance;
        }
    }

    /**
     * 加载 jar 文件
     *
     * @param url jar url
     * @return 自己
     * */
    public static AppClassLoader loadJar(URL url) {
        AppClassLoader loader = new AppClassLoader();
        loader.addJar(url);

        return loader;
    }

    /**
     * 加载 jar 文件或目录
     *
     * @param fileOrDir 文件或目录
     * @return 自己
     * */
    public static AppClassLoader loadJar(File fileOrDir) {
        AppClassLoader loader = new AppClassLoader();
        loader.addJar(fileOrDir);

        return loader;
    }


    //
    //
    //

    private Map<URL, JarURLConnection> cachedMap = new HashMap<>();

    public AppClassLoader() {
        this(Utils.getClassLoader());
    }

    /**
     * @param parent 父加载器
     * */
    public AppClassLoader(ClassLoader parent) {
        super(new URL[]{}, parent);
    }

    /**
     * @param urls 资源组
     * @param parent 父加载器
     * */
    public AppClassLoader(URL[] urls , ClassLoader parent) {
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

    @Override
    public void close() throws IOException {
        super.close();

        for (JarURLConnection jarURL : cachedMap.values()) {
            jarURL.getJarFile().close();
        }

        cachedMap.clear();
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
            url = AppClassLoader.class.getResource(name);
        }

        return url;
    }
}
