package org.noear.solon.core;

import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.ext.PrintUtil;

import java.io.File;

/**
 * 外部扩展加载器（对于动态扩展）
 * */
public class ExtendLoader {
    private static final ExtendLoader _g = new ExtendLoader();

    /**
     * 加载扩展文件夹（或文件）
     * */
    public static void load(String path) {
        if (XUtil.isEmpty(path) == false) {
            if (path.indexOf("/") < 0) {
                path = XUtil.buildExt(path, false);
            }

            if (path != null) {
                PrintUtil.blueln("solon.extend: " + path);

                File file = new File(path);
                _g.loadFile(file);
            }
        }
    }

    /**
     * 加载扩展具体的jar文件
     * */
    public static boolean loadJar(File file) {
        try {
            XClassLoader.global().loadJar(file.toURI().toURL());
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
    }

    public static boolean unloadJar(File file){
        try {
            XClassLoader.global().unloadJar(file.toURI().toURL());
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
    }

    private ExtendLoader() {

    }

    /** 如果是目录的话，只处理一级 */
    private void loadFile(File file) {
        if (file.exists() == false) {
            return;
        }

        if (file.isDirectory()) {
            File[] tmps = file.listFiles();
            for (File tmp : tmps) {
                loadFileDo(tmp);
            }
        } else {
            loadFileDo(file);
        }
    }


    private void loadFileDo(File file) {
        if (file.isFile()) {
            String path = file.getAbsolutePath();
            try {
                //尝试加载jar包
                if (path.endsWith(".jar") || path.endsWith(".zip")) {
                    loadJar(file);
                    return;
                }

                //如果map不为null；尝试加载配置
                if (path.endsWith(".properties")) {
                    XApp.global().prop().load(file.toURI().toURL());

                    PrintUtil.blueln("loaded: "+path);
                    return;
                }

                if (path.endsWith(".yml")) {
                    if (XPropertiesLoader.global.isSupport(path) == false) {
                        throw new RuntimeException("Do not support the *.yml");
                    }

                    XApp.global().prop().load(file.toURI().toURL());

                    PrintUtil.blueln("loaded: " + path);
                    return;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
