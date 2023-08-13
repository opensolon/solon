package graphql.solon.resolver.argument;

import graphql.solon.collect.AbstractBaseCollector;

/**
 * @author fuzi1996
 * @since 2.3
 */
public class HandlerMethodArgumentResolverCollect extends
        AbstractBaseCollector<HandlerMethodArgumentResolver> {

    public HandlerMethodArgumentResolverCollect() {
        super();
        this.append(new ArgumentMethodArgumentResolver());
        this.append(new SourceMethodArgumentResolver());
    }

}
