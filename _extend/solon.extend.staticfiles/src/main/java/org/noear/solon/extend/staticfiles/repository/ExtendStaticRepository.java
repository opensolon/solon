package org.noear.solon.extend.staticfiles.repository;

import org.noear.solon.core.ExtendLoader;

/**
 * 扩展目录型静态仓库
 *
 * @author noear
 * @since 1.5
 */
public class ExtendStaticRepository extends FileStaticRepository {
    public ExtendStaticRepository() {
        super(null);

        String path = ExtendLoader.path();
        if (path == null) {
            throw new RuntimeException("No extension directory exists");
        }

        setDir(path + "static");
    }
}
