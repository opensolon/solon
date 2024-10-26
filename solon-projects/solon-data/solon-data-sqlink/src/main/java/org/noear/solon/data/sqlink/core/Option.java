package org.noear.solon.data.sqlink.core;

public class Option
{
    private boolean ignoreUpdateNoWhere = false;
    private boolean ignoreDeleteNoWhere = false;
    private boolean printSql = true;
    private boolean printUseDs = false;
    private boolean printBatch = false;

    public boolean isIgnoreUpdateNoWhere()
    {
        return ignoreUpdateNoWhere;
    }

    public void setIgnoreUpdateNoWhere(boolean ignoreUpdateNoWhere)
    {
        this.ignoreUpdateNoWhere = ignoreUpdateNoWhere;
    }

    public boolean isIgnoreDeleteNoWhere()
    {
        return ignoreDeleteNoWhere;
    }

    public void setIgnoreDeleteNoWhere(boolean ignoreDeleteNoWhere)
    {
        this.ignoreDeleteNoWhere = ignoreDeleteNoWhere;
    }

    public boolean isPrintSql()
    {
        return printSql;
    }

    public void setPrintSql(boolean printSql)
    {
        this.printSql = printSql;
    }

    public boolean isPrintUseDs()
    {
        return printUseDs;
    }

    public void setPrintUseDs(boolean printUseDs)
    {
        this.printUseDs = printUseDs;
    }

    public boolean isPrintBatch()
    {
        return printBatch;
    }

    public void setPrintBatch(boolean printBatch)
    {
        this.printBatch = printBatch;
    }
}
