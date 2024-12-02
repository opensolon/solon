package features.serialization.protostuff.demo;

import io.protostuff.Tag;
import org.junit.jupiter.api.Test;
import org.noear.solon.serialization.protostuff.ProtostuffBytesSerializer;

/**
 * @author noear 2024/10/24 created
 */
public class DemoTest {
    @Test
    public void case1() throws Exception {
        Book book = new Book();
        book.setTitle("a");

        ///////////////////////

        ProtostuffBytesSerializer serializer = ProtostuffBytesSerializer.getInstance();
        byte[] data = serializer.serialize(book);
        System.out.println(data);

        Book book1 = (Book) serializer.deserialize(data, Book.class);

        System.out.println(book1);
        assert book.getTitle().equals(book1.getTitle());
    }

    public static class Book {
        @Tag(1) //从1开始
        private String title;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        @Override
        public String toString() {
            return "Book{" +
                    "title='" + title + '\'' +
                    '}';
        }
    }
}
