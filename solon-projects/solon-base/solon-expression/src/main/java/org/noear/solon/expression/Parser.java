package org.noear.solon.expression;

/**
 * 解析器
 *
 * @author noear
 * @since 3.1
 * */
public interface Parser<T> {
    /**
     * 解析
     *
     * @param expr   表达式
     * @param cached 是否缓存
     */
    Expression<T> parse(String expr, boolean cached);

    /**
     * 解析（带缓存）
     *
     * @param expr 表达式
     */
    default Expression<T> parse(String expr) {
        return parse(expr, true);
    }
}