package org.noear.solon.cloud.service;

import org.noear.solon.Solon;

/**
 * @author noear
 * @since 1.3
 */
@FunctionalInterface
public interface CloudIdServiceFactory {
    CloudIdService create(String group, String service);


    default CloudIdService create(){
        return create(Solon.cfg().appGroup(), Solon.cfg().appName());
    }
}
