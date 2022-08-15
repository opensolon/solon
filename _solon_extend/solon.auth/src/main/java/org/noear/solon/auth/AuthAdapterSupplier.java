package org.noear.solon.auth;

import org.noear.solon.core.handle.Context;

/**
 * @author noear
 * @since 1.10
 */
public interface AuthAdapterSupplier {
    AuthAdapter get(Context ctx);
}
