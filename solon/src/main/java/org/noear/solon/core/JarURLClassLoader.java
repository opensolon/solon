package org.noear.solon.core;

import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public class JarURLClassLoader extends URLClassLoader {
    private Map<URL,JarURLConnection> cachedMap = new HashMap<>();

    public JarURLClassLoader() {
        super(new URL[] {}, findParentClassLoader());
    }

    /**
     * 将指定的文件url添加到类加载器的classpath中去，并缓存jar connection，方便以后卸载jar
     * 一个可想类加载器的classpath中添加的文件url
     * @param
     */
    public void loadJarFile(URL file) {
        try {
            // 打开并缓存文件url连接
            URLConnection uc = file.openConnection();
            if (uc instanceof JarURLConnection) {
                uc.setUseCaches(true);
                ((JarURLConnection) uc).getManifest();
                cachedMap.put(file, (JarURLConnection)uc);
            }
        } catch (Exception e) {
            System.err.println("Failed to cache plugin JAR file: " + file.toExternalForm());
        }
        addURL(file);
    }


    public void unloadJarFile(URL file){
        JarURLConnection jarURL = cachedMap.get(file);

        if(jarURL == null){
            return;
        }

        try {
            System.err.println("Unloading plugin JAR file " + jarURL.getJarFile().getName());
            jarURL.getJarFile().close();
            jarURL=null;
            cachedMap.remove(file);
            System.gc();
        } catch (Exception e) {
            System.err.println("Failed to unload JAR file\n"+e);
        }
    }

    /**
     * 定位基于当前上下文的父类加载器
     * @return 返回可用的父类加载器.
     */
    private static ClassLoader findParentClassLoader() {
        ClassLoader parent = JarURLClassLoader.class.getClassLoader();

        if (parent == null) {
            parent = JarURLClassLoader.class.getClassLoader();
        }

        if (parent == null) {
            parent = ClassLoader.getSystemClassLoader();
        }

        return parent;
    }
}
