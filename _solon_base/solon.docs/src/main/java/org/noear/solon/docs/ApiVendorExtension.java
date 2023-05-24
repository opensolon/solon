package org.noear.solon.docs;

import java.io.Serializable;

/**
 * @author noear
 * @since 2.2
 */
public interface ApiVendorExtension<T> extends Serializable {
    String getName();
    T getValue();
}
