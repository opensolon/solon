package demo.component;

import demo.dto.BookInputDTO;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Param;
import graphql.solon.annotation.QueryMapping;

/**
 * @author fuzi1996
 * @since 2.3
 */
@Component
public class BookService {

    public BookService() {
    }

    private BookInputDTO generateNewOne(String id) {
        BookInputDTO book = new BookInputDTO();
        book.setId(id);
        book.setName("book-1");
        book.setPageCount(1);
        book.setAuthorId("1");
        return book;
    }

    @QueryMapping
    public BookInputDTO bookById(@Param("id") String id) {
        return this.generateNewOne(id);
    }
}
