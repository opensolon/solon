package features;

import org.junit.jupiter.api.Test;
import org.noear.solon.data.sql.SqlBuilder;

import java.util.Arrays;

/**
 * @author noear 2024/10/9 created
 */
public class SqlTest {
    @Test
    public void case_append() {
        SqlBuilder buf = new SqlBuilder();
        buf.append("select * from user;");
        buf.append("select * from order where id=?;", 1);
        buf.appendIf(false, "select * from pay;");
        buf.appendIf(true, "select * from pay where id=?;", 2);

        System.out.println(buf.getSql());

        assert buf.getSql().split(";").length == 3;
        assert buf.getArgs().length == 2;
        assert "select * from user;select * from order where id=?;select * from pay where id=?;".equals(buf.getSql());
    }

    @Test
    public void case_iterable() {
        SqlBuilder buf = new SqlBuilder();
        buf.append("select * from user where type=? and id in(?...);", 1, Arrays.asList(1, 2, 3, 4));

        System.out.println(buf.getSql());

        assert "select * from user where type=? and id in(?,?,?,?);".equals(buf.getSql());
    }

    @Test
    public void case_iterable_err() {
        try {
            SqlBuilder buf = new SqlBuilder();
            buf.append("select * from user where id in(?);", Arrays.asList(1, 2, 3, 4));

            System.out.println(buf.getSql());
        } catch (IllegalArgumentException e) {
            assert true;
        }
    }
}