package demo.component;

import demo.entity.AuthorEntity;
import org.noear.solon.annotation.Component;
import org.noear.solon.extend.graphql.annotation.QueryMapping;

/**
 * @author fuzi1996
 * @since 2.3
 */
@Component
public class AuthorService {

    public AuthorService() {
    }

    private AuthorEntity getDefaultAuthor() {
        AuthorEntity author = new AuthorEntity();
        author.setId("1");
        author.setFirstName("J");
        author.setLastName("K");
        return author;
    }

    @QueryMapping
    public AuthorEntity authorById(String id) {
        return this.getDefaultAuthor();
    }
}
