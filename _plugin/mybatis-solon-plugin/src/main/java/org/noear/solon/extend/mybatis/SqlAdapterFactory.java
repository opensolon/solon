package org.noear.solon.extend.mybatis;

import org.noear.solon.core.BeanWrap;

import java.util.Properties;

/**
 * 适配器工厂
 *
 * @author noear
 * @since 1.5
 */
public interface SqlAdapterFactory {
    SqlAdapter create(BeanWrap dsWrap);
    SqlAdapter create(BeanWrap dsWrap, Properties props);
}
