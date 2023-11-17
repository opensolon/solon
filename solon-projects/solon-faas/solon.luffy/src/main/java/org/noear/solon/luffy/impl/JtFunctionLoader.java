package org.noear.solon.luffy.impl;

import org.noear.luffy.model.AFileModel;

/**
 * 函数加载器
 *
 * @author noear
 * @since 1.3
 */
public interface JtFunctionLoader {
    AFileModel fileGet(String path) throws Exception;
}
