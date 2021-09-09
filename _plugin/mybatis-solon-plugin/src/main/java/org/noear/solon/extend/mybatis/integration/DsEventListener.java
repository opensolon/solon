package org.noear.solon.extend.mybatis.integration;

import org.apache.ibatis.plugin.Interceptor;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.event.EventListener;

import javax.sql.DataSource;

/**
 * 数据源Bean事件监听器
 *
 * @author noear
 * @since 1.1
 * */
class DsEventListener implements EventListener<BeanWrap> {
    @Override
    public void onEvent(BeanWrap bw) {
        if (bw.raw() instanceof DataSource) {
            //将数据源bean，注册到会话管理器
            SqlSessionManager.global().reg(bw);
        }

        if (bw.raw() instanceof Interceptor) {
            SqlPlugins.addInterceptor(bw.raw());
        }
    }
}
