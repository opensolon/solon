package org.noear.solon.extend.graphql.integration;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.noear.solon.core.BeanExtractor;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.extend.graphql.annotation.SchemaMapping;
import org.noear.solon.extend.graphql.execution.fetcher.DataFetcherWrap;
import org.noear.solon.extend.graphql.execution.fetcher.SchemaMappingDataFetcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author fuzi1996
 * @since 2.3
 */
public class SchemaMappingBeanExtractor implements BeanExtractor<SchemaMapping> {

    private static Logger log = LoggerFactory.getLogger(SchemaMappingBeanExtractor.class);

    private final List<DataFetcherWrap> wrapList;

    public SchemaMappingBeanExtractor() {
        this.wrapList = new LinkedList<>();
    }

    public List<DataFetcherWrap> getWrapList() {
        return wrapList;
    }

    @Override
    public void doExtract(BeanWrap wrap, Method method,
            SchemaMapping schemaMapping) throws Throwable {
        String typeName = schemaMapping.typeName();
        String fieldName = StringUtils.isNotBlank(schemaMapping.value()) ? schemaMapping.value()
                : schemaMapping.field();

        DataFetcherWrap fetcherWrap = new DataFetcherWrap(typeName, fieldName,
                new SchemaMappingDataFetcher());
        log.debug("扫描到 typeName: [],fieldName: [] 的 SchemaMappingDataFetcher");
        this.wrapList.add(fetcherWrap);
    }

}
