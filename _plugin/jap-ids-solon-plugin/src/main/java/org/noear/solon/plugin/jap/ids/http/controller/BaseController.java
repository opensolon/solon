package org.noear.solon.plugin.jap.ids.http.controller;

import org.noear.solon.plugin.jap.ids.IdsProps;

/**
 * @author 颖
 */
public abstract class BaseController {

    protected String formatPath(String path) {
        return IdsProps.bastPath + path;
    }

}
