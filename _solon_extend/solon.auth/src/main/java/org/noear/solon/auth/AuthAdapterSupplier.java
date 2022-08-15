package org.noear.solon.auth;

/**
 * @author noear
 * @since 1.10
 */
public interface AuthAdapterSupplier {
    /**
     * 路径前缀
     * */
    String pathPrefix();
    /**
     * 适配器
     * */
    AuthAdapter adapter();
}
