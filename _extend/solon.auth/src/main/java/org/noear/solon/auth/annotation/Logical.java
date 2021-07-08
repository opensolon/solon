package org.noear.solon.auth.annotation;

/**
 * 认证的逻辑关系
 *
 * @author noear
 * @since 1.4
 */
public enum Logical {
    /**
     * 关且关系
     * */
    AND,
    /**
     * 或者关系
     * */
    OR;

    public static Logical of(String name) {
        if ("AND".equals(name)) {
            return AND;
        } else {
            return OR;
        }
    }
}