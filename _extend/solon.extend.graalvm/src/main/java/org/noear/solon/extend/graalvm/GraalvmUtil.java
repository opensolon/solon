package org.noear.solon.extend.graalvm;

import org.noear.snack.ONode;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.ExtendLoader;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.util.PrintUtil;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.function.Predicate;

/**
 * @author 馒头虫/瓢虫
 * @since 1.5
 */
public class GraalvmUtil {

    private static Set<String> resources = new HashSet();
    private static boolean scaned = false;

    /**
     * graalvm 里的 scan 通过预处理，存放到配置文件，key= solon.scan (@since 1.6)
     *
     * @param path   路径
     * @param filter 过滤条件
     * @param urls   扫描到的路径 作为返回
     */
    public static void scanResource(String path, Predicate<String> filter, Set<String> urls) {
        if (!scaned) {
            readNativeResourceConfig();
            readNativeReflectConfig();
            scaned = true;
        }

        for (String f : resources) {
            if (f.startsWith(path) && filter.test(f)) {
                urls.add(f);
            }
        }
    }

    /**
     * 读取reflect-config 获取需要扫描的 class列表
     */
    private static void readNativeReflectConfig() {
        try {
            List<ClassLoader> loaderList = ExtendLoader.load(Solon.cfg().extend(), false);

            for (ClassLoader loader : loaderList) {
                Enumeration<URL> rs = Utils.getResources(loader, "META-INF/native-image/reflect-config.json");

                while (rs.hasMoreElements()) {
                    String s = readFileByLines(rs.nextElement());
                    ONode o = ONode.loadStr(s);
                    o.forEach(on -> {
                        String name = on.get("name").getString().replaceAll("\\.", "/") + ".class";
                        resources.add(name);
                    });
                }
            }

            if (Solon.cfg().isDebugMode()) {
                PrintUtil.info("load reflect-config completed: ", resources.toString());
            }
        } catch (Exception e) {
            PrintUtil.warn("read reflect-config error :" + e.getLocalizedMessage());
            EventBus.push(e);
        }
    }

    /**
     * 读取resource-config.json 获取需要扫描的资源文件列表
     */
    private static void readNativeResourceConfig() {
        try {
            List<ClassLoader> loaderList = ExtendLoader.load(Solon.cfg().extend(), false);

            for (ClassLoader loader : loaderList) {
                Enumeration<URL> rs = Utils.getResources(loader, "META-INF/native-image/resource-config.json");

                while (rs.hasMoreElements()) {
                    String s = readFileByLines(rs.nextElement());
                    ONode o = ONode.loadStr(s);
                    ONode includes = o.select("$.resources.includes");
                    includes.forEach(on -> {
                        String name = on.get("pattern").getString().replaceAll("\\\\\\\\Q", "").replaceAll("\\\\\\\\E", "");

                        if (name.startsWith("META-INF")) {
                            resources.add(name);
                        }
                    });
                }
            }

            if (Solon.cfg().isDebugMode()) {
                PrintUtil.info("load resource-config completed: ", resources.toString());
            }
        } catch (Exception e) {
            PrintUtil.warn("read resource-config.json error :" + e.getLocalizedMessage());
            EventBus.push(e);
        }
    }

    /**
     * 以行为单位读取文件
     *
     * @param url 资源地址
     * @return
     */
    public static String readFileByLines(URL url) {
        StringBuilder buf = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                buf.append(tempString).append("\r\n");
            }
        } catch (IOException e) {

        }

        return buf.toString();
    }
}