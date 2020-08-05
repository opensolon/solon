package org.noear.solon.ext;

public interface SupplierEx<T> {
    T get() throws Throwable;
}
