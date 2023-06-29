package org.noear.solon.extend.graphql.config;

import java.util.List;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.event.EventListener;
import org.noear.solon.extend.graphql.execution.collect.GraphqlResourceResolverCollect;
import org.noear.solon.extend.graphql.execution.resolver.ClassPathResourceResolver;
import org.noear.solon.extend.graphql.properties.GraphqlProperties;
import org.noear.solon.extend.graphql.properties.GraphqlProperties.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用于扫描路径下graphql定义文件
 *
 * @author fuzi1996
 * @since 2.3
 */
@Configuration
public class DefaultClassPathResourceResolver implements
        EventListener<GraphqlResourceResolverCollect> {

    private static Logger log = LoggerFactory.getLogger(DefaultClassPathResourceResolver.class);

    @Inject
    private GraphqlProperties properties;

    @Override
    public void onEvent(
            GraphqlResourceResolverCollect collector) throws Throwable {
        Schema schema = properties.getSchema();

        List<String> locations = schema.getLocations();
        List<String> fileExtensions = schema.getFileExtensions();
        log.debug("添加[ClassPathResourceResolver] locations: {}, fileExtensions: {}", locations,
                fileExtensions);
        collector.append(new ClassPathResourceResolver(locations, fileExtensions));
    }
}
