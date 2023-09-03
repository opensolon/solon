package graphql.solon.event;

import graphql.schema.idl.TypeRuntimeWiring;
import java.util.LinkedList;
import java.util.List;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.event.EventListener;
import graphql.solon.annotation.BaseSchemaMappingAnnoHandler;
import graphql.solon.configurer.RuntimeWiringConfigurer;
import graphql.solon.configurer.RuntimeWiringConfigurerCollect;
import graphql.solon.fetcher.DataFetcherWrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用于扫描注解生成DataFetcher
 *
 * @author fuzi1996
 * @since 2.3
 */
@Configuration
public class DefaultRwConfigurerCollectEventListener implements
        EventListener<RuntimeWiringConfigurerCollect> {

    private static Logger log = LoggerFactory
            .getLogger(DefaultRwConfigurerCollectEventListener.class);

    public DefaultRwConfigurerCollectEventListener() {
    }

    @Override
    public void onEvent(
            RuntimeWiringConfigurerCollect collect) throws Throwable {

        AppContext context = Solon.context();
        List<BaseSchemaMappingAnnoHandler> beans = context
                .getBeansOfType(BaseSchemaMappingAnnoHandler.class);

        List<DataFetcherWrap> wrapList = new LinkedList<>();
        for (BaseSchemaMappingAnnoHandler bean : beans) {
            wrapList.addAll(bean.getWrapList());
        }
        
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
