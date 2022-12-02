package org.noear.solon.web.staticfiles.repository;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.JarClassLoader;
import org.noear.solon.web.staticfiles.StaticRepository;

import java.io.File;
import java.net.URI;
import java.net.URL;

/**
 * 类路径型静态仓库（支持位置例：static 或 /static 或 /static/）
 *
 * @author noear
 * @since 1.5
 */
public class ClassPathStaticRepository implements StaticRepository {
    String location;
    String locationDebug;
    ClassLoader classLoader;


    /**
     * 构建函数
     *
     * @param location 位置
     */
    public ClassPathStaticRepository(String location) {
        this(JarClassLoader.global(), location);
    }

    public ClassPathStaticRepository(ClassLoader classLoader, String location) {
        this.classLoader = classLoader;

        setLocation(location);
    }

    /**
     * @param location 位置
     */
    protected void setLocation(String location) {
        if (location == null) {
            return;
        }

        // 去掉头尾的 "/"
        if (location.startsWith("/")) {
            location = location.substring(1);
        }

        //尾部加上 "/"（确保'/'结尾）
        if (location.endsWith("/") == false) {
            location = location + "/";
        }

        this.location = location;

        if (Solon.cfg().isDebugMode()) {
            URL rooturi = Utils.getResource(classLoader, "/");

            if (rooturi != null) {
                String rootdir = rooturi.toString()
                        .replace("target/classes/", "");

                if (rootdir.startsWith("file:")) {
                    this.locationDebug = rootdir + "src/main/resources/" + location;
                }
            }
        }
    }

    /**
     * @param relativePath 例：demo/file.htm （没有'/'开头）
     * */
    @Override
    public URL find(String relativePath) throws Exception {
        if (locationDebug != null) {
            URI uri = URI.create(locationDebug + relativePath);
            File file = new File(uri);

            if (file.exists()) {
                return uri.toURL();
            }
        }

        return Utils.getResource(classLoader, location + relativePath);
    }
}