package org.noear.solon.extend.graphql.config;

import graphql.schema.idl.TypeRuntimeWiring;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.extend.graphql.execution.GraphQlSource;
import org.noear.solon.extend.graphql.execution.GraphQlSourceGenerater;
import org.noear.solon.extend.graphql.execution.GraphqlResourceResolver;
import org.noear.solon.extend.graphql.execution.RuntimeWiringConfigurer;
import org.noear.solon.extend.graphql.execution.fetcher.DataFetcherWrap;
import org.noear.solon.extend.graphql.execution.resolver.ClassPathResourceResolver;
import org.noear.solon.extend.graphql.integration.SchemaMappingBeanExtractor;
import org.noear.solon.extend.graphql.properties.GraphqlProperties;
import org.noear.solon.extend.graphql.properties.GraphqlProperties.Schema;
import org.noear.solon.extend.graphql.support.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author fuzi1996
 * @since 2.3
 */
@Configuration
public class GraphqlConfiguration {

    private static Logger log = LoggerFactory.getLogger(GraphqlConfiguration.class);

    public GraphqlConfiguration() {
    }

    /**
     * 用于扫描路径下graphql定义文件
     */
    @Bean
    public ClassPathResourceResolver classPathResourceResolver(
            @Inject GraphqlProperties properties) {
        Schema schema = properties.getSchema();

        List<String> locations = schema.getLocations();
        List<String> fileExtensions = schema.getFileExtensions();
        log.debug("添加[ClassPathResourceResolver] locations: {}, fileExtensions: {}", locations,
                fileExtensions);
        return new ClassPathResourceResolver(locations, fileExtensions);
    }

    /**
     * 用于扫描注解生成DataFetcher
     */
    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer(
            @Inject SchemaMappingBeanExtractor extractor) {
        final List<DataFetcherWrap> wrapList = extractor.getWrapList();
        return (builder) -> {
            wrapList.forEach(wrap -> {
                String typeName = wrap.getTypeName();
                String fieldName = wrap.getFieldName();
                log.debug("添加 typeName:[{}], fieldName:[{}] DataFetcher", typeName, fieldName);
                builder.type(TypeRuntimeWiring.newTypeWiring(typeName)
                        .dataFetcher(fieldName, wrap.getDataFetcher()));
            });
        };
    }

    /**
     * 注入GraphQlSource
     */
    @Bean
    @Condition(onClass = ClassPathResourceResolver.class)
    public GraphQlSource defaultGraphqlSource() {

        List<GraphqlResourceResolver> resolvers = Objects.requireNonNull(Solon.context())
                .getBeansOfType(GraphqlResourceResolver.class);

        Set<Resource> resources = new LinkedHashSet<>();

        if (Objects.nonNull(resolvers)) {
            resolvers.forEach(resolver -> {
                if (resolver.isNeedAppend(resources)) {
                    Set<Resource> otherResources = resolver.getGraphqlResource();
                    resources.addAll(otherResources);
                }
            });
        }

        List<RuntimeWiringConfigurer> configurers = Objects.requireNonNull(Solon.context())
                .getBeansOfType(RuntimeWiringConfigurer.class);
        log.debug("注册默认的 GraphQlSource");
        return GraphQlSourceGenerater.generateGraphqlSource(resources, configurers);
    }
}
