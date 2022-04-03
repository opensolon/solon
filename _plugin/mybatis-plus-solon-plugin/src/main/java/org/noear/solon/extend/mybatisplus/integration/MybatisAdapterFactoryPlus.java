package org.noear.solon.extend.mybatisplus.integration;

import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.Props;
import org.noear.solon.extend.mybatis.MybatisAdapter;
import org.noear.solon.extend.mybatis.MybatisAdapterFactory;

import java.util.Properties;

/**
 * 适配器工厂
 *
 * @author noear, iYarnFog
 * @since 1.5
 */
public class MybatisAdapterFactoryPlus implements MybatisAdapterFactory {
    @Override
    public MybatisAdapter create(BeanWrap dsWrap) {
        return new MybatisAdapterPlus(dsWrap);
    }

    @Override
    public MybatisAdapter create(BeanWrap dsWrap, Props dsProps) {
        return new MybatisAdapterPlus(dsWrap, dsProps);
    }
}
