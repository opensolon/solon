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
public class SchemaMappingAnnoHandler extends BaseSchemaMappingAnnoHandler<SchemaMapping> {

    private static Logger log = LoggerFactory.getLogger(SchemaMappingAnnoHandler.class);

    public SchemaMappingAnnoHandler(AppContext context) {
        super(context);
    }

    @Override
    String getTypeName(BeanWrap wrap, Method method,
            SchemaMapping schemaMapping) {
        return schemaMapping.typeName();
    }

    @Override
    String getFieldName(BeanWrap wrap, Method method,
            SchemaMapping schemaMapping) {
        String fieldName = Utils.annoAlias(schemaMapping.field(), schemaMapping.value());

        if (StringUtils.isBlank(fieldName)) {
            // 注解没标就使用方法名
            fieldName = method.getName();
        }
        return fieldName;
    }

}
