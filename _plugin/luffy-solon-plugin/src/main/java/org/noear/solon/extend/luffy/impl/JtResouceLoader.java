package org.noear.solon.extend.luffy.impl;

import org.noear.luffy.model.AFileModel;

/**
 * 资源加载器
 *
 * @author noear
 * @since 1.3
 */
public interface JtResouceLoader {
    AFileModel fileGet(String path) throws Exception;
}
