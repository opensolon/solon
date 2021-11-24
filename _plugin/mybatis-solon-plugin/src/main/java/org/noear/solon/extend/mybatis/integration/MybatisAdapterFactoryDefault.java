package org.noear.solon.extend.mybatis.integration;

import org.noear.solon.core.BeanWrap;
import org.noear.solon.extend.mybatis.MybatisAdapter;
import org.noear.solon.extend.mybatis.MybatisAdapterFactory;

import java.util.Properties;

/**
 * Mybatis 适配器工厂默认实现
 *
 * @author noear
 * @since 1.5
 */
public class MybatisAdapterFactoryDefault implements MybatisAdapterFactory {
    @Override
    public MybatisAdapter create(BeanWrap dsWrap) {
        return new MybatisAdapterDefault(dsWrap);
    }

    @Override
    public MybatisAdapter create(BeanWrap dsWrap, Properties props) {
        return new MybatisAdapterDefault(dsWrap, props);
    }
}
