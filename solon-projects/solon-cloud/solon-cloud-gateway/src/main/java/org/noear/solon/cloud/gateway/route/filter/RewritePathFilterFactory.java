package org.noear.solon.cloud.gateway.route.filter;

import org.noear.solon.Utils;
import org.noear.solon.cloud.gateway.exchange.ExContext;
import org.noear.solon.cloud.gateway.exchange.ExFilter;
import org.noear.solon.cloud.gateway.exchange.ExFilterChain;
import org.noear.solon.cloud.gateway.route.RouteFilterFactory;
import org.noear.solon.rx.Completable;

import java.util.regex.Pattern;

/**
 * 重写路径路由过滤器
 * 当配置信息为 RewritePath=/red/(?<segment>.*), /$\{segment}时，
 * 该处理器会将路径进行如下替换   /red/(?<segment>.*) => /$\{segment}
 * 例:
 * http://ip:port/red/hello => http://ip2:port2/hello
 *
 * @author TuZhe
 * @since 2.9
 **/
public class RewritePathFilterFactory implements RouteFilterFactory {

    @Override
    public String prefix() {
        return "RewritePath"; //魔法值, 不建议
    }

    @Override
    public ExFilter create(String config) {
        return new RewritePathFilter (config);
    }

    public static class RewritePathFilter implements ExFilter {
        private final String replacement;
        private final Pattern pattern;

        // if [RewritePath=/red/hello, /hello] then config = [/red/hello, /hello]]
        public RewritePathFilter(String config) {
            if(Utils.isBlank (config)) {
                throw new IllegalArgumentException ("RewritePathFilter config cannot be blank");
            }

            String[] parts = config.split (","); //应该统一符号常量
            if(parts.length != 2) {
                throw new IllegalArgumentException ("RewritePathFilter config is wrong: " + config);
                //throw new IllegalArgumentException ("RewritePath config is incorrect, such as /red/(?<segment>.*), /$\\{segment} ");
            }
            String regex = parts[0].trim ();
            String rawReplacement = parts[1].trim ();

            if(!regex.startsWith ("/") || !rawReplacement.startsWith ("/")) {
                throw new IllegalArgumentException ("RewritePathFilter config is wrong, path must be start with slash, config is : " + config);
            }
            pattern = Pattern.compile (regex);
            replacement = rawReplacement.replace ("$\\", "$");
        }

        @Override
        public Completable doFilter(ExContext ctx, ExFilterChain chain) {
            //此处 newRequest()  并非 new XXX()！！！
            String path = ctx.newRequest ().getPath ();
            //TODO 这里需要优化，因为每次都要匹配，考虑使用缓存，
            // 但针对于 /path/{PathVariable}路径，后续会无限膨胀，占用内存的同时，命中率极低
            String newPath = pattern.matcher (path).replaceAll (replacement);
            ctx.newRequest ().path (newPath);
            return chain.doFilter (ctx);
        }
    }

}
