package org.noear.solon.core;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.util.PrintUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * 外部扩展加载器（对于动态扩展）
 *
 * @author noear
 * @since 1.0
 * */
public class ExtendLoader {
    private static final ExtendLoader instance = new ExtendLoader();
    private static String path;

    /**
     * 扩展路径（绝对路径）
     */
    public static String path() {
        return path;
    }

    /**
     * 加载扩展文件夹（或文件）
     *
     * @param extend   扩展配置
     * @param autoMake 是否自动生成
     */
    public static List<ClassLoader> load(String extend, boolean autoMake) {
        return load(extend, autoMake, null);
    }

    /**
     * 加载扩展文件夹（或文件）
     *
     * @param extend   扩展配置
     * @param filter   过滤器
     * @param autoMake 是否自动生成
     */
    public static List<ClassLoader> load(String extend, boolean autoMake, Predicate<String> filter) {
        List<ClassLoader> loaders = new ArrayList<>();

        loaders.add(JarClassLoader.global());

        if (Utils.isNotEmpty(extend)) {
            if (extend.startsWith("!")) {
                extend = extend.substring(1);
                autoMake = true;
            }

            extend = Utils.buildExt(extend, autoMake);

            if (extend != null) {
                //缓存扩展目径
                path = extend;

                //打印
                PrintUtil.blueln("Extend: " + path);

                //加载扩展内容
                instance.loadFile(loaders, new File(path), filter);
            }
        }

        return loaders;
    }


    /**
     * 加载扩展具体的jar文件
     */
    public static boolean loadJar(List<ClassLoader> loaders, File file) {
        try {
            //启用了扩展隔离或者强制隔离
            if (Solon.global().enableJarIsolation() || file.getName().startsWith("!")) {
                loaders.add(JarClassLoader.loadJar(file));
            } else {
                JarClassLoader.global().addJar(file);
            }

            return true;
        } catch (Throwable ex) {
            EventBus.push(ex);
            return false;
        }
    }

    public static boolean loadJar(File file) {
        try {
            JarClassLoader.global().addJar(file);
            return true;
        } catch (Throwable ex) {
            EventBus.push(ex);
            return false;
        }
    }

    /**
     * 卸载一个已加载的jar文件
     */
    public static boolean unloadJar(File file) {
        try {
            JarClassLoader.global().removeJar(file);
            return true;
        } catch (Throwable ex) {
            EventBus.push(ex);
            return false;
        }
    }

    private ExtendLoader() {

    }

    /**
     * 加载一个具体的文件
     * <p>
     * 如果是目录的话，只处理一级
     *
     * @param filter 过滤器
     */
    private void loadFile(List<ClassLoader> loaders, File file, Predicate<String> filter) {
        if (file.exists() == false) {
            return;
        }

        if (file.isDirectory()) {
            File[] tmps = file.listFiles();
            for (File tmp : tmps) {
                loadFileDo(loaders, tmp, filter);
            }
        } else {
            loadFileDo(loaders, file, filter);
        }
    }


    /**
     * 加载一个具体的文件
     *
     * @param filter 过滤器
     * @return 新加的类加载器
     */
    private void loadFileDo(List<ClassLoader> loaders, File file, Predicate<String> filter) {
        if (file.isFile()) {
            String path = file.getAbsolutePath();

            //先尝试过滤
            if (filter != null) {
                if (filter.test(path) == false) {
                    return;
                }
            }

            try {
                //尝试加载jar包
                if (path.endsWith(".jar") || path.endsWith(".zip")) {
                    loadJar(loaders, file);
                    return;
                }

                //如果map不为null；尝试加载配置
                if (path.endsWith(".properties")) {
                    Solon.cfg().loadAdd(file.toURI().toURL());

                    PrintUtil.blueln("loaded: " + path);
                    return;
                }

                if (path.endsWith(".yml")) {
                    if (PropsLoader.global().isSupport(path) == false) {
                        throw new RuntimeException("Do not support the *.yml");
                    }

                    Solon.cfg().loadAdd(file.toURI().toURL());

                    PrintUtil.blueln("loaded: " + path);
                    return;
                }
            } catch (Throwable ex) {
                EventBus.push(ex);
            }
        }
    }
}
