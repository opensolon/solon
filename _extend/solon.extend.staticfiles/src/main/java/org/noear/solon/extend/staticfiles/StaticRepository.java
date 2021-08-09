package org.noear.solon.extend.staticfiles;

import java.net.URL;

/**
 * 资源工厂
 *
 * @author noear
 * @since 1.5
 */
public interface StaticRepository {
    URL find(String path) throws Exception;
}
