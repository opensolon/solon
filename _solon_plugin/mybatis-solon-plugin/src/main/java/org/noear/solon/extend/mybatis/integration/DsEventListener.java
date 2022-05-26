package org.noear.solon.extend.mybatis.integration;

import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.event.EventListener;

import javax.sql.DataSource;

/**
 * 数据源监听器（监听：DataSource bean）
 *
 * @author noear
 * @since 1.1
 * */
class DsEventListener implements EventListener<BeanWrap> {
    @Override
    public void onEvent(BeanWrap bw) {
        if (bw.raw() instanceof DataSource) {
            //将数据源bean，注册到会话管理器
            MybatisAdapterManager.register(bw);
        }
    }
}
