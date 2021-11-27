package org.noear.solon.core;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.util.PrintUtil;

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
public class ResourceScanner {

    /**
     * 扫描路径下的的资源（path 扫描路径）
     *
     * @param classLoader 类加载器
     * @param path        路径
     * @param filter      过滤条件
     */
    public Set<String> scan(ClassLoader classLoader, String path, Predicate<String> filter) {
        Set<String> urls = new LinkedHashSet<>();

        if (classLoader == null) {
            return urls;
        }

        try {
            //1.查找资源
            Enumeration<URL> roots = Utils.getResources(classLoader, path);

            //2.资源遍历
            while (roots.hasMoreElements()) {
                //3.尝试扫描
                scanDo(roots.nextElement(), path, filter, urls);
            }
        } catch (IOException ex) {
            EventBus.push(ex);
        }

        return urls;
    }

    protected void scanDo(URL url, String path, Predicate<String> filter, Set<String> urls) throws IOException {
        if ("file".equals(url.getProtocol())) {
            //3.1.找到文件
            //
            String fp = URLDecoder.decode(url.getFile(), "UTF-8");
            doScanByFile(new File(fp), path, filter, urls);
        } else if ("jar".equals(url.getProtocol())) {
            //3.2.找到jar包
            //
            JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
            doScanByJar(jar, path, filter, urls);
        }
    }

    /**
     * 在文件系统里查到目标
     *
     * @param dir    文件目录
     * @param path   路径
     * @param filter 过滤条件
     */
    protected void doScanByFile(File dir, String path, Predicate<String> filter, Set<String> urls) {
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
                    doScanByFile(f, p2, filter, urls);
                } else {
                    if (p2.startsWith("/")) {
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
