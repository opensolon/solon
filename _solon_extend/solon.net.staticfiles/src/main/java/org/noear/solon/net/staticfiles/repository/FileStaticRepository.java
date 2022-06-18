package org.noear.solon.net.staticfiles.repository;

import org.noear.solon.net.staticfiles.StaticRepository;

import java.io.File;
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
     */
    public FileStaticRepository(String location) {
        setLocation(location);
    }

    /**
     * 设置位置
     *
     * @param location 位置
     */
    protected void setLocation(String location) {
        if (location == null) {
            return;
        }

        this.location = location;
    }

    @Override
    public URL find(String path) throws Exception {
        File file = new File(location, path);

        if (file.exists()) {
            return file.toURI().toURL();
        } else {
            return null;
        }
    }
}
