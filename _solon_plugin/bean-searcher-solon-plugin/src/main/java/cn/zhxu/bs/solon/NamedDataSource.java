package cn.zhxu.bs.solon;

import javax.sql.DataSource;
import java.util.Objects;

/**
 * 具名数据源
 * @author Troy.Zhou @ 2017-03-20
 * @since v3.1.0
 */
public class NamedDataSource {

    private final String name;
    private final DataSource dataSource;

    public NamedDataSource(String name, DataSource dataSource) {
        this.name = Objects.requireNonNull(name);
        this.dataSource = Objects.requireNonNull(dataSource);
    }

    public String getName() {
        return name;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

}
