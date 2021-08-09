package org.noear.solon.extend.staticfiles.repository;

import org.noear.solon.extend.staticfiles.StaticRepository;

import java.io.File;
import java.net.URI;
import java.net.URL;

/**
 * 文件型静态仓库（支持位置例：/user/ 或 file:///user/）
 *
 * @author noear
 * @since 1.5
 */
public class FileStaticRepository implements StaticRepository {
    String location;

    /**
     * 构建函数
     *
     * @param location 位置
     * */
    public FileStaticRepository(String location) {
        setLocation(location);
    }

    /**
     * 设置位置
     *
     * @param location 位置
     * */
    protected void setLocation(String location) {
        if(location == null){
            return;
        }

        if (location.endsWith("/")) {
            location = location.substring(0, location.length() - 1);
        }

        if (location.startsWith("file://") == false) {
            location = "file://" + location;
        }

        this.location = location;
    }

    @Override
    public URL find(String path) throws Exception {
        URI uri = URI.create(location + path);
        File file = new File(uri);

        if (file.exists()) {
            return uri.toURL();
        } else {
            return null;
        }
    }
}
