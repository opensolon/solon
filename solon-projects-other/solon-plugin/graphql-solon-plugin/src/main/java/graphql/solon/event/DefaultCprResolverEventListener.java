package graphql.solon.event;

import java.util.List;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.event.EventListener;
import graphql.solon.properties.GraphqlProperties;
import graphql.solon.properties.GraphqlProperties.Schema;
import graphql.solon.resolver.resource.ClassPathResourceResolver;
import graphql.solon.resolver.resource.GraphqlResourceResolverCollect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用于扫描路径下graphql定义文件
 *
 * @author fuzi1996
 * @since 2.3
 */
@Configuration
public class DefaultCprResolverEventListener implements
        EventListener<GraphqlResourceResolverCollect> {

    private static Logger log = LoggerFactory.getLogger(
            DefaultCprResolverEventListener.class);

    @Inject
    private GraphqlProperties properties;

    public DefaultCprResolverEventListener() {
    }

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
