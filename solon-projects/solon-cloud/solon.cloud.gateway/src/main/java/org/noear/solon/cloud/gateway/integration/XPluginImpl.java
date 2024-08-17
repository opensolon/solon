package org.noear.solon.cloud.gateway.integration;

import org.noear.solon.Solon;
import org.noear.solon.cloud.gateway.CloudGateway;
import org.noear.solon.cloud.gateway.CloudGatewayConfiguration;
import org.noear.solon.cloud.gateway.route.RouteFilter;
import org.noear.solon.cloud.gateway.route.RoutePredicate;
import org.noear.solon.cloud.gateway.route.filter.StripPrefixFilter;
import org.noear.solon.cloud.gateway.route.Route;
import org.noear.solon.cloud.gateway.route.redicate.PathPredicate;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.Props;
import org.noear.solon.core.event.AppLoadEndEvent;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.web.reactive.RxFilter;

import java.net.URI;

/**
 * @author noear
 * @since 2.9
 */
public class XPluginImpl implements Plugin {
    @Override
    public void start(AppContext context) throws Throwable {
        CloudGateway cloudGateway = new CloudGateway();

        context.wrapAndPut(CloudGatewayConfiguration.class, cloudGateway.getConfiguration());

        //加载配置
        loadConfiguration(cloudGateway.getConfiguration(), "solon.cloud.gateway");

        //添加过注解滤器
        context.subWrapsOfType(RxFilter.class, bw -> {
            cloudGateway.getConfiguration().filter(bw.raw(), bw.index());
        });

        //启动完成后注册
        Solon.app().onEvent(AppLoadEndEvent.class, e -> {
            e.app().http("/**", cloudGateway);
        });
    }

    /**
     * 构建分布式网关
     */
    public void loadConfiguration(CloudGatewayConfiguration configuration, String keyStarts) {
        Props props = Solon.cfg().getProp(keyStarts);
        if (props.size() == 0) {
            return;
        }

        GatewayProperties configModel = props.getBean(GatewayProperties.class);

        //routes
        for (RouteProperties rm : configModel.getRoutes()) {
            Route route = new Route();

            route.id(rm.getId());
            route.upstream(URI.create(rm.getUpstream()));
            route.stripPrefix(rm.getStripPrefix());

            if (rm.getPredicates() != null) {
                //route.predicates
                for (String predicateStr : rm.getPredicates()) {
                    route.predicate(buildPredicate(predicateStr));
                }
            }

            if (rm.getFilters() != null) {
                //route.filters
                for (String filterStr : rm.getFilters()) {
                    route.filter(buildFilter(filterStr));
                }
            }

            configuration.route(route);
        }

        //routeHandler
        if (configModel.getRouteHandler() != null) {
            configuration.routeHandler(configModel.getRouteHandler());
        }

        //filters
        for (RxFilter rf : configModel.getFilters()) {
            configuration.filter(rf);
        }
    }

    private RoutePredicate buildPredicate(String predicate) {
        RoutePredicate routePredicate = null;
        int idx = predicate.indexOf('=');

        if (idx > 0) {
            String label = predicate.substring(0, idx);
            String config = predicate.substring(idx + 1, predicate.length());

            if ("Path".equals(label)) {
                routePredicate = new PathPredicate();
            } else {
                if (label.indexOf('.') > 0) {
                    routePredicate = ClassUtil.tryInstance(label);
                }
            }

            if (routePredicate != null) {
                routePredicate.init(config);
            }
        }

        return routePredicate;
    }

    private RouteFilter buildFilter(String filter) {
        RouteFilter routeFilter = null;
        int idx = filter.indexOf('=');

        if (idx > 0) {
            String label = filter.substring(0, idx);
            String config = filter.substring(idx + 1, filter.length());


            if ("StripPrefix".equals(label)) {
                routeFilter = new StripPrefixFilter();
            } else {
                if (label.indexOf('.') > 0) {
                    routeFilter = ClassUtil.tryInstance(label);
                }
            }

            if (routeFilter != null) {
                routeFilter.init(config);
            }
        }

        return routeFilter;
    }
}