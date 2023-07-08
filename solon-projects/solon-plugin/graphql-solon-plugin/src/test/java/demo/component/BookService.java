package demo.component;

import demo.entity.BookEntity;
import org.noear.solon.annotation.Component;
import org.noear.solon.extend.graphql.annotation.QueryMapping;

/**
 * @author fuzi1996
 * @since 2.3
 */
@Component
public class BookService {

    public BookService() {
    }

    private BookEntity getDefaultBook() {
        BookEntity book = new BookEntity();
        book.setId("1");
        book.setName("book-1");
        book.setPageCount(1);
        book.setAuthorId("1");
        return book;
    }

    @QueryMapping
    public BookEntity bookById(String id) {
        return this.getDefaultBook();
    }
}
