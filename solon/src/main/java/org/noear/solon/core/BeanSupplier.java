package org.noear.solon.core;

/**
 * Bean 提供者
 *
 * @author noear
 * @since 2.7
 */
@FunctionalInterface
public interface BeanSupplier {
    Object get();
}
