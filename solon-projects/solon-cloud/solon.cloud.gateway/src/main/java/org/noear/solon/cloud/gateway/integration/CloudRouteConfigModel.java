package org.noear.solon.cloud.gateway.integration;

import org.noear.solon.web.reactive.RxFilter;

import java.io.Serializable;
import java.util.List;

/**
 * 分布式路由配置模型
 *
 * @author noear
 * @since 2.9
 */
public class CloudRouteConfigModel implements Serializable {
    private String id;
    private String uri;
    private int stripPrefix = 1;
    private List<String> predicates;
    private List<RxFilter> filters;

    /**
     * 标识
     */
    public String getId() {
        return id;
    }

    /**
     * 地址
     */
    public String getUri() {
        return uri;
    }

    /**
     * 去除前缀段数
     */
    public int getStripPrefix() {
        return stripPrefix;
    }

    /**
     * 断言
     */
    public List<String> getPredicates() {
        return predicates;
    }

    /**
     * 过滤器
     */
    public List<RxFilter> getFilters() {
        return filters;
    }
}