package org.noear.solon.cloud.gateway.integration;

import org.noear.solon.web.reactive.RxFilter;

import java.util.List;

/**
 * 分布式路由配置属性
 *
 * @author noear
 * @since 2.9
 */
public class RouteProperties {
    /**
     * 标识
     */
    private String id;
    /**
     * 地址
     */
    private String uri;
    /**
     * 去除前缀段数
     */
    private int stripPrefix = 1;
    /**
     * 匹配断言
     */
    private List<String> predicates;
    /**
     * 过滤器
     */
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
     * 匹配断言
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