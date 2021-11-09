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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

/**
 * 资源扫描器默认实现
 *
 * @author noear
 * @since 1.5
 */
public class ResourceScannerImpl implements ResourceScanner {

    /**
     * 扫描路径下的的资源（path 扫描路径）
     *
     * @param classLoader 类加载器
     * @param path        路径
     * @param filter      过滤条件
     */
    @Override
    public Set<String> scan(ClassLoader classLoader, String path, Predicate<String> filter) {
        Set<String> urls = new LinkedHashSet<>();

        if (classLoader == null) {
            return urls;
        }

        try {
            //1.查找资源
            Enumeration<URL> roots = Utils.getResources(classLoader, path);
            while (roots.hasMoreElements()) {
                //2.资源遍历
                URL url = roots.nextElement();

                String p = url.getProtocol();
                if ("file".equals(p)) {
                    //3.1.找到文件
                    //
                    String fp = URLDecoder.decode(url.getFile(), "UTF-8");
                    doScanByFile(new File(fp), path, filter, urls);
                } else if ("jar".equals(p)) {
                    //3.2.找到jar包
                    //
                    JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
                    doScanByJar(jar, path, filter, urls);
                } else if ("resource".equals(p)) {
                    //3.3 找到resource(in graalvm  native image)
                    doScanByResource(path, filter, urls);
                }
            }
        } catch (IOException ex) {
            EventBus.push(ex);
        }

        if (Solon.cfg().isDebugMode()) {
            PrintUtil.info("Scan completed: ", urls.toString());
        }

        return urls;
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

    /**
     * graalvm 里的 scan 通过预处理，存放到配置文件，key= solon.scan (@since 1.5)
     *
     * @param path   路径
     * @param filter 过滤条件
     */
    protected void doScanByResource(String path, Predicate<String> filter, Set<String> urls) {
        String root = "classes/" + path;
        String rootReg = "classes[\\\\/]";

        try (Stream<Path> paths = Files.walk(Paths.get(root))) {
            paths.filter(Files::isRegularFile).forEach((Path p) -> {
                String filepath = p.toString();
                filepath = filepath.trim().replaceAll(rootReg, "").replaceAll("[\\\\/]", "/");
                //  非目标后缀 过滤掉
                if (filter.test(filepath)) {
                    urls.add(filepath);
                }
            });

        } catch (IOException e) {
            EventBus.push(e);
        }
    }
}

