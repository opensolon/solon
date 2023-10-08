package graphql.solon.annotation;

import graphql.schema.DataFetcher;
import graphql.solon.fetcher.DataFetcherWrap;
import graphql.solon.fetcher.SchemaMappingDataFetcher;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.BeanExtractor;
import org.noear.solon.core.BeanWrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author fuzi1996
 * @since 2.3
 */
public abstract class BaseSchemaMappingAnnoHandler<T extends Annotation> implements
        BeanExtractor<T> {

    private static Logger log = LoggerFactory.getLogger(BaseSchemaMappingAnnoHandler.class);

    protected final List<DataFetcherWrap> wrapList;

    protected final AppContext context;

    public BaseSchemaMappingAnnoHandler(AppContext context) {
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

        DataFetcher<Object> dataFetcher = this.getDataFetcher(context, wrap, method);
        DataFetcherWrap fetcherWrap = new DataFetcherWrap(typeName, fieldName, dataFetcher);
        log.debug("扫描到 typeName: [{}],fieldName: [{}] 的 SchemaMappingDataFetcher", typeName,
                fieldName);
        this.wrapList.add(fetcherWrap);
    }

    protected DataFetcher<Object> getDataFetcher(AppContext context, BeanWrap wrap,
            Method method) {
        boolean isBatch = this.getIsBatch();
        return new SchemaMappingDataFetcher(context, wrap, method, isBatch);
    }

    protected boolean getIsBatch() {
        return false;
    }

    abstract String getTypeName(BeanWrap wrap, Method method,
            T schemaMapping);

    abstract String getFieldName(BeanWrap wrap, Method method,
            T schemaMapping);
}
