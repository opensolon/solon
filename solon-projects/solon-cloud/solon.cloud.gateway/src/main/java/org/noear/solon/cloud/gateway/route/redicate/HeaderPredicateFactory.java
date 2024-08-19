package org.noear.solon.cloud.gateway.route.redicate;

import org.noear.solon.cloud.gateway.exchange.ExContext;
import org.noear.solon.cloud.gateway.exchange.ExPredicate;
import org.noear.solon.cloud.gateway.route.RoutePredicateFactory;
import org.noear.solon.core.route.HeaderRule;

import java.util.Map;

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
        private HeaderRule rule;


        public HeaderPredicate(String config) {
            String[] configs = config.split(",");
            String headerKey = configs[0].trim();
            String regex = configs[1].trim();
            rule = new HeaderRule(headerKey, regex);
        }

        @Override
        public boolean test(ExContext exContext) {
            Map<String, String> headers = exContext.rawHeaders();
            String correspondingKey = rule.getCorrespondingKey();
            for (String key : headers.keySet()) {
                // 如果是对应的key
                if (correspondingKey.equals(key)){
                    // 校验其值是否符合
                    String value = headers.get(key);
                    assert value != null;
                    return rule.test(value);
                }
            }
            return false;
        }
    }
}
