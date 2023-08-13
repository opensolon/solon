package demo.component;

import demo.dto.AuthorInputDTO;
import demo.dto.BookInputDTO;
import org.noear.solon.annotation.Component;
import org.noear.solon.extend.graphql.annotation.SchemaMapping;

/**
 * @author fuzi1996
 * @since 2.3
 */
@Component
public class AuthorService {

    public AuthorService() {
    }

    private AuthorInputDTO getDefaultAuthor() {
        AuthorInputDTO author = new AuthorInputDTO();
        author.setId("1");
        author.setFirstName("J");
        author.setLastName("K");
        return author;
    }

    @SchemaMapping(field = "author", typeName = "Book")
    public AuthorInputDTO authorByBookId(BookInputDTO book) {
        return this.getDefaultAuthor();
    }
}
