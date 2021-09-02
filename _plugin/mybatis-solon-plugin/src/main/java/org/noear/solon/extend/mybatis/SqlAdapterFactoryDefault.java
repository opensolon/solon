package org.noear.solon.extend.mybatis;

import org.noear.solon.core.BeanWrap;

import java.util.Properties;

/**
 * 适配器工厂默认实现
 *
 * @author noear
 * @since 1.5
 */
public class SqlAdapterFactoryDefault implements SqlAdapterFactory {
    @Override
    public SqlAdapter create(BeanWrap dsWrap) {
        return new SqlAdapterDefault(dsWrap);
    }

    @Override
    public SqlAdapter create(BeanWrap dsWrap, Properties props) {
        return new SqlAdapterDefault(dsWrap, props);
    }
}
