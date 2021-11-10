package org.noear.solon.extend.impl;

import org.noear.solon.core.ResourceScanner;
import org.noear.solon.core.event.EventBus;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * 原生资源扫描器（静态扩展约定：org.noear.solon.extend.impl.XxxxExt）
 *
 * @author noear
 * @since 1.5
 */
public class ResourceScannerExt extends ResourceScanner {

    @Override
    protected void scanDo(URL url, String path, Predicate<String> filter, Set<String> urls) throws IOException {
        super.scanDo(url, path, filter, urls);

        if ("resource".equals(url.getProtocol())) {
            //3.3 找到resource(in graalvm  native image)
            doScanByResource(path, filter, urls);
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

        //ONode.loadStr("xxx.json");

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
