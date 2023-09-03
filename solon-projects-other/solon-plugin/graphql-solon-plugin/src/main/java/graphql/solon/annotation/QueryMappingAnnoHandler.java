package graphql.solon.annotation;

import java.lang.reflect.Method;
import org.apache.commons.lang3.StringUtils;
import org.noear.solon.Utils;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.BeanWrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author fuzi1996
 * @since 2.3
 */
public class QueryMappingAnnoHandler extends BaseSchemaMappingAnnoHandler<QueryMapping> {

    private static Logger log = LoggerFactory.getLogger(QueryMappingAnnoHandler.class);

    public QueryMappingAnnoHandler(AppContext context) {
        super(context);
    }

    @Override
    String getTypeName(BeanWrap wrap, Method method,
            QueryMapping schemaMapping) {
        return schemaMapping.typeName();
    }

    @Override
    String getFieldName(BeanWrap wrap, Method method,
            QueryMapping schemaMapping) {
        String fieldName = Utils.annoAlias(schemaMapping.field(), schemaMapping.value());

        if (StringUtils.isBlank(fieldName)) {
            // 注解没标就使用方法名
            fieldName = method.getName();
        }
        return fieldName;
    }

}
