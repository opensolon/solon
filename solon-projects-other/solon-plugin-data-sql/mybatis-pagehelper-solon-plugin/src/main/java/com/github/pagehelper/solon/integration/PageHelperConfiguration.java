package com.github.pagehelper.solon.integration;

import com.github.pagehelper.PageInterceptor;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.Configuration;
import org.noear.solon.annotation.Component;
import org.noear.solon.core.event.EventListener;

/**
 * PageHelper 分布插件配置器（添加拦截器）
 *
 * @author noear
 * @since 1.5
 */
@Component
public class PageHelperConfiguration implements EventListener<Configuration> {

    private PageHelperProperties pageProperties;
    private PageInterceptor pageInterceptor;

    public PageHelperConfiguration() {
        pageProperties = new PageHelperProperties();
        pageInterceptor = new PageInterceptor();
        pageInterceptor.setProperties(pageProperties.getProperties());
    }

    @Override
    public void onEvent(Configuration configuration) {
        if (!containsInterceptor(configuration, pageInterceptor)) {
            configuration.addInterceptor(pageInterceptor);
        }
    }


    /**
     * 是否已经存在相同的拦截器
     */
    private boolean containsInterceptor(org.apache.ibatis.session.Configuration configuration, Interceptor interceptor) {
        try {
            // getInterceptors since 3.2.2
            return configuration.getInterceptors().contains(interceptor);
        } catch (Exception e) {
            return false;
        }
    }
}
