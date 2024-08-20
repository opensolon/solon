package org.noear.solon.cloud.gateway.route.predicate;

import org.noear.solon.Utils;
import org.noear.solon.cloud.gateway.exchange.ExContext;
import org.noear.solon.cloud.gateway.exchange.ExPredicate;
import org.noear.solon.cloud.gateway.route.RoutePredicateFactory;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;


/**
 * @author wjc28
 * @version 1.0
 * @description: 路由请求头匹配
 * @date 2024-08-19
 */
public class HeaderPredicateFactory implements RoutePredicateFactory {
    @Override
    public String prefix() {
        return "Header";
    }


    @Override
    public ExPredicate create(String config) {
        HeaderPredicate headerPredicate = new HeaderPredicate(config);
        return headerPredicate;
    }

    private static class HeaderPredicate implements ExPredicate {
        private String headerKey;
        private Pattern pattern;



        public HeaderPredicate(String config) {
            if (Utils.isEmpty(config)) {
                throw new IllegalArgumentException("Header config is null or empty");
            }
            String[] configs = config.split(",", 2);
            headerKey = configs[0].trim();

            if (Utils.isEmpty(headerKey)){
                throw new IllegalArgumentException("header Key is null or empty");
            }
            if (configs.length > 1) {
                String regex = configs[1].trim();
                if (Utils.isEmpty(regex)){
                    throw new IllegalArgumentException("Header regex is null or empty");
                }
                pattern = Pattern.compile(regex);
            }

        }

        @Override
        public boolean test(ExContext exContext) {
            String value = exContext.rawHeader(headerKey);
            if (Utils.isEmpty(value)) {
                return false;
            }
            if (pattern == null){
                return true;
            }
            return pattern.matcher(value).find();
        }
    }
}
