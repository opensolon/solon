package com.mybatisflex.solon.integration;

import org.apache.ibatis.solon.MybatisAdapter;
import org.apache.ibatis.solon.MybatisAdapterFactory;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.Props;

/**
 * MyBatis-Flex 适配器工厂。
 *
 * @author noear
 * @since 2.2
 */
public class MybatisAdapterFactoryFlex implements MybatisAdapterFactory {

    @Override
    public MybatisAdapter create(BeanWrap dsWrap) {
        return new MybatisAdapterFlex(dsWrap);
    }

    @Override
    public MybatisAdapter create(BeanWrap dsWrap, Props dsProps) {
        return new MybatisAdapterFlex(dsWrap, dsProps);
    }

}
