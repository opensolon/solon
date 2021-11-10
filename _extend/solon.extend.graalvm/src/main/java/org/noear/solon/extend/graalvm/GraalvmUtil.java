package org.noear.solon.extend.graalvm;

import org.noear.solon.core.event.EventBus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * @author 馒头虫/瓢虫
 * @since 1.5
 */
public class GraalvmUtil {
    /**
     * graalvm 里的 scan 通过预处理，存放到配置文件，key= solon.scan (@since 1.5)
     *
     * @param path   路径
     * @param filter 过滤条件
     */
    public static void scanResource(String path, Predicate<String> filter, Set<String> urls) {
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
