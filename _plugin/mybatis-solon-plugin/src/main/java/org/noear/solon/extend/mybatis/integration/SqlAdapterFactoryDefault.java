package org.noear.solon.extend.mybatis.integration;

import org.noear.solon.core.BeanWrap;
import org.noear.solon.extend.mybatis.SqlAdapter;
import org.noear.solon.extend.mybatis.SqlAdapterFactory;

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
