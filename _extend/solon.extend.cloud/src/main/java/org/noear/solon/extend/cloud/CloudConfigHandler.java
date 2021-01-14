package org.noear.solon.extend.cloud;

import org.noear.solon.extend.cloud.model.ConfigSet;

/**
 * @author noear 2021/1/14 created
 */
@FunctionalInterface
public interface CloudConfigHandler {
    void handler(ConfigSet configSet);
}
