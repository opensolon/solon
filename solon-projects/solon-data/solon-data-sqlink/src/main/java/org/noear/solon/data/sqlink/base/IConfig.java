package org.noear.solon.data.sqlink.base;

import org.noear.solon.data.sqlink.base.dataSource.DataSourceManager;
import org.noear.solon.data.sqlink.base.expression.SqlExpressionFactory;
import org.noear.solon.data.sqlink.base.session.SqlSessionFactory;
import org.noear.solon.data.sqlink.base.toBean.Include.IncludeFactory;
import org.noear.solon.data.sqlink.base.toBean.beancreator.BeanCreatorFactory;
import org.noear.solon.data.sqlink.base.transaction.TransactionManager;

public interface IConfig
{
    SqlExpressionFactory getSqlExpressionFactory();

    IDialect getDisambiguation();

    DataSourceManager getDataSourceManager();

    TransactionManager getTransactionManager();

    SqlSessionFactory getSqlSessionFactory();

    IncludeFactory getIncludeFactory();

    BeanCreatorFactory getFastCreatorFactory();

    DbType getDbType();

    boolean isPrintSql();

    boolean isPrintUseDs();

    boolean isPrintBatch();

    boolean isIgnoreDeleteNoWhere();

    boolean isIgnoreUpdateNoWhere();
}
