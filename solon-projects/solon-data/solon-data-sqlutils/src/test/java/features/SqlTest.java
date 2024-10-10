package features;

import org.junit.jupiter.api.Test;
import org.noear.solon.data.sql.SqlBuilder;

/**
 * @author noear 2024/10/9 created
 */
public class SqlTest {
    @Test
    public void case_append() {
        SqlBuilder builder = new SqlBuilder();
        builder.append("select * from user;");
        builder.append("select * from order where id=?;", 1);
        builder.appendIf(false, "select * from pay;");
        builder.appendIf(true, "select * from pay where id=?;", 2);

        System.out.println(builder.getSql());

        assert builder.getSql().split(";").length == 3;
        assert builder.getArgs().length == 2;
        assert "select * from user;select * from order where id=?;select * from pay where id=?;".equals(builder.getSql());
    }
}
