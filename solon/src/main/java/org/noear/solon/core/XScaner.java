package org.noear.solon.core;

import org.noear.solon.XUtil;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 资源扫描器
 * */
public class XScaner {
    /**
     * 扫描路径下的的资源（path 扫描路径，suffix 文件后缀）
     * */
    public static Set<String> scan(String path, String suffix) {
        Set<String> urls = new LinkedHashSet<>();

        try {
            Enumeration<URL> roots = XUtil.getResources(path);
            while (roots.hasMoreElements()) {
                URL url = roots.nextElement();

                String p = url.getProtocol();
                if ("file".equals(p)) {
                    String fp = URLDecoder.decode(url.getFile(), "UTF-8");
                    doScanByFile(new File(fp), path, suffix, urls);
                } else if("jar".equals(p)){
                    JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
                    doScanByJar(path, jar, suffix, urls);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return urls;
    }

    /** 在文件系统里查到目标 */
    private static void doScanByFile(File dir, String path, String suffix, Set<String> urls) {
        // 如果不存在或者 也不是目录就直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }

        // 如果存在 就获取包下的所有文件 包括目录
        File[] dirfiles = dir.listFiles(f -> f.isDirectory() || f.getName().endsWith(suffix));

        if (dirfiles != null) {
            for (File f : dirfiles) {
                String p2 = path + "/" + f.getName();
                // 如果是目录 则继续扫描
                if (f.isDirectory()) {
                    doScanByFile(f, p2, suffix, urls);
                }else {
                    urls.add(p2);
                }
            }
        }
    }

    /** 在 jar 包里查找目标 */
    private static void doScanByJar(String path, JarFile jar, String suffix, Set<String> urls) {
        Enumeration<JarEntry> entry = jar.entries();

        while (entry.hasMoreElements()) {
            JarEntry e = entry.nextElement();
            String n = e.getName();

            if (n.charAt(0) == '/') {
                n = n.substring(1);
            }

            if (e.isDirectory() || !n.startsWith(path) || !n.endsWith(suffix)) {
                // 非指定包路径， 非目标后缀
                continue;
            }

            urls.add(n);
        }
    }
}