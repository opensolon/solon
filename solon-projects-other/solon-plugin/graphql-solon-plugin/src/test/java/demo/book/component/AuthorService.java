package demo.book.component;

import demo.book.dto.AuthorInputDTO;
import demo.book.dto.BookInputDTO;
import graphql.solon.annotation.SchemaMapping;
import org.noear.solon.annotation.Component;

/**
 * @author fuzi1996
 * @since 2.3
 */
@Component
public class AuthorService {

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
