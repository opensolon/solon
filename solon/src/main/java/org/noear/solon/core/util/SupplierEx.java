package org.noear.solon.core.util;

/**
 * 供应者
 *
 * @author noear
 * @since 1.0
 * */
public interface SupplierEx<T> {
    T get() throws Throwable;
}
