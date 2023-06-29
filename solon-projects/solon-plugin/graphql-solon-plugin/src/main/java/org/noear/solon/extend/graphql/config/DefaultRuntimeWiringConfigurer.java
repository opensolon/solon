package org.noear.solon.extend.graphql.config;

import graphql.schema.idl.TypeRuntimeWiring;
import java.util.List;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.event.EventListener;
import org.noear.solon.extend.graphql.execution.collect.RuntimeWiringConfigurerCollect;
import org.noear.solon.extend.graphql.execution.configurer.RuntimeWiringConfigurer;
import org.noear.solon.extend.graphql.execution.fetcher.DataFetcherWrap;
import org.noear.solon.extend.graphql.integration.SchemaMappingBeanExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用于扫描注解生成DataFetcher
 *
 * @author fuzi1996
 * @since 2.3
 */
@Configuration
public class DefaultRuntimeWiringConfigurer implements
        EventListener<RuntimeWiringConfigurerCollect> {

    private static Logger log = LoggerFactory.getLogger(DefaultRuntimeWiringConfigurer.class);

    @Inject
    private SchemaMappingBeanExtractor extractor;

    @Override
    public void onEvent(
            RuntimeWiringConfigurerCollect collect) throws Throwable {
        final List<DataFetcherWrap> wrapList = extractor.getWrapList();
        RuntimeWiringConfigurer configurer = (builder) -> {
            wrapList.forEach(wrap -> {
                String typeName = wrap.getTypeName();
                String fieldName = wrap.getFieldName();
                log.debug("添加 typeName:[{}], fieldName:[{}] DataFetcher", typeName, fieldName);
                builder.type(TypeRuntimeWiring.newTypeWiring(typeName)
                        .dataFetcher(fieldName, wrap.getDataFetcher()));
            });
        };
        log.debug("添加默认的 RuntimeWiringConfigurer 配置器");
        collect.append(configurer);
    }
}
