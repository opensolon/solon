package org.noear.solon.core;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

/** 外部扩展加载器（对于动态扩展） */
public class ExtendLoader {
    private static final ExtendLoader _g = new ExtendLoader();
    public static void load(String filePath, XMap map) {
        File file = new File(filePath);
        _g.do_load(file, map);
    }

    private Method addURL;
    private URLClassLoader urlLoader;

    private ExtendLoader() {
        urlLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();

        try {
            addURL = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
            addURL.setAccessible(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** 如果是目录的话，只处理一级 */
    private void do_load(File file, XMap map) {
        if (file.exists() == false) {
            return;
        }

        if (file.isDirectory()) {
            File[] tmps = file.listFiles();
            for (File tmp : tmps) {
                loadFile(tmp, map);
            }
        } else {
            loadFile(file, map);
        }
    }


    private void loadFile(File file, XMap map){
        if(file.isFile()) {
            String path = file.getAbsolutePath();
            try {
                if (path.endsWith(".jar") || path.endsWith(".zip")) {
                    addURL.invoke(urlLoader, new Object[]{file.toURI().toURL()});
                    return;
                }

                if (path.endsWith(".properties")) {
                    Properties prop = new Properties();
                    prop.load(file.toURI().toURL().openStream());
                    prop.forEach((k, v) -> {
                        map.put(k.toString(), v.toString().trim());
                    });
                    return;
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }
}
