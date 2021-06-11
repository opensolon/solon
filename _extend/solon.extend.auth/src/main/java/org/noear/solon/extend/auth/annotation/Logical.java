package org.noear.solon.extend.auth.annotation;

/**
 * 逻辑关系
 *
 * @author noear
 * @since 1.4
 */
public enum Logical {
    AND,
    OR;

    public static Logical of(String name) {
        if ("AND".equals(name)) {
            return AND;
        } else {
            return OR;
        }
    }
}