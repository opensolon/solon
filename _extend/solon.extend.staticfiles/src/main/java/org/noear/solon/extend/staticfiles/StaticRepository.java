package org.noear.solon.extend.staticfiles;

import java.net.URL;

/**
 * 静态仓库
 *
 * @author noear
 * @since 1.5
 */
public interface StaticRepository {
    URL find(String path) throws Exception;
}
