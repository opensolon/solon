package graphql.solon.annotation;

import graphql.schema.DataFetcher;
import graphql.solon.execution.BatchLoaderRegistry;
import graphql.solon.fetcher.BatchMappingDataFetcher;
import graphql.solon.fetcher.DataFetcherWrap;
import graphql.solon.util.ClazzUtil;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Callable;
import org.apache.commons.lang3.StringUtils;
import org.noear.solon.Utils;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.BeanWrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author fuzi1996
 * @since 2.3
 */
public class BatchMappingAnnoHandler extends BaseSchemaMappingAnnoHandler<BatchMapping> {

    private static Logger log = LoggerFactory.getLogger(BatchMappingAnnoHandler.class);

    public BatchMappingAnnoHandler(AppContext context) {
        super(context);
    }

    @Override
    public void doExtract(BeanWrap wrap, Method method,
            BatchMapping schemaMapping) throws Throwable {
        String typeName = this.getTypeName(wrap, method, schemaMapping);
        String fieldName = this.getFieldName(wrap, method, schemaMapping);

        if (StringUtils.isBlank(typeName)) {
            Parameter[] parameters = method.getParameters();
            for (Parameter parameter : parameters) {

                if (Collection.class.isAssignableFrom(parameter.getType())) {
                    typeName = ClazzUtil.getGenericReturnClass(parameter.getParameterizedType())
                            .getSimpleName();
                    break;
                }
            }
        }

        DataFetcher<Object> dataFetcher = registerBatchLoader(wrap, method, typeName, fieldName);

        DataFetcherWrap fetcherWrap = new DataFetcherWrap(typeName, fieldName, dataFetcher);
        log.debug("扫描到 typeName: [{}],fieldName: [{}] 的 SchemaMappingDataFetcher", typeName,
                fieldName);
        this.wrapList.add(fetcherWrap);
    }

    private DataFetcher<Object> registerBatchLoader(BeanWrap wrap, Method method, String typeName,
            String fieldName) {
        String dataLoaderKey = String.format("%s.%s", typeName, fieldName);
        BatchLoaderRegistry registry = this.context.getBean(BatchLoaderRegistry.class);

        Class<?> returnType = method.getReturnType();
        Class<?> nestedClass = (returnType.equals(Callable.class) ? ClazzUtil.getGenericReturnClass(
                method.getGenericReturnType()) : returnType);
        BatchMappingDataFetcher result = new BatchMappingDataFetcher(this.context,
                wrap, method, true, dataLoaderKey);

        if (returnType.equals(Flux.class) || Collection.class.isAssignableFrom(nestedClass)) {
            registry.forName(dataLoaderKey).registerBatchLoader(result::invokeForIterable);
        } else if (returnType.equals(Mono.class) || nestedClass.equals(Map.class)) {
            registry.forName(dataLoaderKey).registerMappedBatchLoader(result::invokeForMap);
        } else {
            throw new IllegalStateException("@BatchMapping method is expected to return " +
                    "Flux<V>, List<V>, Mono<Map<K, V>>, or Map<K, V>: " + result);
        }

        return result;
    }

    @Override
    String getTypeName(BeanWrap wrap, Method method,
            BatchMapping schemaMapping) {
        return schemaMapping.typeName();
    }

    @Override
    String getFieldName(BeanWrap wrap, Method method,
            BatchMapping schemaMapping) {
        String fieldName = Utils.annoAlias(schemaMapping.field(), schemaMapping.value());

        if (StringUtils.isBlank(fieldName)) {
            // 注解没标就使用方法名
            fieldName = method.getName();
        }
        return fieldName;
    }

    @Override
    protected boolean getIsBatch() {
        return true;
    }

}
