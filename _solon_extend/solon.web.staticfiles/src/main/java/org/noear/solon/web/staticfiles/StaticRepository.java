package org.noear.solon.web.staticfiles;

import org.noear.solon.Utils;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * 静态仓库
 *
 * @author noear
 * @since 1.5
 */
public interface StaticRepository {
    /**
     * 查找
     *
     * @param relativePath 例：demo/file.htm （没有'/'开头）
     */
    URL find(String relativePath) throws Exception;

    /**
     * 预热
     *
     * @param relativePath 例：demo/file.htm （没有'/'开头）
     */
    default void preheat(String relativePath, boolean useCaches) throws Exception {
        URL url = find(relativePath);

        if (url != null) {
            URLConnection connection = url.openConnection();
            connection.setUseCaches(useCaches);
            try (InputStream stream = connection.getInputStream()) {
                Utils.transferToString(stream);
            }
        }
    }
}
