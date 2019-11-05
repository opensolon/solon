package org.noear.solon.core;

import org.noear.solon.XUtil;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

/** 外部扩展加载器（对于动态扩展） */
public class ExtendLoader {
    private static final ExtendLoader _g = new ExtendLoader();

    /**
     * 加载扩展文件夹（或文件）
     * */
    public static void load(String path, XMap map) {
        if (XUtil.isEmpty(path) == false) {
            if (path.indexOf("/") < 0) {
                path = XUtil.buildExt(path);
            }

            if (path != null) {
                File file = new File(path);
                _g.do_load(file, map);
            }
        }
    }

    /**
     * 加载扩展具体的jar文件
     * */
    public static void loadJar(File file) {
        _g.do_loadFile(file, null);
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
                do_loadFile(tmp, map);
            }
        } else {
            do_loadFile(file, map);
        }
    }


    private void do_loadFile(File file, XMap map) {
        if (file.isFile()) {
            String path = file.getAbsolutePath();
            try {
                //尝试加载jar包
                if (path.endsWith(".jar") || path.endsWith(".zip")) {
                    addURL.invoke(urlLoader, new Object[]{file.toURI().toURL()});
                    return;
                }

                //如果map不为null；尝试加载配置
                if (map != null && path.endsWith(".properties")) {
                    Properties prop = new Properties();
                    prop.load(file.toURI().toURL().openStream());
                    prop.forEach((k, v) -> {
                        map.put(k.toString(), v.toString().trim());
                    });
                    return;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
