package demo.component;

import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.ProxyComponent;
import org.noear.solon.extend.graphql.annotation.QueryMapping;

/**
 * @author fuzi1996
 * @since 2.3
 */
@Component
@ProxyComponent
public class BookService {

    @QueryMapping
    public Object bookById(String id) {
        return "bookById result";
    }
}
