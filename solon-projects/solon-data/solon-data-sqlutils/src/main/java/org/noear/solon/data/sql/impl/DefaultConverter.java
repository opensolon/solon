package org.noear.solon.data.sql.impl;

import org.noear.snack.ONode;
import org.noear.solon.Solon;
import org.noear.solon.data.sql.Row;

import java.sql.SQLException;

/**
 * @author noear
 * @since 3.0
 */
public class DefaultConverter implements Row.Converter {
    private static Row.Converter instance = new DefaultConverter();

    static {
        if (Solon.app() != null) {
            Solon.context().getBeanAsync(Row.Converter.class, bean -> {
                instance = bean;
            });
        }
    }

    /**
     * 获取实例
     */
    public static Row.Converter getInstance() {
        return instance;
    }

    /**
     * 设置实例
     */
    public static void setInstance(Row.Converter instance) {
        if (instance != null) {
            DefaultConverter.instance = instance;
        }
    }

    /**
     * 转换
     */
    @Override
    public Object convert(Row row, Class<?> type) throws SQLException {
        return ONode.load(row.toMap()).toObject(type);
    }
}
