package org.noear.solon.extend.graphql.annotation;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.noear.solon.Utils;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.BeanExtractor;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.extend.graphql.execution.fetcher.DataFetcherWrap;
import org.noear.solon.extend.graphql.execution.fetcher.QueryMappingDataFetcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author fuzi1996
 * @since 2.3
 */
public class QueryMappingAnnoHandler implements BeanExtractor<QueryMapping> {

    private static Logger log = LoggerFactory.getLogger(QueryMappingAnnoHandler.class);

    private final List<DataFetcherWrap> wrapList;

    private final AopContext context;

    public QueryMappingAnnoHandler(AopContext context) {
        this.wrapList = new LinkedList<>();
        this.context = context;
    }

    public List<DataFetcherWrap> getWrapList() {
        return wrapList;
    }

    @Override
    public void doExtract(BeanWrap wrap, Method method,
            QueryMapping schemaMapping) throws Throwable {
        String typeName = schemaMapping.typeName();
        String fieldName = Utils.annoAlias(schemaMapping.field(), schemaMapping.value());

        if (StringUtils.isBlank(fieldName)) {
            // 注解没标就使用方法名
            fieldName = method.getName();
        }

        DataFetcherWrap fetcherWrap = new DataFetcherWrap(typeName, fieldName,
                new QueryMappingDataFetcher(context, wrap, method));
        log.debug("扫描到 typeName: [{}],fieldName: [{}] 的 QueryMappingDataFetcher", typeName,
                fieldName);
        this.wrapList.add(fetcherWrap);
    }

}
