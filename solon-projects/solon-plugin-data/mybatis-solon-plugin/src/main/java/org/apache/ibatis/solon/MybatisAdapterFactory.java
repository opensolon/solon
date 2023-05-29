package org.apache.ibatis.solon;

import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.Props;

/**
 * 适配器工厂
 *
 * @author noear
 * @since 1.5
 */
public interface MybatisAdapterFactory {
    MybatisAdapter create(BeanWrap dsWrap);
    MybatisAdapter create(BeanWrap dsWrap, Props dsProps);
}
