package webapp.models;

/**
 * @author noear 2024/10/13 created
 */
public class Book {
    public final long bookId;
    public final String title;

    public Book(long bookId, String title) {
        this.bookId = bookId;
        this.title = title;
    }
}
