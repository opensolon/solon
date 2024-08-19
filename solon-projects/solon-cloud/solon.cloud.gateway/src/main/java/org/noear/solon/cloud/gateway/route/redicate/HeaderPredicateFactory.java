package org.noear.solon.cloud.gateway.route.redicate;

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
        private static final Map<String, Pattern> cached = new HashMap<>();
        private String headerKey;
        private Pattern pattern;

        public static Pattern get(String regex) {
            Pattern rst = cached.get(regex);
            if (rst == null) {
                Utils.locker().lock();
                try {
                    rst = cached.get(regex);
                    if (rst == null) {
                        rst = Pattern.compile(regex);
                        cached.put(regex, rst);
                    }
                }finally {
                    Utils.locker().unlock();
                }
            }
            return rst;
        }


        public HeaderPredicate(String config) {
            String[] configs = config.split(",", 2);
            headerKey = configs[0].trim();
            String regex = configs[1].trim();

            pattern = cached.get(regex);
        }

        @Override
        public boolean test(ExContext exContext) {
            String value = exContext.rawHeader(headerKey);
            return pattern.matcher(value).find();
        }
    }
}
