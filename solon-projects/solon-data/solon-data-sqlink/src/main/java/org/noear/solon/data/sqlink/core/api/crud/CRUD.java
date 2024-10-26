package org.noear.solon.data.sqlink.core.api.crud;


import org.noear.solon.data.sqlink.base.IConfig;
import org.slf4j.Logger;

public abstract class CRUD
{
    protected abstract IConfig getConfig();

    protected void tryPrintSql(Logger log, String sql)
    {
        if (getConfig().isPrintSql())
        {
            log.info("==> {}", sql);
        }
    }

    protected void tryPrintUseDs(Logger log, String ds)
    {
        if (getConfig().isPrintUseDs())
        {
            log.info("Current use datasource: {}", ds == null ? "default" : ds);
        }
    }

    protected void tryPrintBatch(Logger log, long count)
    {
        if (getConfig().isPrintBatch())
        {
            log.info("DataSize: {} Use batch execute", count);
        }
    }

    protected void tryPrintNoBatch(Logger log, long count)
    {
        if (getConfig().isPrintBatch())
        {
            log.info("DataSize: {} Use normal execute", count);
        }
    }
}
