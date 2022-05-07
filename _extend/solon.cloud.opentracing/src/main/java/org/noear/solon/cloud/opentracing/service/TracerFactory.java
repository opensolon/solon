package org.noear.solon.cloud.opentracing.service;

import io.opentracing.Tracer;

/**
 * 跟踪器工厂服务
 *
 * @author noear
 * @since 1.7
 */
public interface TracerFactory {
    Tracer create() throws Exception;
}
