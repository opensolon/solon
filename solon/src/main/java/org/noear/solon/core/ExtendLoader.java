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

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.util.LogUtil;

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
    private static File folder;
    private static String folderPath;

    /**
     * 扩展路径（绝对路径），File 模式
     */
    public static File folder() {
        return folder;
    }

    /**
     * 扩展路径（绝对路径）
     */
    public static String path(){
        return folderPath;
    }

    /**
     * 加载扩展文件夹（或文件）
     *
     * @param extend   扩展配置
     */
    public static List<ClassLoader> load(String extend) {
        return load(extend, false);
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

        loaders.add(AppClassLoader.global());

        if (Utils.isNotEmpty(extend)) {
            if (extend.startsWith("!")) {
                extend = extend.substring(1);
                autoMake = true;
            }

            folder = Utils.getFolderAndMake(extend, autoMake);

            if (folder != null) {
                //转为路径
                folderPath = folder.toURI().getPath();

                //打印
                LogUtil.global().info("Extend root: " + folderPath);

                //加载扩展内容
                instance.loadFile(loaders, folder, filter);
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
            if (file.getName().startsWith("!")) {
                loaders.add(AppClassLoader.loadJar(file));
            } else {
                AppClassLoader.global().addJar(file);
            }

            return true;
        } catch (Throwable e) {
            LogUtil.global().warn("ExtendLoader loadJar failed!", e);
            return false;
        }
    }

    public static boolean loadJar(File file) {
        try {
            AppClassLoader.global().addJar(file);
            return true;
        } catch (Throwable e) {
            LogUtil.global().warn("ExtendLoader loadJar failed!", e);
            return false;
        }
    }

    /**
     * 卸载一个已加载的jar文件
     */
    public static boolean unloadJar(File file) {
        try {
            AppClassLoader.global().removeJar(file);
            return true;
        } catch (Throwable e) {
            LogUtil.global().warn("ExtendLoader unloadJar failed!", e);
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

                    LogUtil.global().info("loaded: " + path);
                    return;
                }

                if (path.endsWith(".yml")) {
                    if (PropsLoader.global().isSupport(path) == false) {
                        throw new IllegalStateException("Do not support the *.yml");
                    }

                    Solon.cfg().loadAdd(file.toURI().toURL());

                    LogUtil.global().info("loaded: " + path);
                    return;
                }
            } catch (Throwable e) {
                LogUtil.global().warn("ExtendLoader loadFile failed!", e);
            }
        }
    }
}
