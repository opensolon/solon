package org.noear.solon.extend.graphql.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.BeanExtractor;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.extend.graphql.fetcher.DataFetcherWrap;
import org.noear.solon.extend.graphql.fetcher.QueryMappingDataFetcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author fuzi1996
 * @since 2.3
 */
public abstract class BaseSchemaMappingAnnoHandler<T extends Annotation> implements
        BeanExtractor<T> {

    private static Logger log = LoggerFactory.getLogger(BaseSchemaMappingAnnoHandler.class);

    private final List<DataFetcherWrap> wrapList;

    private final AopContext context;

    public BaseSchemaMappingAnnoHandler(AopContext context) {
        this.wrapList = new LinkedList<>();
        this.context = context;
    }

    public List<DataFetcherWrap> getWrapList() {
        return wrapList;
    }


    @Override
    public void doExtract(BeanWrap wrap, Method method,
            T schemaMapping) throws Throwable {
        String typeName = this.getTypeName(wrap, method, schemaMapping);
        String fieldName = this.getFieldName(wrap, method, schemaMapping);

        DataFetcherWrap fetcherWrap = new DataFetcherWrap(typeName, fieldName,
                new QueryMappingDataFetcher(context, wrap, method));
        log.debug("扫描到 typeName: [{}],fieldName: [{}] 的 QueryMappingDataFetcher", typeName,
                fieldName);
        this.wrapList.add(fetcherWrap);
    }

    abstract String getTypeName(BeanWrap wrap, Method method,
            T schemaMapping);

    abstract String getFieldName(BeanWrap wrap, Method method,
            T schemaMapping);
}
