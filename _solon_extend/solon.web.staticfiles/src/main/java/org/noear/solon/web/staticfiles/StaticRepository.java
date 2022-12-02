package org.noear.solon.web.staticfiles;

import java.net.URL;

/**
 * 静态仓库
 *
 * @author noear
 * @since 1.5
 */
public interface StaticRepository {
    /**
     * @param relativePath 例：demo/file.htm （没有'/'开头）
     * */
    URL find(String relativePath) throws Exception;
}
