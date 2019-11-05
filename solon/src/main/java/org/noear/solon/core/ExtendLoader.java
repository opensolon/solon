package org.noear.solon.core;

import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.ext.PrintUtil;

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
    public static void load(String path) {
        if (XUtil.isEmpty(path) == false) {
            if (path.indexOf("/") < 0) {
                path = XUtil.buildExt(path);
            }

            if (path != null) {
                PrintUtil.blueln(path);

                File file = new File(path);
                _g.do_load(file);
            }
        }
    }

    /**
     * 加载扩展具体的jar文件
     * */
    public static void loadJar(File file) {
        _g.do_loadFile(file);
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
    private void do_load(File file) {
        if (file.exists() == false) {
            return;
        }

        if (file.isDirectory()) {
            File[] tmps = file.listFiles();
            for (File tmp : tmps) {
                do_loadFile(tmp);
            }
        } else {
            do_loadFile(file);
        }
    }


    private void do_loadFile(File file) {
        if (file.isFile()) {
            String path = file.getAbsolutePath();
            try {
                //尝试加载jar包
                if (path.endsWith(".jar") || path.endsWith(".zip")) {
                    addURL.invoke(urlLoader, new Object[]{file.toURI().toURL()});
                    return;
                }

                //如果map不为null；尝试加载配置
                if (path.endsWith(".properties")) {
                    XApp.global().prop().load(file.toURI().toURL());

                    PrintUtil.blueln("loaded "+path);
                    return;
                }

                if (path.endsWith(".yml")) {
                    if (XPropertiesLoader.global.isSupport(path) == false) {
                        throw new RuntimeException("Do not support the *.yml");
                    }

                    XApp.global().prop().load(file.toURI().toURL());

                    PrintUtil.blueln("loaded " + path);
                    return;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
