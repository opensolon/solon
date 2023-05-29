package org.noear.solon.auth;

/**
 * 适配器提供者（可以通过路径前缀限制效果）
 *
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
