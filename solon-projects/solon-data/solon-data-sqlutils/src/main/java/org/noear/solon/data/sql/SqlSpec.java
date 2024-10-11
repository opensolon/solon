package org.noear.solon.data.sql;

/**
 * Sql 代码申明
 *
 * @author noear
 * @since 3.0
 */
public interface SqlSpec {
    /**
     * 获取代码
     */
    String getSql();

    /**
     * 获取参数
     */
    Object[] getArgs();
}
