package labs;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import org.noear.solon.rx.r2dbc.RxSqlUtils;

/**
 * @author noear 2024/12/13 created
 */
public class Demo {
    public static void main(String[] args) {
        ConnectionFactory connectionFactory = ConnectionFactories
                .get("r2dbc:h2:mem:///testdb");

        RxSqlUtils sqlUtils = RxSqlUtils.of(connectionFactory);

        sqlUtils.sql("SELECT firstname FROM PERSON WHERE age > ?", 42)
                .queryValue(String.class)
                .doOnNext(r -> System.out.println(r))
                .subscribe();
    }
}
