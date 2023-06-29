package org.noear.solon.extend.graphql;

import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.extend.graphql.annotation.SchemaMapping;
import org.noear.solon.extend.graphql.config.DefaultClassPathResourceResolver;
import org.noear.solon.extend.graphql.config.DefaultRuntimeWiringConfigurer;
import org.noear.solon.extend.graphql.config.GraphqlConfiguration;
import org.noear.solon.extend.graphql.controller.GraphqlController;
import org.noear.solon.extend.graphql.integration.SchemaMappingBeanExtractor;
import org.noear.solon.extend.graphql.properties.GraphqlProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author fuzi1996
 * @since 2.3
 */
public class GraphqlPlugin implements Plugin {

    private static Logger log = LoggerFactory.getLogger(GraphqlPlugin.class);

    @Override
    public void start(AopContext context) {
        log.debug("load GraphqlPlugin ...");
        SchemaMappingBeanExtractor extractor = new SchemaMappingBeanExtractor();
        context.beanExtractorAdd(SchemaMapping.class, extractor);

        context.wrapAndPut(SchemaMappingBeanExtractor.class, extractor);

        context.lifecycle(-99, () -> {
            context.beanMake(GraphqlProperties.class);
            context.beanMake(DefaultClassPathResourceResolver.class);
            context.beanMake(DefaultRuntimeWiringConfigurer.class);
            context.beanMake(GraphqlConfiguration.class);
            context.beanMake(GraphqlController.class);
        });
    }
}
