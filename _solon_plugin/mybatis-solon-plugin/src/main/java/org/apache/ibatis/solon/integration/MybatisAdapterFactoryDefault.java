package org.apache.ibatis.solon.integration;

import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.Props;
import org.apache.ibatis.solon.MybatisAdapter;
import org.apache.ibatis.solon.MybatisAdapterFactory;

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
    public MybatisAdapter create(BeanWrap dsWrap, Props props) {
        return new MybatisAdapterDefault(dsWrap, props);
    }
}
