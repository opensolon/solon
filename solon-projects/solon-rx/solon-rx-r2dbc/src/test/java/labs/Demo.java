package labs;

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Mono;

/**
 * @author noear 2024/12/13 created
 */
public class Demo {
    public static void main(String[] args) {
        ConnectionFactory connectionFactory = ConnectionFactories
                .get("r2dbc:h2:mem:///testdb");

        Mono.from(connectionFactory.create())
                .flatMapMany(connection -> connection
                        .createStatement("SELECT firstname FROM PERSON WHERE age > $1")
                        .bind("$1", 42)
                        .execute())
                .flatMap(result -> result
                        .map((row, rowMetadata) -> row.get("firstname", String.class)))
                .doOnNext(System.out::println)
                .subscribe();
    }
}
