package org.noear.solon.cloud.opentracing.service;

import io.opentracing.Tracer;
import org.noear.solon.cloud.CloudProps;

/**
 * @author noear
 * @since 1.7
 */
public interface TracerFactoryService {
    Tracer create() throws Exception;
}
