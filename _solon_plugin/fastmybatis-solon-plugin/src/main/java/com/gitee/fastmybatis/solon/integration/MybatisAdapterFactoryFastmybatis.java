package com.gitee.fastmybatis.solon.integration;

import org.apache.ibatis.solon.MybatisAdapter;
import org.apache.ibatis.solon.MybatisAdapterFactory;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.Props;

/**
 * 适配器工厂 for fastmybatis
 *
 * @author thc
 */
public class MybatisAdapterFactoryFastmybatis implements MybatisAdapterFactory {
    @Override
    public MybatisAdapter create(BeanWrap dsWrap) {
        return new MybatisAdapterFastmybatis(dsWrap);
    }

    @Override
    public MybatisAdapter create(BeanWrap dsWrap, Props dsProps) {
        return new MybatisAdapterFastmybatis(dsWrap, dsProps);
    }
}
