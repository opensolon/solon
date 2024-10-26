package org.noear.solon.data.sqlink.base.expression;


import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.metaData.MetaData;
import org.noear.solon.data.sqlink.base.metaData.MetaDataCache;

public interface ISqlFromExpression extends ISqlExpression
{
    ISqlTableExpression getSqlTableExpression();

    default boolean isEmptyTable()
    {
        Class<?> tableClass = getSqlTableExpression().getTableClass();
        MetaData metaData = MetaDataCache.getMetaData(tableClass);
        return metaData.isEmptyTable();
    }

    int getIndex();

    @Override
    default ISqlFromExpression copy(IConfig config)
    {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        return factory.from(getSqlTableExpression().copy(config), getIndex());
    }
}
