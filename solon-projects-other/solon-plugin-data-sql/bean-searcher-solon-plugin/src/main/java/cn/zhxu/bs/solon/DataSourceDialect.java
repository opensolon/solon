package cn.zhxu.bs.solon;

import cn.zhxu.bs.dialect.Dialect;

/**
 * 数据源 与 方言
 * @author Troy.Zhou @ 2023-03-111
 * @since v4.1.0
 */
public class DataSourceDialect {

    private final String dataSource;
    private final Dialect dialect;

    public DataSourceDialect(String dataSource, Dialect dialect) {
        this.dataSource = dataSource;
        this.dialect = dialect;
    }

    public String getDataSource() {
        return dataSource;
    }

    public Dialect getDialect() {
        return dialect;
    }

}
