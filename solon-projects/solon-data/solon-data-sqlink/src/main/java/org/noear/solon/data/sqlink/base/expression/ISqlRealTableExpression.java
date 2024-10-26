package org.noear.solon.data.sqlink.base.expression;

import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.IDialect;
import org.noear.solon.data.sqlink.base.metaData.MetaData;
import org.noear.solon.data.sqlink.base.metaData.MetaDataCache;

import java.util.List;

public interface ISqlRealTableExpression extends ISqlTableExpression
{
    @Override
    default String getSqlAndValue(IConfig config, List<Object> values)
    {
        String fullName = "";
        MetaData metaData = MetaDataCache.getMetaData(getTableClass());
        IDialect dbConfig = config.getDisambiguation();
        String schema = metaData.getSchema();
        if (!schema.isEmpty())
        {
            fullName += dbConfig.disambiguationTableName(schema) + ".";
        }
        fullName += dbConfig.disambiguationTableName(metaData.getTableName());
        return fullName;
    }

    @Override
    default ISqlRealTableExpression copy(IConfig config)
    {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        return factory.table(getTableClass());
    }
}
