package org.noear.solon.data.sqlink.base.expression.impl;

import org.noear.solon.data.sqlink.base.SqLinkConfig;
import org.noear.solon.data.sqlink.base.SqLinkDialect;
import org.noear.solon.data.sqlink.base.expression.ISqlDynamicColumnExpression;
import org.noear.solon.data.sqlink.base.metaData.FieldMetaData;
import org.noear.solon.data.sqlink.base.session.SqlValue;

import java.util.List;

public class SqlDynamicColumnExpression implements ISqlDynamicColumnExpression {
    private final String column;
    private String tableAsName;

    public SqlDynamicColumnExpression(String column, String tableAsName) {
        this.column = column;
        this.tableAsName = tableAsName;
    }

    @Override
    public String getColumn() {
        return column;
    }

    @Override
    public String getTableAsName() {
        return tableAsName;
    }

    @Override
    public void setTableAsName(String tableAsName) {
        this.tableAsName = tableAsName;
    }

    @Override
    public String getSqlAndValue(SqLinkConfig config, List<SqlValue> values) {
        SqLinkDialect dialect = config.getDisambiguation();
        String columnName = dialect.disambiguation(column);
        if (tableAsName != null) {
            return dialect.disambiguation(tableAsName) + "." + columnName;
        }
        else {
            return columnName;
        }
    }
}
