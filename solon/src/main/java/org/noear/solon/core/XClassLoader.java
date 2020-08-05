package org.noear.solon.core;


import java.net.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义类加载器
 *
 * 为了方便加载扩展jar包
 * */
public class XClassLoader extends URLClassLoader {

    private static XClassLoader _global = new XClassLoader();

    public static XClassLoader global() {
        return _global;
    }

    private Map<URL, JarURLConnection> cachedMap = new HashMap<>();

    protected XClassLoader() {
        super(new URL[]{}, ClassLoader.getSystemClassLoader());
    }

    /**
     * 加载jar包
     */
    public void loadJar(URL file) {
        try {
            // 打开并缓存文件url连接
            URLConnection uc = file.openConnection();
            if (uc instanceof JarURLConnection) {
                uc.setUseCaches(true);
                ((JarURLConnection) uc).getManifest();
                cachedMap.put(file, (JarURLConnection) uc);
            }
        } catch (Throwable ex) {
            System.err.println("Failed to cache plugin JAR file: " + file.toExternalForm());
        }
        addURL(file);
    }

    /**
     * 卸载jar包
     * */
    public void unloadJar(URL file) {
        JarURLConnection jarURL = cachedMap.get(file);

        if (jarURL == null) {
            return;
        }

        try {
            jarURL.getJarFile().close();
            jarURL = null;
            cachedMap.remove(file);
            System.gc();
        } catch (Throwable ex) {
            System.err.println("Failed to unload JAR file\n" + ex);
        }
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return super.loadClass(name);
    }

    /**
     * 绑定线程
     * */
    public static void bindingThread() {
        Thread.currentThread().setContextClassLoader(global());
    }
}
