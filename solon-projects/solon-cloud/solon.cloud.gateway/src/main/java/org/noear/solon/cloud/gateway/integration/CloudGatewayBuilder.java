package org.noear.solon.cloud.gateway.integration;

import org.noear.solon.cloud.gateway.CloudGateway;
import org.noear.solon.cloud.gateway.CloudRoute;
import org.noear.solon.core.AppContext;
import org.noear.solon.web.reactive.RxFilter;

import java.net.URI;

/**
 * 分布式网关构造器
 *
 * @author noear
 * @since 2.9
 */
public class CloudGatewayBuilder {
    /**
     * 构建
     */
    public static CloudGateway build(AppContext context, String keyStarts) {
        CloudGatewayConfigModel configModel = context.cfg().getBean(keyStarts, CloudGatewayConfigModel.class);

        if (configModel == null) {
            return null;
        }

        if (configModel.getRoutes().size() == 0) {
            return null;
        } else {
            CloudGateway cloudGateway = new CloudGateway();

            //routes
            for (CloudRouteConfigModel rm : configModel.getRoutes()) {
                CloudRoute route = new CloudRoute();

                route.id(rm.getId());
                route.uri(URI.create(rm.getUri()));
                route.stripPrefix(rm.getStripPrefix());

                if (rm.getPredicates() != null) {
                    for (String predicate : rm.getPredicates()) {
                        int idx = predicate.indexOf('=');
                        if (idx > 0) {
                            String label = predicate.substring(0, idx);
                            String value = predicate.substring(idx + 1, predicate.length());

                            if ("PATH".equals(label)) {
                                route.path(value);
                            }
                        }
                    }
                }

                if (rm.getFilters() != null) {
                    for (RxFilter rf : rm.getFilters()) {
                        route.filterAdd(rf);
                    }
                }

                cloudGateway.route(route);
            }

            //filters
            for (RxFilter rf : configModel.getFilters()) {
                cloudGateway.filter(rf);
            }

            context.subWrapsOfType(RxFilter.class, bw -> {
                cloudGateway.filter(bw.raw(), bw.index());
            });

            return cloudGateway;
        }
    }
}