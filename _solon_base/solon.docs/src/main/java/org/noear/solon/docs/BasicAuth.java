package org.noear.solon.docs;

/**
 * @author noear
 * @since 2.3
 */
public interface BasicAuth {
    /**
     * 是否启用
     */
    boolean isEnable();

    /**
     * 用户
     */
    String getUsername();

    /**
     * 密码
     */
    String getPassword();
}
