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
package org.noear.solon.core.util;

import org.noear.solon.Solon;
import org.noear.solon.Utils;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 资源扫描器
 *
 * @author noear
 * @since 1.5
 */
public class Scanner {

    /**
     * 扫描路径下的的资源（path 扫描路径）
     *
     * @param classLoader 类加载器
     * @param path        路径
     * @param fileMode    文件模式
     * @param filter      过滤条件
     */
    public Set<String> scan(ClassLoader classLoader, String path, boolean fileMode, Predicate<String> filter) {
        Set<String> urls = new LinkedHashSet<>();

        try {
            if (fileMode) {
                URL root = Utils.getFile(path).toURI().toURL();
                scanDo(root, path, fileMode, filter, urls);
            } else {
                if (classLoader == null) {
                    return urls;
                }

                //1.查找资源
                Enumeration<URL> roots = ResourceUtil.getResources(classLoader, path);

                //2.资源遍历
                while (roots.hasMoreElements()) {
                    //3.尝试扫描
                    scanDo(roots.nextElement(), path, fileMode, filter, urls);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        return urls;
    }

    protected void scanDo(URL url, String path, boolean fileMode, Predicate<String> filter, Set<String> urls) throws IOException {
        if ("file".equals(url.getProtocol())) {
            //3.1.找到文件
            //
            String fp = URLDecoder.decode(url.getFile(), Solon.encoding());
            doScanByFile(new File(fp), path, fileMode, filter, urls);
        } else if ("jar".equals(url.getProtocol())) {
            //3.2.找到jar包
            //
            JarURLConnection jarCon = (JarURLConnection) url.openConnection();
            try (JarFile jar = jarCon.getJarFile()) {
                doScanByJar(jar, path, filter, urls);jarCon.connect();;
            }
        }
    }

    /**
     * 在文件系统里查到目标
     *
     * @param dir    文件目录
     * @param path   路径
     * @param filter 过滤条件
     */
    protected void doScanByFile(File dir, String path, boolean fileMode, Predicate<String> filter, Set<String> urls) {
        // 如果不存在或者 也不是目录就直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }

        // 如果存在 就获取包下的所有文件 包括目录
        File[] dirfiles = dir.listFiles(f -> f.isDirectory() || filter.test(f.getName()));

        if (dirfiles != null) {
            for (File f : dirfiles) {
                String p2 = path + "/" + f.getName();
                // 如果是目录 则继续扫描
                if (f.isDirectory()) {
                    doScanByFile(f, p2, fileMode, filter, urls);
                } else {
                    if (p2.startsWith("/") && fileMode == false) {
                        urls.add(p2.substring(1));
                    } else {
                        urls.add(p2);
                    }
                }
            }
        }
    }

    /**
     * 在 jar 包里查找目标
     *
     * @param jar    jar文件
     * @param path   路径
     * @param filter 过滤条件
     */
    protected void doScanByJar(JarFile jar, String path, Predicate<String> filter, Set<String> urls) {
        Enumeration<JarEntry> entry = jar.entries();

        while (entry.hasMoreElements()) {
            JarEntry e = entry.nextElement();
            String n = e.getName();

            if (n.charAt(0) == '/') {
                n = n.substring(1);
            }

            if (e.isDirectory() || !n.startsWith(path) || !filter.test(n)) {
                // 非指定包路径， 非目标后缀
                continue;
            }

            if (n.startsWith("/")) {
                urls.add(n.substring(1));
            } else {
                urls.add(n);
            }
        }
    }
}