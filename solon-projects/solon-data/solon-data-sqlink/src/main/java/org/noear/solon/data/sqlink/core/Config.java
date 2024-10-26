package org.noear.solon.data.sqlink.core;

import org.noear.solon.data.sqlink.base.transaction.TransactionManager;
import org.noear.solon.data.sqlink.base.DbType;
import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.IDialect;
import org.noear.solon.data.sqlink.base.expression.SqlExpressionFactory;
import org.noear.solon.data.sqlink.base.toBean.beancreator.BeanCreatorFactory;
import org.noear.solon.data.sqlink.base.toBean.Include.IncludeFactory;
import org.noear.solon.data.sqlink.core.dialect.*;
import org.noear.solon.data.sqlink.core.include.h2.H2IncludeFactory;
import org.noear.solon.data.sqlink.core.include.mysql.MySqlIncludeFactory;
import org.noear.solon.data.sqlink.core.include.oracle.OracleIncludeFactory;
import org.noear.solon.data.sqlink.core.include.sqlserver.SqlServerIncludeFactory;
import org.noear.solon.data.sqlink.base.dataSource.DataSourceManager;
import org.noear.solon.data.sqlink.base.session.SqlSessionFactory;
import org.noear.solon.data.sqlink.core.expression.h2.H2ExpressionFactory;
import org.noear.solon.data.sqlink.core.expression.mysql.MySqlExpressionFactory;
import org.noear.solon.data.sqlink.core.expression.oracle.OracleExpressionFactory;
import org.noear.solon.data.sqlink.core.expression.pgsql.PostgreSQLExpressionFactory;
import org.noear.solon.data.sqlink.core.expression.sqlite.SqliteExpressionFactory;
import org.noear.solon.data.sqlink.core.expression.sqlserver.SqlServerExpressionFactory;

class Config implements IConfig
{
    private final Option option;
    private final DbType dbType;
    private final IDialect disambiguation;
    private final TransactionManager transactionManager;
    private final DataSourceManager dataSourceManager;
    private final SqlSessionFactory sqlSessionFactory;
    private final SqlExpressionFactory sqlExpressionFactory;
    private final IncludeFactory includeFactory;
    private final BeanCreatorFactory beanCreatorFactory;

    public Config(Option option, DbType dbType, TransactionManager transactionManager, DataSourceManager dataSourceManager, SqlSessionFactory sqlSessionFactory, BeanCreatorFactory beanCreatorFactory)
    {
        this.option = option;
        this.dbType = dbType;
        this.beanCreatorFactory = beanCreatorFactory;

        this.disambiguation = getIDialectByDbType(dbType);
        this.sqlExpressionFactory = getSqlExpressionFactoryByDbType(dbType);
        this.includeFactory = getIncludeFactoryByDbType(dbType);

        this.transactionManager = transactionManager;
        this.dataSourceManager = dataSourceManager;
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public DataSourceManager getDataSourceManager()
    {
        return dataSourceManager;
    }

    public IDialect getDisambiguation()
    {
        return disambiguation;
    }

    public DbType getDbType()
    {
        return dbType;
    }

    public boolean isIgnoreUpdateNoWhere()
    {
        return option.isIgnoreUpdateNoWhere();
    }

    public boolean isIgnoreDeleteNoWhere()
    {
        return option.isIgnoreDeleteNoWhere();
    }

    public boolean isPrintSql()
    {
        return option.isPrintSql();
    }

    public TransactionManager getTransactionManager()
    {
        return transactionManager;
    }

    public SqlSessionFactory getSqlSessionFactory()
    {
        return sqlSessionFactory;
    }

    public boolean isPrintUseDs()
    {
        return option.isPrintUseDs();
    }

    public boolean isPrintBatch()
    {
        return option.isPrintBatch();
    }

    public SqlExpressionFactory getSqlExpressionFactory()
    {
        return sqlExpressionFactory;
    }

    public IncludeFactory getIncludeFactory()
    {
        return includeFactory;
    }

    public BeanCreatorFactory getFastCreatorFactory()
    {
        return beanCreatorFactory;
    }

    private IDialect getIDialectByDbType(DbType dbType)
    {
        switch (dbType)
        {
            case Any:
                return new DefaultDialect();
            case MySQL:
                return new MySQLDialect();
            case SQLServer:
                return new SqlServerDialect();
            case H2:
                return new H2Dialect();
            case Oracle:
                return new OracleDialect();
            case SQLite:
                return new SQLiteDialect();
            case PostgreSQL:
                return new PostgreSQLDialect();
            default:
                throw new RuntimeException(dbType.name());
        }
    }

    public SqlExpressionFactory getSqlExpressionFactoryByDbType(DbType dbType)
    {
        switch (dbType)
        {
            case Any:
            case MySQL:
                return new MySqlExpressionFactory();
            case SQLServer:
                return new SqlServerExpressionFactory();
            case H2:
                return new H2ExpressionFactory();
            case Oracle:
                return new OracleExpressionFactory();
            case SQLite:
                return new SqliteExpressionFactory();
            case PostgreSQL:
                return new PostgreSQLExpressionFactory();
            default:
                throw new RuntimeException(dbType.name());
        }
    }

    public IncludeFactory getIncludeFactoryByDbType(DbType dbType)
    {
        switch (dbType)
        {
            case Any:
            case MySQL:
            case SQLite:
            case PostgreSQL:
                return new MySqlIncludeFactory();
            case SQLServer:
                return new SqlServerIncludeFactory();
            case H2:
                return new H2IncludeFactory();
            case Oracle:
                return new OracleIncludeFactory();
            default:
                throw new RuntimeException(dbType.name());
        }
    }
}
