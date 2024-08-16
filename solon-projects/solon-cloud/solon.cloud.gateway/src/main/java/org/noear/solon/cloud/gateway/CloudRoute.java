package org.noear.solon.cloud.gateway;

import org.noear.solon.cloud.gateway.redicate.PathPredicate;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.util.RankEntity;
import org.noear.solon.web.reactive.RxFilter;

import java.net.URI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

/**
 * 分布式路由记录
 *
 * @author noear
 * @since 2.9
 */
public class CloudRoute {
    private String id;
    private URI uri;
    private int stripPrefix;
    private List<Predicate<Context>> predicates = new ArrayList<>();
    private List<RankEntity<RxFilter>> filters = new ArrayList<>();

    public CloudRoute id(String id) {
        this.id = id;
        return this;
    }

    public CloudRoute uri(URI uri) {
        this.uri = uri;
        return this;
    }

    public CloudRoute stripPrefix(int stripPrefix) {
        this.stripPrefix = stripPrefix;
        return this;
    }

    public CloudRoute filterAdd(RxFilter filter) {
        return filterAdd(filter, 0);
    }

    public CloudRoute filterAdd(RxFilter filter, int index) {
        this.filters.add(new RankEntity<>(filter, index));
        this.filters.sort(Comparator.comparingInt(e -> e.index));
        return this;
    }

    public CloudRoute predicateAdd(Predicate<Context> predicate) {
        this.predicates.add(predicate);
        return this;
    }

    public CloudRoute path(String path) {
        return predicateAdd(new PathPredicate(path));
    }

    /**
     * 匹配
     */
    public boolean matched(Context ctx) {
        if (predicates.size() == 0) {
            return false;
        } else {
            for (Predicate<Context> p : predicates) {
                if (p.test(ctx) == false) {
                    return false;
                }
            }

            return true;
        }
    }

    /**
     * 标识
     */
    public String getId() {
        return id;
    }

    /**
     * 地址
     */
    public URI getUri() {
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
    public List<Predicate<Context>> getPredicates() {
        return predicates;
    }

    /**
     * 过滤器
     */
    public List<RankEntity<RxFilter>> getFilters() {
        return filters;
    }
}