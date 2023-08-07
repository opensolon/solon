package org.noear.solon.docs.models;

import java.io.Serializable;

/**
 * 接口供应商扩展
 *
 * @author noear
 * @since 2.2
 */
public interface ApiVendorExtension<T> extends Serializable {
    String getName();
    T getValue();
}
