package org.noear.solon.extend.graphql.resolver.argument;

import org.noear.solon.extend.graphql.collect.AbstractBaseCollector;

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
