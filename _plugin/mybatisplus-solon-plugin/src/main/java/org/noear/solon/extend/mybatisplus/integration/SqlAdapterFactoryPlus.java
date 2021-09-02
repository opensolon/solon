package org.noear.solon.extend.mybatisplus.integration;

import org.noear.solon.core.BeanWrap;
import org.noear.solon.extend.mybatis.SqlAdapter;
import org.noear.solon.extend.mybatis.SqlAdapterFactory;

import java.util.Properties;

/**
 * 适配器工厂
 *
 * @author noear, iYarnFog
 * @since 1.5
 */
public class SqlAdapterFactoryPlus implements SqlAdapterFactory {
    @Override
    public SqlAdapter create(BeanWrap dsWrap) {
        return new SqlAdapterPlus(dsWrap);
    }

    @Override
    public SqlAdapter create(BeanWrap dsWrap, Properties props) {
        return new SqlAdapterPlus(dsWrap, props);
    }
}
